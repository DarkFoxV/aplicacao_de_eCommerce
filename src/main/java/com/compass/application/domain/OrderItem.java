package com.compass.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private OrderItemPK id = new OrderItemPK();
    private Integer quantity;
    private Double discount;

    public OrderItem(Integer quantidade, Double desconto, Product product, Sale sale) {
        this.quantity = quantidade;
        this.discount = desconto;
        this.id.setProduct(product);
        this.id.setSale(sale);
    }
}
