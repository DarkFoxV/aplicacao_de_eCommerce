package com.compass.application.repositories;

import com.compass.application.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findByDateBetween(Instant startDate, Instant endDate);
}
