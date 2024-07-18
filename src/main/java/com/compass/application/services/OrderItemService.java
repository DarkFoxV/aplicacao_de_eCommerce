package com.compass.application.services;

import com.compass.application.domain.OrderItem;
import com.compass.application.domain.OrderItemPK;
import com.compass.application.repositories.OrderItemRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> findAll() {
        return orderItemRepository.findAll();
    }

    public OrderItem findById(OrderItemPK id) {
        return orderItemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found orderItem: " + id));
    }

    public OrderItem save(OrderItem category) {
        return orderItemRepository.save(category);
    }

    public List<OrderItem> saveAll(List<OrderItem> items) {
        return orderItemRepository.saveAll(items);
    }

    public void deleteById(OrderItemPK id) {
        if (!orderItemRepository.existsById(id)) {
            throw new ObjectNotFoundException("Not found OrderItem:" + id);
        }
        orderItemRepository.deleteById(id);
    }

}
