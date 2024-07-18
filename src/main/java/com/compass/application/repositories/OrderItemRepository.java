package com.compass.application.repositories;

import com.compass.application.domain.OrderItem;
import com.compass.application.domain.OrderItemPK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    boolean existsById(OrderItemPK pk);

    Optional<OrderItem> findById(OrderItemPK pk);

    void deleteById(OrderItemPK id);
}
