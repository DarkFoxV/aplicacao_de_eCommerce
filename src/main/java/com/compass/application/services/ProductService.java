package com.compass.application.services;

import com.compass.application.domain.Product;
import com.compass.application.domain.Stock;
import com.compass.application.repositories.ProductRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import com.compass.application.services.exceptions.ProductInSaleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockService stockService;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found Product: " + id));
    }

    public Product findByName(String name) {
        return productRepository.findByName(name);
    }

    public Product save(Product product) {
        product = productRepository.save(product);
        stockService.save(new Stock(null,product,0));
        return product;
    }

    public void delete(Long id) {
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ProductInSaleException("Cannot delete Product with active sale");
        }
    }

}
