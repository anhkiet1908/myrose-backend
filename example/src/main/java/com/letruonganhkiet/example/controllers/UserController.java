package com.letruonganhkiet.example.controllers;

import com.letruonganhkiet.example.model.User;
import com.letruonganhkiet.example.payload.response.MessageResponse;
import com.letruonganhkiet.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 🟢 Lấy toàn bộ user (nhân viên)
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 🟢 Lấy danh sách user còn hoạt động
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userRepository.findByIsActiveTrue());
    }

    // 🔍 Tìm kiếm user theo tên hoặc username
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(userRepository.searchUsers(keyword));
    }

    // 🟡 Lấy user theo ID (đã sửa lỗi type mismatch)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("❌ User not found"));
        }
    }

    // 🟡 Cập nhật thông tin user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> userData = userRepository.findById(id);
        if (userData.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("❌ User not found"));
        }

        User user = userData.get();
        user.setFullName(userDetails.getFullName());
        user.setEmail(userDetails.getEmail());
        user.setPhone(userDetails.getPhone());
        user.setImageUrl(userDetails.getImageUrl());
        user.setIsActive(userDetails.getIsActive());
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("✅ User updated successfully"));
    }

    // 🔴 Xóa user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.badRequest().body(new MessageResponse("❌ User not found"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("🗑️ User deleted successfully"));
    }// 🟡 Cập nhật trạng thái hoạt động
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam boolean status) {
        int result = userRepository.updateUserStatus(id, status);
        return result > 0
                ? ResponseEntity.ok(new MessageResponse("✅ User status updated"))
                : ResponseEntity.badRequest().body(new MessageResponse("❌ Failed to update user status"));
    }

    // 🖼️ Upload ảnh cho user
    @PostMapping("/{id}/upload")
    public ResponseEntity<?> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("❌ File is empty"));
        }

        String uploadDir = "uploads/users/";
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File destination = new File(uploadDir + fileName);

        try {
            file.transferTo(destination);
            String imageUrl = "/uploads/users/" + fileName;
            userRepository.updateUserImage(id, imageUrl);
            return ResponseEntity.ok(new MessageResponse("✅ Image uploaded successfully: " + imageUrl));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(new MessageResponse("❌ Upload failed: " + e.getMessage()));
        }
    }
}