package com.compass.application.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Sale implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer quantity;
    private final List<OrderItem> itens = new ArrayList<>();

}
