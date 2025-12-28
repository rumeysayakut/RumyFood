package com.example.demo.entities;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "customer_profile")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CustomerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "phone")
    private String phone;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    // User bilgisi açık, profil kime ait görelim
    private User user;
    
    @OneToMany(mappedBy = "customerProfile", cascade = CascadeType.ALL)
    @JsonIgnore // Döngü Kırıcı: Profil çekilirken sepet listesi gelmesin
    private List<CartItem> cartItems;

    public CustomerProfile() {}

    // Getter ve Setterlar (Manuel eklendi)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
}