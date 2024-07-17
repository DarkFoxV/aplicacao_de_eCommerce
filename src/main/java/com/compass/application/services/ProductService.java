package com.compass.application.services;

import com.compass.application.domain.Product;
import com.compass.application.domain.Stock;
import com.compass.application.dtos.EnableProductDTO;
import com.compass.application.dtos.ProductDTO;
import com.compass.application.repositories.ProductRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import com.compass.application.services.exceptions.ObjectAlreadyExistsException;
import com.compass.application.services.exceptions.ProductInSaleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockService stockService;

    @Cacheable(value = "products", key = "#root.methodName")
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Cacheable(value = "products", key = "#id")
    public Product findById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Not found Product: " + id));
    }

    @CacheEvict(value = "products", key = "'findAll'")
    public Product save(ProductDTO productDTO) {
        try {
            Product product = new Product(null, productDTO.name(), productDTO.price(), productDTO.enabled(), null);
            product = productRepository.save(product);
            stockService.save(new Stock(null, product, productDTO.quantity() != null ? productDTO.quantity() : 0));
            return product;
        } catch (DataIntegrityViolationException e) {
            throw new ObjectAlreadyExistsException("Product Already Exists on stock");
        }
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product update(Long id, ProductDTO productDTO) {
        Product product = findById(id);
        Stock stock = stockService.findById(id);
        product.setName(productDTO.name());
        product.setPrice(productDTO.price());
        product.setEnabled(productDTO.enabled());
        stock.setQuantity(productDTO.quantity() != null ? productDTO.quantity() : stock.getQuantity());
        productRepository.save(product);
        stockService.save(stock);
        return product;
    }

    @CacheEvict(value = "products", allEntries = true)
    public Product enableOrDisable(Long id, EnableProductDTO enableProductDTO) {
        Product product = findById(id);
        product.setEnabled(enableProductDTO.enabled());
        return productRepository.save(product);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ObjectNotFoundException("Not found Product: " + id);
        }

        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ProductInSaleException("Cannot delete Product with active sale");
        }
    }

}
