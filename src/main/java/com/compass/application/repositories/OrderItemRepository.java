package com.compass.application.repositories;

import com.compass.application.domain.OrderItem;
import com.compass.application.domain.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    void deleteById(OrderItemPK id);
}
