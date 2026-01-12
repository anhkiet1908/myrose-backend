package com.letruonganhkiet.example.controllers;

import com.letruonganhkiet.example.entity.TableEntity;
import com.letruonganhkiet.example.security.services.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
public class TableController {

    @Autowired
    private TableService tableService;

    // GET: ai cũng xem được
    @GetMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public List<TableEntity> getAll() {
        return tableService.getAll();
    }

    // ✅ GET theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public TableEntity getById(@PathVariable Long id) {
        return tableService.findById(id);
    }

    // POST: chỉ nhân viên và admin
    @PostMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public TableEntity create(@RequestBody TableEntity table) {
        return tableService.create(table);
    }

    // PUT: chỉ nhân viên và admin
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public TableEntity update(@PathVariable Long id, @RequestBody TableEntity table) {
        return tableService.update(id, table);
    }

    // DELETE: chỉ admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        tableService.delete(id);
    }

    // ✅ PUT: cập nhật trạng thái bàn
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public TableEntity updateStatus(@PathVariable Long id, @RequestParam String status) {
        return tableService.updateStatus(id, status);
    }
}