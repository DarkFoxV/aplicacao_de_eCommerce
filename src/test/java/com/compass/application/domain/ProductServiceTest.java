package com.compass.application.domain;

import com.compass.application.repositories.ProductRepository;
import com.compass.application.services.ProductService;
import com.compass.application.services.StockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.compass.application.common.ProductConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @InjectMocks
    private ProductService productService;

    @Mock
    private StockService stockService;

    @Mock
    private ProductRepository productRepository;

    @Test
    public void createProduct_WithValidData_ReturnsProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(RTX_4090);
        when(stockService.save(any(Stock.class))).thenReturn(new Stock());

        Product sut = productService.save(RTX_4090_DTO);

        assertThat(sut).isEqualTo(RTX_4090);
    }

    @Test
    public void createProduct_WithInvalidData_ThrowsException() {
        when(productRepository.save(any(Product.class))).thenThrow(RuntimeException.class);

        assertThatThrownBy(() -> productService.save(INVALID_PRODUCT_DTO)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getProduct_ByExistingId_ReturnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(RTX_4090));

        Product sut = productService.findById(1L);

        assertThat(sut).isNotNull();
        assertThat(sut).isEqualTo(RTX_4090);
    }

    @Test
    public void getProduct_ByUnexistingId_ReturnsEmpty() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> productService.findById(1L)).isInstanceOf(RuntimeException.class);
    }


    @Test
    public void listProducts_ReturnsAllProducts() {
        List<Product> products = new ArrayList<>() {
            {
                add(RTX_4090);
            }
        };
        when(productRepository.findAll()).thenReturn(products);

        List<Product> sut = productService.findAll();

        assertThat(sut).isNotEmpty();
        assertThat(sut).hasSize(1);
        assertThat(sut.get(0)).isEqualTo(RTX_4090);
    }

    @Test
    public void listProducts_ReturnsNoProducts() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<Product> sut = productService.findAll();

        assertThat(sut).isEmpty();
    }

    @Test
    public void removeProduct_WithExistingId_doesNotThrowAnyException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(RTX_4090));
        when(stockService.findById(anyLong())).thenReturn(new Stock()); // Mock stock retrieval if necessary

        assertThatCode(() -> productService.delete(1L)).doesNotThrowAnyException();
    }

    @Test
    public void removeProduct_WithUnexistingId_ThrowsException() {
        assertThatThrownBy(() -> productService.delete(99L)).isInstanceOf(RuntimeException.class);
    }
}
