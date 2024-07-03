package com.compass.application.services;

import com.compass.application.domain.Product;
import com.compass.application.repositories.ProductRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found category"));
    }

    public Product save(Product category) {
        return productRepository.save(category);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }

}
