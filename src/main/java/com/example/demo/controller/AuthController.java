package com.example.demo.controller;

import java.util.HashMap; // <--- EKLENDİ
import java.util.Map;     // <--- EKLENDİ

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.business.abstracts.CustomerProfileService;
import com.example.demo.business.abstracts.OwnerProfileService;
import com.example.demo.business.abstracts.UserService;
import com.example.demo.entities.CustomerProfile;
import com.example.demo.entities.OwnerProfile;
import com.example.demo.entities.Role;
import com.example.demo.entities.User;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;
    private final CustomerProfileService customerProfileService;
    private final OwnerProfileService ownerProfileService;

    @Autowired
    public AuthController(UserService userService,
                          CustomerProfileService customerProfileService,
                          OwnerProfileService ownerProfileService) {
        this.userService = userService;
        this.customerProfileService = customerProfileService;
        this.ownerProfileService = ownerProfileService;
    }

    public static class RegisterRequest {
        public String firstName;
        public String lastName;
        public String email;
        public String password;
        public String phone;
        public String role;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userService.getByEmail(req.email) != null) {
            return ResponseEntity.badRequest().body("Bu e-posta zaten kayıtlı!");
        }

        User user = new User();
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setEmail(req.email);
        user.setPassword(req.password);
        user.setRole(Role.valueOf(req.role.toUpperCase()));

        User savedUser = userService.add(user);

        if (savedUser.getRole() == Role.CUSTOMER) {
            CustomerProfile cp = new CustomerProfile();
            cp.setPhone(req.phone);
            cp.setUser(savedUser);
            customerProfileService.create(cp, savedUser.getId());
        } else if (savedUser.getRole() == Role.OWNER) {
            OwnerProfile op = new OwnerProfile();
            op.setPhone(req.phone);
            op.setUser(savedUser);
            ownerProfileService.create(op, savedUser.getId());
        }

        return ResponseEntity.ok(savedUser);
    }

    public static class LoginRequest {
        public String email;
        public String password;
    }

    // --- GÜNCELLENEN LOGİN METODU ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User user = userService.getByEmail(req.email);

        if (user == null) {
            return ResponseEntity.status(401).body("Kullanıcı bulunamadı!");
        }

        if (!user.getPassword().equals(req.password)) {
            return ResponseEntity.status(401).body("Şifre hatalı!");
        }

        // DİKKAT: User nesnesini direkt döndürmüyoruz!
        // Çünkü User -> Customer -> Cart -> Food -> ... sonsuz döngüye girip 500 hatası veriyor.
        // Bunun yerine temiz bir Map (Sözlük) oluşturup sadece lazım olanı gönderiyoruz.
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        // Eğer Owner ise profil ID'sini de ekleyelim (Frontend kullanıyor)
        if (user.getRole() == Role.OWNER && user.getOwnerProfile() != null) {
            Map<String, Object> ownerProfileData = new HashMap<>();
            ownerProfileData.put("id", user.getOwnerProfile().getId());
            response.put("ownerProfile", ownerProfileData);
        }

        // Customer profil ID'sini frontend zaten ayrı bir istekle çekiyor, buraya eklemeye gerek yok.
        
        return ResponseEntity.ok(response);
    }
}