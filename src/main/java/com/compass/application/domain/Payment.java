package com.compass.application.domain;

import com.compass.application.domain.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;
    private PaymentStatus paymentStatus;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "sale_id")
    @MapsId
    private Sale sale;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment pagamento = (Payment) o;
        return Objects.equals(id, pagamento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
