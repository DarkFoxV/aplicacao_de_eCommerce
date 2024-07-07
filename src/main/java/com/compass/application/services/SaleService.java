package com.compass.application.services;

import com.compass.application.domain.OrderItem;
import com.compass.application.domain.Sale;
import com.compass.application.dtos.SaleDTO;
import com.compass.application.repositories.SaleRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import com.compass.application.utils.ISOInstantFormatter;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public Sale findById(Long id) {
        return saleRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found sale: " + id));
    }

    public Sale save(SaleDTO saleDTO) {
        Sale sale = new Sale(null, ISOInstantFormatter.createISOInstant());

        // Create a list of OrdersItems
        List<OrderItem> itens = saleDTO.orderItems().stream().map(orderItemDTO ->
                new OrderItem(orderItemDTO.quantity(),
                        orderItemDTO.discount(),
                        productService.findById(orderItemDTO.productId()),
                        sale)).toList();

        // Associate the items with the sale and save each item
        sale.getItens().addAll(itens);
        saleRepository.save(sale);

        // Associate the items with the sale and save each item
        itens.forEach(orderItem -> {
            orderItem.getId().getSale().setId(sale.getId());
            orderItemService.save(orderItem);
            sale.getItens().add(orderItem);
        });

        return saleRepository.save(sale);
    }

    @Transactional
    public Sale updateSale(Long id, SaleDTO saleDTO) {
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
            orderItem.getId().getSale().setId(sale.getId());
            orderItemService.save(orderItem);
            sale.getItens().add(orderItem);
        });

        // save and return the new sale
        return saleRepository.save(sale);
    }

    public void delete(Long id) {
        Sale sale = findById(id);

        // Delete all items related to the sale
        Set<OrderItem> itens = sale.getItens();
        for (OrderItem orderItem : itens) {
            orderItemService.deleteById(orderItem.getId());
        }

        saleRepository.deleteById(id);
    }

}
