package com.compass.application.services;

import com.compass.application.domain.Product;
import com.compass.application.domain.Stock;
import com.compass.application.dtos.EnableProductDTO;
import com.compass.application.dtos.ProductDTO;
import com.compass.application.repositories.ProductRepository;
import com.compass.application.services.exceptions.ObjectNotFoundException;
import com.compass.application.services.exceptions.ProductAlreadyExistsException;
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

    public Product save(ProductDTO productDTO) {
        try {
            Product product = new Product(null, productDTO.name(), productDTO.price(), productDTO.enabled(), null);
            product = productRepository.save(product);
            stockService.save(new Stock(null, product, productDTO.quantity() != null ? productDTO.quantity() : 0));
            return product;
        } catch (DataIntegrityViolationException e){
            throw new ProductAlreadyExistsException("Product Already Exists on stock");
        }
    }

    public Product update(Long id, ProductDTO productDTO) {
        Product product = findById(id);
        Stock stock = stockService.findById(productDTO.id());
        product.setName(productDTO.name());
        product.setPrice(productDTO.price());
        product.setEnabled(productDTO.enabled());
        stock.setQuantity(productDTO.quantity() != null ? productDTO.quantity() : stock.getQuantity());
        productRepository.save(product);
        stockService.save(stock);
        return product;
    }

    // if product is already enabled then update to disabled
    public Product enableOrDisable(Long id, EnableProductDTO enableProductDTO) {
        Product product = findById(id);
        product.setEnabled(true);
        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = findById(id);
        try {
            productRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ProductInSaleException("Cannot delete Product with active sale");
        }
    }

}
