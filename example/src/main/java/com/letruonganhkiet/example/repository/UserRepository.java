package com.letruonganhkiet.example.repository;

import com.letruonganhkiet.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List; // <<< THIẾU
import org.springframework.data.jpa.repository.Query; // <<< THIẾU
import org.springframework.data.jpa.repository.Modifying; // <<< THIẾU
import org.springframework.transaction.annotation.Transactional; // <<< THIẾU

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    // 🔍 Tìm theo username
    Optional<User> findByUsername(String username);

    // 🔍 Tìm theo email
    Optional<User> findByEmail(String email);

    // ✅ Kiểm tra trùng username hoặc email
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // 🟢 Lấy danh sách tất cả user còn hoạt động
    List<User> findByIsActiveTrue();

    // 🟡 Lấy danh sách user theo vai trò
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findUsersByRole(String roleName);

    // 🔴 Cập nhật trạng thái hoạt động của user
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.isActive = :status WHERE u.id = :id")
    int updateUserStatus(Long id, boolean status);

    // 🖼️ Cập nhật đường dẫn ảnh của user
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.imageUrl = :imageUrl WHERE u.id = :id")
    int updateUserImage(Long id, String imageUrl);

    // 🔍 Tìm kiếm nhân viên theo tên hoặc username (phục vụ search)
    @Query("SELECT u FROM User u WHERE LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchUsers(String keyword);
}