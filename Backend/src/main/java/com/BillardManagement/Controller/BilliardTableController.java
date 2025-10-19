package com.BillardManagement.Controller;

import com.BillardManagement.DTO.Request.BilliardTableRequest;
import com.BillardManagement.DTO.Response.BilliardTableResponse;
import com.BillardManagement.Service.BilliardTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/tables")
public class BilliardTableController {

    @Autowired
    private BilliardTableService tableService;

    @GetMapping
    public ResponseEntity<List<BilliardTableResponse>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BilliardTableResponse> getTableById(@PathVariable Integer id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }
    @GetMapping("/customer/{customerId}")
    public List<BilliardTableResponse> getTablesByCustomer(@PathVariable Integer customerId) {
        return tableService.getTablesByCustomer(customerId);
    }

    @PostMapping
    public ResponseEntity<BilliardTableResponse> addTable(@RequestBody BilliardTableRequest req) {
        return ResponseEntity.ok(tableService.addTable(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BilliardTableResponse> updateTable(@PathVariable Integer id, @RequestBody BilliardTableRequest req) {
        return ResponseEntity.ok(tableService.updateTable(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Integer id) {
        tableService.deleteTable(id);
        return ResponseEntity.noContent().build();
    }
}
