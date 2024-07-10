package com.compass.application.dtos;

import java.time.LocalDate;
import java.util.List;

public record ReportDTO(
        LocalDate startDate,
        LocalDate endDate,
        int totalSales,
        List<ReportProductDTO> productsSold
) {}