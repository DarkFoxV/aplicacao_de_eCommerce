package com.compass.application.config;

import com.compass.application.domain.*;
import com.compass.application.domain.enums.PaymentStatus;
import com.compass.application.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

@Configuration
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private StockRepository stockRepository;

    @Override
    public void run(String... args) throws Exception {
        Random random = new Random ();

        Product prod1 = new Product(null, "Computador", 2000.0, true, Instant.now().minus(Duration.ofDays(random.nextInt(7,21))));
        Product prod2 = new Product(null, "Impressora", 800.0, true, Instant.now().minus(Duration.ofDays(random.nextInt(7,21))));
        Product prod3 = new Product(null, "Mouse", 80.0, true, Instant.now().minus(Duration.ofDays(random.nextInt(7,21))));
        Product prod4 = new Product(null, "Teclado", 50.0, true, Instant.now().minus(Duration.ofDays(random.nextInt(7,21))));
        Product prod5 = new Product(null, "Monitor", 1500.0, true, Instant.now().minus(Duration.ofDays(random.nextInt(7,21))));
        Product prod6 = new Product(null, "Mouse Gaming", 120.0, true, Instant.now().minus(Duration.ofDays(random.nextInt(7,21))));

        productRepository.saveAll(Arrays.asList(prod1, prod2, prod3, prod4, prod5, prod6));
        Stock stock1 = new Stock (prod1.getId(), prod1, 10);
        Stock stock2 = new Stock (prod2.getId(), prod2, 10);
        Stock stock3 = new Stock (prod3.getId(), prod3, 10);
        Stock stock4 = new Stock (prod4.getId(), prod4, 10);
        Stock stock5 = new Stock (prod5.getId(), prod5, 10);
        Stock stock6 = new Stock (prod6.getId(), prod6, 10);
        stockRepository.saveAll(Arrays.asList(stock1, stock2, stock3, stock4, stock5, stock6));

        Sale s1 = new Sale();
        Payment payment1 = new Payment (null, PaymentStatus.PAID, s1);
        s1.setPayment(payment1);
        saleRepository.save(s1);

        OrderItem orderItem1 = new OrderItem(2, 0.0, prod6, s1);
        OrderItem orderItem2 = new OrderItem(2, 0.0, prod4, s1);
        orderItemRepository.saveAll(Arrays.asList(orderItem1, orderItem2));

        s1.getItens().addAll(Arrays.asList(orderItem1, orderItem2));

    }
}
