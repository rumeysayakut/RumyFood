package com.example.demo.business.concretes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.business.abstracts.FoodService;
import com.example.demo.entities.Food;
import com.example.demo.entities.OwnerProfile;
import com.example.demo.repository.FoodRepository;
import com.example.demo.repository.OwnerProfileRepository;

@Service
public class FoodManager implements FoodService {

    private final FoodRepository foodRepository;
    private final OwnerProfileRepository ownerProfileRepository;

    @Autowired
    public FoodManager(FoodRepository foodRepository, OwnerProfileRepository ownerProfileRepository) {
        this.foodRepository = foodRepository;
        this.ownerProfileRepository = ownerProfileRepository;
    }

    @Override
    public List<Food> getFoodsByOwner(int ownerId) {
        return foodRepository.findAllByOwnerProfile_Id(ownerId);
    }
    
    @Override
    public void increaseSoldCount(int foodId, int amount) {
        Food food = foodRepository.findById(foodId).orElse(null); 
        
        if(food != null) {
            food.setSoldCount(food.getSoldCount() + amount);
            foodRepository.save(food); 
        }
    }

    @Override
    public void delete(int id) {
        foodRepository.deleteById(id);
    }

    @Override
    public void delete(int id, int ownerId) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food bulunamadı"));

        if (!Objects.equals(existingFood.getOwnerProfile().getId(), ownerId)) {
            throw new RuntimeException("Bu işlemi yapmaya yetkiniz yok!");
        }

        foodRepository.deleteById(id);
    }

    @Override
    public List<Food> getAll() {
        return foodRepository.findAll();
    }

    @Override
    public Food getById(int id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food bulunamadı"));
    }

    @Override
    public Food save(Food food) {
        return foodRepository.save(food);
    }

    @Override
    public Food update(int id, Food updatedFood, int ownerId) {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food bulunamadı"));

        if (!Objects.equals(existingFood.getOwnerProfile().getId(), ownerId)) {
            throw new RuntimeException("Bu işlemi yapmaya yetkiniz yok!");
        }

        existingFood.setName(updatedFood.getName());
        existingFood.setDescription(updatedFood.getDescription());
        existingFood.setCategory(updatedFood.getCategory());
        existingFood.setImageUrl(updatedFood.getImageUrl());
        existingFood.setLatitude(updatedFood.getLatitude());
        existingFood.setLongitude(updatedFood.getLongitude());
        existingFood.setAddress(updatedFood.getAddress());

        return foodRepository.save(existingFood);
    }

    @Override
    public Food addFoodForOwner(Food food, Integer ownerId) {
        OwnerProfile owner = ownerProfileRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner bulunamadı"));

        food.setOwnerProfile(owner);
        return foodRepository.save(food);
    }

    @Override
    public Food addFoodForOwnerWithImage(Food food, Integer ownerId, MultipartFile imageFile) throws IOException {
        OwnerProfile owner = ownerProfileRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner bulunamadı"));

        String uploadDir = "uploads/";
        Files.createDirectories(Paths.get(uploadDir));
        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path path = Paths.get(uploadDir, fileName);
        Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        food.setOwnerProfile(owner);
        food.setImageUrl(fileName);

        return foodRepository.save(food);
    }

    @Override
    public Food updateFoodWithImage(int id, Integer ownerId, Food updatedFood, MultipartFile imageFile) throws IOException {
        Food existingFood = foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food bulunamadı"));

        if (!Objects.equals(existingFood.getOwnerProfile().getId(), ownerId)) {
            throw new RuntimeException("Bu işlemi yapmaya yetkiniz yok!");
        }

        existingFood.setName(updatedFood.getName());
        existingFood.setDescription(updatedFood.getDescription());
        existingFood.setCategory(updatedFood.getCategory());
        existingFood.setLatitude(updatedFood.getLatitude());
        existingFood.setLongitude(updatedFood.getLongitude());
        existingFood.setAddress(updatedFood.getAddress());

        if (imageFile != null && !imageFile.isEmpty()) {
            Files.createDirectories(Paths.get("uploads/"));
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path path = Paths.get("uploads", fileName);
            Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            existingFood.setImageUrl(fileName);
        }

        return foodRepository.save(existingFood);
    }
}
