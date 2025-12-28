package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "cart_item")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "quantity")
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "customer_profile_id", referencedColumnName = "id")
    @JsonIgnore // <--- BURASI KAPALI OLMALI (Sepet kime ait diye geri dönmesin)
    private CustomerProfile customerProfile;

    @ManyToOne
    @JoinColumn(name = "food_id")
    // BURASI AÇIK (Yemek detaylarını görmek istiyoruz)
    private Food food;

    public CartItem() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public CustomerProfile getCustomerProfile() { return customerProfile; }
    public void setCustomerProfile(CustomerProfile customerProfile) { this.customerProfile = customerProfile; }

    public Food getFood() { return food; }
    public void setFood(Food food) { this.food = food; }
}