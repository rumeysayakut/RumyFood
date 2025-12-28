package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.business.abstracts.CustomerProfileService;
import com.example.demo.entities.CustomerProfile;


@RestController
@RequestMapping("/customer-profiles")
@CrossOrigin(origins = "*")
public class CustomerProfileController {

    private final CustomerProfileService service;

    @Autowired
    public CustomerProfileController(CustomerProfileService service) { 
    	this.service = service;
    	}

    @PostMapping("/user/{userId}")
    public ResponseEntity<CustomerProfile> create(@PathVariable Integer userId, @RequestBody CustomerProfile profile) {
        return ResponseEntity.ok(service.create(profile, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerProfile> getById(@PathVariable int id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<CustomerProfile> getByUser(@PathVariable int userId) {
        return ResponseEntity.ok(service.getByUserId(userId));
    }

    @GetMapping
    public ResponseEntity<List<CustomerProfile>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerProfile> update(@PathVariable int id, @RequestBody CustomerProfile profile) {
        profile.setId(id);
        return ResponseEntity.ok(service.update(profile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
