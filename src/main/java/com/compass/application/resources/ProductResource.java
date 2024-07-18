package com.compass.application.resources;

import com.compass.application.domain.Product;
import com.compass.application.dtos.EnableProductDTO;
import com.compass.application.dtos.ProductDTO;
import com.compass.application.dtos.UpdateStockDTO;
import com.compass.application.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@RequestMapping("v1/products")
public class ProductResource {

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody @Valid ProductDTO productDTO) {
        Product product = productService.save(productDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(product.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody @Valid ProductDTO productDTO) {
        Product product = productService.update(id, productDTO);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("status/{id}")
    public ResponseEntity<Product> disableProduct(@PathVariable Long id, @Valid @RequestBody EnableProductDTO enableProductDTO) {
        Product product = productService.updateProductStatus(id, enableProductDTO);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("stock/{id}")
    public ResponseEntity<Product> updateStock(@PathVariable Long id, @Valid @RequestBody UpdateStockDTO updateStockDTO) {
        Product product = productService.updateStock(id, updateStockDTO.quantity());
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
