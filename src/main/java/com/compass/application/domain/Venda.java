package com.compass.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Venda implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Integer quantidade;
    private final List<ItensPedido> itens = new ArrayList<>();

}
