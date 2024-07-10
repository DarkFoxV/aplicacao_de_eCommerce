package com.compass.application.services;

import com.compass.application.domain.OrderItem;
import com.compass.application.domain.Sale;
import com.compass.application.dtos.ReportDTO;
import com.compass.application.dtos.ReportProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private SaleService saleService;

    public ReportDTO generateMonthlyReport(LocalDate date) {
        // If date is null, set it to the current date
        if (date == null) {
            date = LocalDate.now();
        }

        // Retrieve sales for the specified date
        List<Sale> sales = saleService.findSalesInDateRange(date, date);

        // Flatten order items from sales into a list
        List<OrderItem> itens = sales
                .stream()
                .flatMap(sale -> sale.getItens().stream())
                .toList();

        // Create a list of ReportProductDTO from order items
        List<ReportProductDTO> products = new ArrayList<>(createProductList(itens).values());

        // Create and return a ReportDTO
        return new ReportDTO(date, date, sales.size(), products);
    }

    public ReportDTO generateWeeklyReport(LocalDate startDate) {
        // If startDate is null, set it to the start of the current week (Sunday)
        if (startDate == null) {
            startDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        } else {
            startDate = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        }

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

