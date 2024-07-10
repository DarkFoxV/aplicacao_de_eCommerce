package com.compass.application.resources;

import com.compass.application.dtos.ReportDTO;
import com.compass.application.services.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("v1/reports")
public class ReportResource {

    @Autowired
    private ReportService reportService;

    @GetMapping("/weekly")
    public ResponseEntity<ReportDTO> getWeeklyReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ReportDTO report = reportService.generateWeeklyReport(date);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/monthly")
    public ResponseEntity<ReportDTO> getMonthlyReport(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        ReportDTO report = reportService.generateMonthlyReport(date);
        return ResponseEntity.ok(report);
    }

}
