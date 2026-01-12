package com.letruonganhkiet.example.security.services;

import java.time.LocalDateTime;
import java.util.List;

import com.letruonganhkiet.example.entity.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.letruonganhkiet.example.entity.TableEntity;
import com.letruonganhkiet.example.repository.TableRepository;

@Service
public class TableService {

    @Autowired
    private TableRepository tableRepo;

    public List<TableEntity> getAll() {
        return tableRepo.findAll();
    }

    public TableEntity findById(Long id) {
        return tableRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bàn với ID: " + id));
    }

    public TableEntity create(TableEntity table) {
        table.setCreatedAt(LocalDateTime.now());
        table.setUpdatedAt(LocalDateTime.now());
        return tableRepo.save(table);
    }

    public TableEntity update(Long id, TableEntity table) {
        TableEntity existing = findById(id);
        existing.setNumber(table.getNumber());
        existing.setCapacity(table.getCapacity());
        existing.setStatus(table.getStatus());
        existing.setUpdatedAt(LocalDateTime.now());
        return tableRepo.save(existing);
    }

    public void delete(Long id) {
        if (!tableRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy bàn để xóa");
        }
        tableRepo.deleteById(id);
    }

    public TableEntity updateStatus(Long id, String statusString) {
        TableEntity t = findById(id);

        Status status;
        try {
            status = Status.valueOf(statusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Trạng thái không hợp lệ");
        }

        t.setStatus(status);
        t.setUpdatedAt(LocalDateTime.now());
        return tableRepo.save(t);
    }
}