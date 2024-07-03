package com.compass.application.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class ItensPedido implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ItemPedidoPK id = new ItemPedidoPK();
    private Integer quantidade;
    private Double desconto;

    public ItensPedido(Integer quantidade, Double desconto, Produto produto, Venda venda) {
        this.quantidade = quantidade;
        this.desconto = desconto;
        this.id.setProduto(produto);
        this.id.setVenda(venda);
    }
}
