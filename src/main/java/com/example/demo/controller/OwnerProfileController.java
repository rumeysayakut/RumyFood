package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.business.abstracts.OwnerProfileService;
import com.example.demo.entities.OwnerProfile;

@RestController
@RequestMapping("/api/owner-profiles")
@CrossOrigin(origins = "*")
public class OwnerProfileController {

    private final OwnerProfileService service;

    @Autowired
    public OwnerProfileController(OwnerProfileService service) { this.service = service; }

    @PostMapping("/user/{userId}")
    public ResponseEntity<OwnerProfile> create(@PathVariable Integer userId, @RequestBody OwnerProfile profile) {
        return ResponseEntity.ok(service.create(profile, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerProfile> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<OwnerProfile> getByUser(@PathVariable int userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }

    // 🔹 Tüm owner profilleri döndüren endpoint
    @GetMapping
    public ResponseEntity<List<OwnerProfile>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OwnerProfile> update(@PathVariable int id, @RequestBody OwnerProfile profile) {
        profile.setId(id);
        return ResponseEntity.ok(service.update(profile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}

