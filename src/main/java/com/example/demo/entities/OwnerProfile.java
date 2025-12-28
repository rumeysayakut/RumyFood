package com.example.demo.entities;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "owner_profile")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class OwnerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "restaurant_name")
    private String restaurantName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    // User bilgisi gelsin, burası açık kalmalı
    private User user;
    
    @OneToMany(mappedBy="ownerProfile", cascade=CascadeType.ALL)
    @JsonIgnore // <--- İŞTE ÇÖZÜM: Owner çekilirken Yemek Listesi gelmesin (Döngü burada biter)
    private List<Food> foods;

    // Constructors
    public OwnerProfile() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Food> getFoods() { return foods; }
    public void setFoods(List<Food> foods) { this.foods = foods; }
}