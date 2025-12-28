package com.example.demo.business.abstracts;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Food;

@Service
public interface FoodService {
    List<Food> getFoodsByOwner(int ownerId);
    void delete(int id);
    List<Food> getAll();
    Food getById(int id);
    Food save(Food food);
    void delete(int id, int ownerId);
    Food update(int id, Food updatedFood, int ownerId);
    Food addFoodForOwner(Food food, Integer ownerId);
    void increaseSoldCount(int foodId, int amount);

    // Görsel destekli metodlar
    Food addFoodForOwnerWithImage(Food food, Integer ownerId, MultipartFile imageFile) throws IOException;
    Food updateFoodWithImage(int id, Integer ownerId, Food food, MultipartFile imageFile) throws IOException;
}
