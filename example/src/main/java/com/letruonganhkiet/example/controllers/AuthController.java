package com.letruonganhkiet.example.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letruonganhkiet.example.model.ERole;
import com.letruonganhkiet.example.model.Role;
import com.letruonganhkiet.example.model.User;
import com.letruonganhkiet.example.payload.request.LoginRequest;
import com.letruonganhkiet.example.payload.request.SignupRequest;
import com.letruonganhkiet.example.payload.response.JwtResponse;
import com.letruonganhkiet.example.payload.response.MessageResponse;
import com.letruonganhkiet.example.repository.RoleRepository;
import com.letruonganhkiet.example.repository.UserRepository;
import com.letruonganhkiet.example.security.jwt.JwtUtils;
import com.letruonganhkiet.example.security.services.UserDetailsImpl;

import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        System.out.println("Đăng nhập với username: " + loginRequest.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()));

            System.out.println("Xác thực thành công cho: " + loginRequest.getUsername());

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    roles));
        } catch (Exception e) {
            System.out.println("Lỗi xác thực: " + e.getMessage());
            return ResponseEntity
                    .status(401)
                    .body(new MessageResponse("Đăng nhập thất bại: " + e.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }

        // ✅ Tạo user mới
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));

        // Bổ sung field mới
        user.setFullName(signUpRequest.getFullName());
        user.setPhone(signUpRequest.getPhone());
        user.setImageUrl(signUpRequest.getImageUrl());
        user.setIsActive(true);
        user.setCreatedAt(java.time.LocalDateTime.now());

        // ✅ Xử lý roles
   // ✅ Xử lý roles linh hoạt
Set<String> strRoles = new HashSet<>();

// Cho phép dùng cả "role" (1 chuỗi) hoặc "roles" (mảng)
if (signUpRequest.getRoles() != null && !signUpRequest.getRoles().isEmpty()) {
    strRoles = signUpRequest.getRoles();
} else if (signUpRequest.getRole() != null && !signUpRequest.getRole().isEmpty()) {
    strRoles.add(signUpRequest.getRole());
}

Set<Role> roles = new HashSet<>();

if (strRoles.isEmpty()) {
    // Nếu không truyền gì thì mặc định là USER
    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    roles.add(userRole);
} else {
    // Lặp qua từng role được gửi lên
    strRoles.forEach(role -> {
        switch (role.toLowerCase()) { // 👈 Thêm .toLowerCase() để tránh lỗi viết hoa/thường
            case "admin":
                Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);
                break;
            case "mod":
            case "moderator":
                Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(modRole);
                break;
            default:
                Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(userRole);
        }
    });
}


        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
}
