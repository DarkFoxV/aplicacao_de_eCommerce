package com.compass.application.resources;

import com.compass.application.domain.Sale;
import com.compass.application.dtos.OrderItemDTO;
import com.compass.application.dtos.SaleDTO;
import com.compass.application.security.TokenService;
import com.compass.application.services.OrderItemService;
import com.compass.application.services.ProductService;
import com.compass.application.services.SaleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("v1/sales")
public class SaleResource {

    @Autowired
    private SaleService saleService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<List<Sale>> findAllSales() {
        List<Sale> list = saleService.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sale> findSaleById(@PathVariable Long id) {
        Sale sale = saleService.findById(id);
        return ResponseEntity.ok(sale);
    }

    @GetMapping("/date")
    public ResponseEntity<List<Sale>> getSalesInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Sale> sales = saleService.findSalesInDateRange(startDate, endDate);
        return ResponseEntity.ok(sales);
    }

    @PostMapping
    public ResponseEntity<Sale> createSale(HttpServletRequest request, @RequestBody @Valid SaleDTO saleDTO) {
        String token = tokenService.recoverToken(request);
        String email = tokenService.validateToken(token);
        Sale sale = saleService.save(saleDTO, email);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(sale.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PostMapping("/{saleId}/items")
    public ResponseEntity<Sale> addItemToSale(@PathVariable Long saleId, @Valid @RequestBody OrderItemDTO orderItemDTO) {
        Sale sale = saleService.addItemToSale(saleId, orderItemDTO);
        return ResponseEntity.ok(sale);
    }

    @PatchMapping("/{saleId}/items")
    public ResponseEntity<Sale> updateItemInSale(@PathVariable Long saleId, @Valid @RequestBody OrderItemDTO orderItemDTO) {
        Sale sale = saleService.updateItemInSale(saleId, orderItemDTO);
        return ResponseEntity.ok(sale);
    }

    @DeleteMapping("/{saleId}/items/{productId}")
    public ResponseEntity<Sale> removeItemFromSale(@PathVariable Long saleId, @PathVariable Long productId) {
        Sale sale = saleService.removeItemFromSale(saleId, productId);
        return ResponseEntity.ok(sale);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSale(@PathVariable Long id) {
        saleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
