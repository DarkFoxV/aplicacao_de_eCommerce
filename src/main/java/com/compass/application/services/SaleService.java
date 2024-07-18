package com.compass.application.services;

import com.compass.application.domain.*;
import com.compass.application.domain.enums.PaymentStatus;
import com.compass.application.dtos.OrderItemDTO;
import com.compass.application.dtos.SaleDTO;
import com.compass.application.repositories.SaleRepository;
import com.compass.application.services.exceptions.InsufficientStockException;
import com.compass.application.services.exceptions.ObjectAlreadyExistsException;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import com.compass.application.services.exceptions.ObjectNotAvailableException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private ProductService productService;

    @Autowired
    private StockService stockService;

    @Autowired
    private UserService userService;

    @Cacheable(value = "sales", key = "#root.methodName")
    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public List<Sale> findSalesInDateRange(LocalDate startDate, LocalDate endDate) {
        Instant startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endInstant = endDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC).minusSeconds(1);
        return saleRepository.findByDateBetween(startInstant, endInstant);
    }

    @Cacheable(value = "sales", key = "#id")
    public Sale findById(Long id) {
        return saleRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found sale: " + id));
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale save(SaleDTO saleDTO, String email) {
        User user = userService.findByEmail(email);
        validateStock(saleDTO.orderItems());
        Sale sale = new Sale(null, user, null, null);

        Payment payment = new Payment(null, PaymentStatus.PENDING, sale);
        sale.setPayment(payment);
        saleRepository.save(sale);

        // Create a list of OrdersItems
        List<OrderItem> itens = saleDTO.orderItems().stream().map(orderItemDTO ->
                new OrderItem(orderItemDTO.quantity(),
                        orderItemDTO.discount(),
                        productService.findById(orderItemDTO.productId()),
                        sale)).toList();

        // Associate the items with sale
        sale.getItens().addAll(itens);

        // Save all the items
        orderItemService.saveAll(itens);
        return sale;
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale addItemToSale(Long saleId, OrderItemDTO orderItemDTO) {
        Sale sale = findById(saleId);

        if (!sale.getPayment().getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new ObjectNotAvailableException("Unable to modify finalized sales.");
        }

        Product product = productService.findById(orderItemDTO.productId());

        OrderItemPK pk = new OrderItemPK();
        pk.setProduct(product);
        pk.setSale(sale);

        boolean alreadyExists = sale.getItens().stream().anyMatch(item -> item.getId().equals(pk));

        if(alreadyExists) {
            throw new ObjectAlreadyExistsException("Item already exists in sale: " + orderItemDTO.productId());
        }

        if (!product.getEnabled()) {
            throw new ObjectNotAvailableException("Product disabled: " + orderItemDTO.productId());
        }

        Stock stock = product.getStock();

        // ensure sufficient stock
        if (stock.getQuantity() < orderItemDTO.quantity()) {
            throw new InsufficientStockException("Insufficient stock for product: " + orderItemDTO.productId());
        }

        OrderItem orderItem = new OrderItem(orderItemDTO.quantity(), orderItemDTO.discount(), product, sale);
        orderItemService.save(orderItem);

        sale.getItens().add(orderItem);

        // update stock
        stock.setQuantity(stock.getQuantity() - orderItemDTO.quantity());
        stockService.save(stock);

        return sale;
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale removeItemFromSale(Long saleId, Long productId) {
        Sale sale = findById(saleId);

        if (!sale.getPayment().getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new ObjectNotAvailableException("Unable to modify finalized sales.");
        }

        Product product = productService.findById(productId);
        Stock stock = product.getStock();

        OrderItemPK pk = new OrderItemPK();
        pk.setProduct(product);
        pk.setSale(sale);

        OrderItem orderItem = sale.getItens().stream()
                .filter(item -> item.getId().equals(pk))
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Item not found in sale: " + productId));

        sale.getItens().remove(orderItem);

        int quantity = orderItem.getQuantity();

        orderItemService.deleteById(pk);

        // Restore the stock
        stock.setQuantity(stock.getQuantity() + quantity);
        stockService.save(stock);

        return sale;
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale updateItemInSale(Long saleId, OrderItemDTO orderItemDTO) {
        Sale sale = findById(saleId);

        if (!sale.getPayment().getPaymentStatus().equals(PaymentStatus.PENDING)) {
            throw new ObjectNotAvailableException("Unable to modify finalized sales.");
        }

        Product product = productService.findById(orderItemDTO.productId());

        if (!product.getEnabled()) {
            throw new ObjectNotAvailableException("Product disabled: " + orderItemDTO.productId());
        }

        Stock stock = product.getStock();
        OrderItemPK pk = new OrderItemPK();
        pk.setProduct(product);
        pk.setSale(sale);

        OrderItem orderItem = sale.getItens().stream()
                .filter(item -> item.getId().equals(pk))
                .findFirst()
                .orElseThrow(() -> new ObjectNotFoundException("Item not found in sale: " + orderItemDTO.productId()));

        // Check the difference in quantity and ensure sufficient stock
        int currentQuantity = orderItem.getQuantity();
        int newQuantity = orderItemDTO.quantity();
        int quantityDifference = newQuantity - currentQuantity;

        if (quantityDifference > 0 && stock.getQuantity() < quantityDifference) {
            throw new InsufficientStockException("Insufficient stock for product: " + orderItemDTO.productId());
        }

        orderItem.setQuantity(orderItemDTO.quantity());
        orderItem.setDiscount(orderItemDTO.discount());
        orderItemService.save(orderItem);

        // update stock
        stock.setQuantity(stock.getQuantity() - quantityDifference);
        stockService.save(stock);

        return sale;
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale updatePaymentStatus(Long saleId) {
        Sale sale = findById(saleId);
        sale.getPayment().setPaymentStatus(PaymentStatus.PAID);
        saleRepository.save(sale);
        return sale;
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public void delete(Long id) {
        Sale sale = findById(id);

        // Delete all items related to the sale
        Set<OrderItem> itens = sale.getItens();
        for (OrderItem orderItem : itens) {
            Stock stock = orderItem.getId().getProduct().getStock();
            stock.setQuantity(stock.getQuantity() + orderItem.getQuantity());
            stockService.save(stock);
            orderItemService.deleteById(orderItem.getId());
        }

        saleRepository.deleteById(id);
    }

    private void validateStock(List<OrderItemDTO> orderItemDTO) {
        for (OrderItemDTO item : orderItemDTO) {
            Product product = productService.findById(item.productId());

            if (!product.getEnabled()) {
                throw new ObjectNotAvailableException("Product disabled: " + item.productId());
            }

            Stock stock = product.getStock();
            int availableStock = stock.getQuantity();

            if (item.quantity() > availableStock) {
                throw new InsufficientStockException("Not enough stock for product ID: " + item.productId());
            }

            stock.setQuantity(stock.getQuantity() - item.quantity());
            stockService.save(stock);
        }
    }

}
