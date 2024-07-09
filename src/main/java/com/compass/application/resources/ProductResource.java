package com.compass.application.resources;

import com.compass.application.domain.Product;
import com.compass.application.dtos.EnableProductDTO;
import com.compass.application.dtos.ProductDTO;
import com.compass.application.services.ProductService;
import com.compass.application.validations.ValidBoolean;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("v1/products")
public class ProductResource {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> listAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findProductById(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        if (productService.findByName(productDTO.name()) != null) {
            return ResponseEntity.status(409).build();
        }

        Product product = new Product(null, productDTO.name(), productDTO.price(), productDTO.enabled(), null);
        productService.save(product);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO productDTO) {
        Product product = productService.findById(id);

        // update product attributes
        product.setName(productDTO.name());
        product.setPrice(productDTO.price());
        product.setEnabled(productDTO.enabled());

        productService.save(product);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("status/{id}")
    public ResponseEntity<Product> disableProduct(@PathVariable Long id, @Valid @RequestBody EnableProductDTO enableProductDTO) {
        Product product = productService.findById(id);
        product.setEnabled(enableProductDTO.enabled());
        productService.save(product);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.findById(id);

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
