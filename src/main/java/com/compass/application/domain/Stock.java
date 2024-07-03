package com.compass.application.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Stock implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Product product;
    private Integer quantity;

}
