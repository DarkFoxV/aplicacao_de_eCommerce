package com.compass.application.services;

import com.compass.application.domain.OrderItem;
import com.compass.application.domain.Product;
import com.compass.application.domain.Sale;
import com.compass.application.domain.Stock;
import com.compass.application.dtos.OrderItemDTO;
import com.compass.application.dtos.SaleDTO;
import com.compass.application.repositories.SaleRepository;
import com.compass.application.services.exceptions.InsufficientStockException;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import com.compass.application.services.exceptions.ProductNotAvailableException;
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

    @CacheEvict(value = "sales", allEntries = true)
    public Sale save(SaleDTO saleDTO) {
        validateStock(saleDTO);
        Sale sale = new Sale();

        // Create a list of OrdersItems
        List<OrderItem> itens = saleDTO.orderItems().stream().map(orderItemDTO ->
                new OrderItem(orderItemDTO.quantity(),
                        orderItemDTO.discount(),
                        productService.findById(orderItemDTO.productId()),
                        sale)).toList();

        // Associate the items with the sale and save each item
        sale.getItens().addAll(itens);
        saleRepository.save(sale);

        // Associate the items with the sale
        itens.forEach(orderItem -> {
            sale.getItens().add(orderItem);
        });

        // Save all the items
        orderItemService.saveAll(itens);
        return saleRepository.save(sale);
    }

    @Transactional
    @CacheEvict(value = "sales", allEntries = true)
    public Sale updateSale(Long id, SaleDTO saleDTO) {
        if (!saleRepository.existsById(id)) {
            throw new ObjectNotFoundException("Not found sale: " + id);
        }

        validateStock(saleDTO);
        Sale sale = findById(id);

        // Remove all old items from the sale
        sale.getItens().forEach(orderItem -> orderItemService.deleteById(orderItem.getId()));
        sale.getItens().clear();

        // Create and add the new items to the sale
        List<OrderItem> itens = saleDTO.orderItems().stream().map(orderItemDTO ->
                        new OrderItem(orderItemDTO.quantity(),
                                orderItemDTO.discount(),
                                productService.findById(orderItemDTO.productId()),
                                sale))
                .toList();

        // Associate the new items with the sale and save each item
        itens.forEach(orderItem -> {
            orderItemService.save(orderItem);
            sale.getItens().add(orderItem);
        });

        //return the new sale
        return sale;
    }

    @CacheEvict(value = "sales", allEntries = true)
    public void delete(Long id) {
        Sale sale = findById(id);

        // Delete all items related to the sale
        Set<OrderItem> itens = sale.getItens();
        for (OrderItem orderItem : itens) {
            orderItemService.deleteById(orderItem.getId());
        }

        saleRepository.deleteById(id);
    }

    private void validateStock(SaleDTO saleDTO) {
        for (OrderItemDTO orderItemDTO : saleDTO.orderItems()) {
            Product product = productService.findById(orderItemDTO.productId());

            if(!product.getEnabled()){
                throw new ProductNotAvailableException("Product disabled: " + orderItemDTO.productId());
            }

            Stock stock = stockService.findById(orderItemDTO.productId());
            int availableStock = stock.getQuantity();

            if (orderItemDTO.quantity() > availableStock) {
                throw new InsufficientStockException("Not enough stock for product ID: " + orderItemDTO.productId());
            }

            stock.setQuantity(stock.getQuantity() - orderItemDTO.quantity());
            stockService.save(stock);
        }
    }

}
