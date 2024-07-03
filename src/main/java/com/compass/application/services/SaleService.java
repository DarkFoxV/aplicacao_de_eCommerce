package com.compass.application.services;

import com.compass.application.domain.Sale;
import com.compass.application.repositories.SaleRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public Sale findById(Long id) {
        return saleRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found category"));
    }

    public Sale save(Sale category) {
        return saleRepository.save(category);
    }

    public void delete(Long id) {
        saleRepository.deleteById(id);
    }

}
