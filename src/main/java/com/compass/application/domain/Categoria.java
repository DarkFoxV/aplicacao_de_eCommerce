package com.compass.application.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Categoria implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
}
