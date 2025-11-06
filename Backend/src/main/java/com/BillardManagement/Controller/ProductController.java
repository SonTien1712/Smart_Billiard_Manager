package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.ProductRequestDTO;
import com.BillardManagement.DTO.Response.ProductResponseDTO;
import com.BillardManagement.Entity.Customer; // Import Customer
import com.BillardManagement.Service.CustomerService; // Import CustomerService
import com.BillardManagement.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CustomerService customerService; // Đã inject

    // Các hàm GET không cần customerId, giữ nguyên
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts(
            @RequestParam Integer clubId,
            @RequestParam(required = false) Boolean activeOnly) {

        List<ProductResponseDTO> products = productService.getAllProductsByClub(clubId, activeOnly);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(
            @PathVariable Integer id,
            @RequestParam Integer clubId) {

        ProductResponseDTO product = productService.getProductById(id, clubId);
        return ResponseEntity.ok(product);
    }

    // Sửa lại hàm create
    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @Valid @RequestBody ProductRequestDTO request
            /* Xóa @RequestHeader */
    ) {
        // Lấy customerId từ token
        Customer customer = customerService.getCurrentUser();
        Integer customerId = customer.getId();

        ProductResponseDTO product = productService.createProduct(request, customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    // Sửa lại hàm update
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequestDTO request
            /* Xóa @RequestHeader */
    ) {
        // Lấy customerId từ token
        Customer customer = customerService.getCurrentUser();
        Integer customerId = customer.getId();

        ProductResponseDTO product = productService.updateProduct(id, request, customerId);
        return ResponseEntity.ok(product);
    }

    // Sửa lại hàm delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Integer id
            /* Xóa @RequestHeader */
    ) {
        // Lấy customerId từ token
        Customer customer = customerService.getCurrentUser();
        Integer customerId = customer.getId();

        productService.deleteProduct(id, customerId);
        return ResponseEntity.noContent().build();
    }

    // Sửa lại hàm toggle-status (nguồn gây lỗi)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ProductResponseDTO> toggleProductStatus(
            @PathVariable Integer id
            /* Xóa @RequestHeader */
    ) {
        // Lấy customerId từ token
        Customer customer = customerService.getCurrentUser();
        Integer customerId = customer.getId();

        ProductResponseDTO product = productService.toggleProductStatus(id, customerId);
        return ResponseEntity.ok(product);
    }

    // Hàm search không cần customerId, giữ nguyên
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDTO>> searchProducts(
            @RequestParam Integer clubId,
            @RequestParam String keyword) {

        List<ProductResponseDTO> products = productService.searchProducts(clubId, keyword);
        return ResponseEntity.ok(products);
    }
}