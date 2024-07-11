package com.compass.application.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class OrderItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    @JsonIgnore
    private OrderItemPK id;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double discount;

    @Column(nullable = false)
    private Double TotalValue;

    public OrderItem(Integer quantidade, Double desconto, Product product, Sale sale) {
        this.id = new OrderItemPK();
        this.id.setProduct(product);
        this.id.setSale(sale);
        this.productName = product.getName();
        this.quantity = quantidade;
        this.discount = desconto;
        this.TotalValue = (product.getPrice() * (1 - (desconto / 100))) * quantidade;
    }

    @JsonIgnore
    public Sale getSale() {
        return id.getSale();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(getId(), orderItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

}
