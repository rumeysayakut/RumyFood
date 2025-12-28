package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.business.abstracts.FoodService;
import com.example.demo.entities.Food;

@RestController
@RequestMapping("/api/foods")
@CrossOrigin(origins = "*")
public class FoodController {

    private final FoodService foodService;

    @Autowired
    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    // Tüm foodları listele (customer da owner da görebilir)
    @GetMapping
    public ResponseEntity<List<Food>> getAll() {
        return ResponseEntity.ok(foodService.getAll());
    }
    


    // Owner'a ait yemekleri listele
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Food>> getFoodsByOwner(@PathVariable int ownerId) {
        return ResponseEntity.ok(foodService.getFoodsByOwner(ownerId));
    }

    // Owner yeni food ekler (image destekli)
    @PostMapping("/owner/{ownerId}")
    public ResponseEntity<Food> addFood(
            @PathVariable int ownerId,
            @ModelAttribute Food food,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        Food savedFood = foodService.addFoodForOwnerWithImage(food, ownerId, imageFile);
        return ResponseEntity.ok(savedFood);
    }

    // Owner food günceller (image destekli)
    @PutMapping("/{id}/owner/{ownerId}")
    public ResponseEntity<Food> updateFood(
            @PathVariable int id,
            @PathVariable int ownerId,
            @ModelAttribute Food food,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) throws IOException {

        Food updatedFood = foodService.updateFoodWithImage(id, ownerId, food, imageFile);
        return ResponseEntity.ok(updatedFood);
    }

    // Owner food siler
    @DeleteMapping("/{id}/owner/{ownerId}")
    public ResponseEntity<String> deleteFood(
            @PathVariable int id,
            @PathVariable int ownerId) {

        foodService.delete(id, ownerId);
        return ResponseEntity.ok("Food başarıyla silindi.");
    }

    // Tek food getir
    @GetMapping("/{id}")
    public ResponseEntity<Food> getById(@PathVariable int id) {
        return ResponseEntity.ok(foodService.getById(id));
    }
    
 // --- BURAYI EKLE (Admin Paneli İçin) ---
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFoodByAdmin(@PathVariable int id) {
        foodService.delete(id); // Senin FoodManager'ındaki mevcut delete(id) metodunu kullanır.
        return ResponseEntity.ok("Yemek (Admin yetkisiyle) silindi.");
    }
}
