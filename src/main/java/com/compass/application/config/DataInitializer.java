package com.compass.application.config;

import com.compass.application.domain.*;
import com.compass.application.domain.enums.PaymentStatus;
import com.compass.application.domain.enums.UserRoles;
import com.compass.application.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

@Configuration
@Profile("dev")
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

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        String encryptedPassword = new BCryptPasswordEncoder().encode("12345@Mi");
        User user = userRepository.save(new User(null,"Michel Pereira","virtualmachinevx@gmail.com",encryptedPassword, UserRoles.ADMIN));

        Product prod1 = new Product(null, "Computador", 2000.0, true, null,null);
        Product prod2 = new Product(null, "Impressora", 800.0, true, null,null);
        Product prod3 = new Product(null, "Mouse", 80.0, true, null,null);
        Product prod4 = new Product(null, "Teclado", 50.0, true, null,null);
        Product prod5 = new Product(null, "Monitor", 1500.0, true, null,null);
        Product prod6 = new Product(null, "Mouse Gaming", 120.0, true, null,null);

        productRepository.saveAll(Arrays.asList(prod1, prod2, prod3, prod4, prod5, prod6));
        Stock stock1 = new Stock (null, prod1, 10);
        Stock stock2 = new Stock (null, prod2, 10);
        Stock stock3 = new Stock (null, prod3, 10);
        Stock stock4 = new Stock (null, prod4, 10);
        Stock stock5 = new Stock (null, prod5, 10);
        Stock stock6 = new Stock (null, prod6, 10);
        stockRepository.saveAll(Arrays.asList(stock1, stock2, stock3, stock4, stock5, stock6));

        Sale s1 = new Sale(null, user,null,null);
        Payment payment1 = new Payment (null, PaymentStatus.PAID, s1);
        s1.setPayment(payment1);
        saleRepository.save(s1);

        OrderItem orderItem1 = new OrderItem(2, 0.0, prod6, s1);
        OrderItem orderItem2 = new OrderItem(2, 0.0, prod4, s1);
        orderItemRepository.saveAll(Arrays.asList(orderItem1, orderItem2));

        s1.getItens().addAll(Arrays.asList(orderItem1, orderItem2));

    }
}
