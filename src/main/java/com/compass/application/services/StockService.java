package com.compass.application.services;

import com.compass.application.domain.Stock;
import com.compass.application.repositories.StockRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public List<Stock> findAll() {
        return stockRepository.findAll();
    }

    public Stock findById(Long id) {
        return stockRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found stock: " + id));
    }

    public Stock findByProductId(Long id) {
        return stockRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found stock for product: " + id));
    }

    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    public void delete(Long id) {
        stockRepository.deleteById(id);
    }

}
