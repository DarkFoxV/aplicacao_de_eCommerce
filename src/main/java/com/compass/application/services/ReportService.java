package com.compass.application.services;

import com.compass.application.domain.OrderItem;
import com.compass.application.domain.Sale;
import com.compass.application.dtos.ReportDTO;
import com.compass.application.dtos.ReportProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private SaleService saleService;

    @Cacheable(value = "monthlyReports", key = "#root.method.name + ':' + #date.withDayOfMonth(1) + '-' + #date.withDayOfMonth(#date.lengthOfMonth())",
            condition = "#date != null && #date.withDayOfMonth(1).isBefore(T(java.time.LocalDate).now().withDayOfMonth(1))")
    public ReportDTO generateMonthlyReport(LocalDate date) {
        // If date is null, set it to the current date
        if (date == null) {
            date = LocalDate.now();
        }

        // Determine the first and last day of the month
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        // Retrieve sales for the specified date
        List<Sale> sales = saleService.findSalesInDateRange(firstDayOfMonth, lastDayOfMonth);

        // Flatten order items from sales into a list
        List<OrderItem> itens = sales
                .stream()
                .flatMap(sale -> sale.getItens().stream())
                .toList();

        // Create a list of ReportProductDTO from order items
        List<ReportProductDTO> products = new ArrayList<>(createProductList(itens).values());

        // Create and return a ReportDTO
        return new ReportDTO(firstDayOfMonth, lastDayOfMonth, sales.size(), products);
    }
    public ReportDTO generateWeeklyReport(LocalDate startDate) {
        // If startDate is null, set it to the start of the current week (Sunday)
        startDate = Objects.requireNonNullElseGet(startDate, LocalDate::now).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        // Define endDate as the seventh day after startDate (end of the week)
        LocalDate endDate = startDate.plusDays(6);

        // Retrieve sales for the date range from startDate to endDate
        List<Sale> sales = saleService.findSalesInDateRange(startDate, endDate);

        // Flatten order items from sales into a list
        List<OrderItem> itens = sales.stream()
                .flatMap(sale -> sale.getItens().stream())
                .toList();

        // Create a list of ReportProductDTO from order items
        List<ReportProductDTO> products = new ArrayList<>(createProductList(itens).values());

        // Create and return a ReportDTO
        return new ReportDTO(startDate, endDate, sales.size(), products);
    }

    // Private method to create a map of product IDs to ReportProductDTO
    private Map<Long, ReportProductDTO> createProductList(List<OrderItem> items) {
        Map<Long, ReportProductDTO> productMap = new HashMap<>();
        for (OrderItem item : items) {
            Long productId = item.getId().getProduct().getId();
            ReportProductDTO productDTO = productMap.computeIfAbsent(productId, id -> new ReportProductDTO(
                    productId,
                    item.getProductName(),
                    0
            ));
            productDTO.setQuantity(productDTO.getQuantity() + item.getQuantity());
        }
        return productMap;
    }

}

