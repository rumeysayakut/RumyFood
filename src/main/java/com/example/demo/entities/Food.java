package com.example.demo.entities;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name="foods")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Food {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    
    private Double latitude;
    private Double longitude;
    private String address;
    private String imageUrl;
    private String name;
    private String description;
    private String category;

    // DEĞİŞİKLİK BURADA: int -> Integer yaptık.
    @Column(name = "sold_count")
    private Integer soldCount = 0; 

    @ManyToOne
    @JoinColumn(name = "owner_profile_id")
    private OwnerProfile ownerProfile;

    @OneToMany(mappedBy = "food", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<CartItem> cartItems;

    public Food() {}

    // GETTER ve SETTER'ları da int -> Integer olarak güncellemelisin:

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // --- BURASI GÜNCELLENDİ ---
    public Integer getSoldCount() { 
        // Eğer veritabanından null gelirse 0 döndür ki matematik işlemi bozulmasın
        return soldCount == null ? 0 : soldCount; 
    }
    
    public void setSoldCount(Integer soldCount) { 
        this.soldCount = soldCount; 
    }
    // ---------------------------

    public OwnerProfile getOwnerProfile() { return ownerProfile; }
    public void setOwnerProfile(OwnerProfile ownerProfile) { this.ownerProfile = ownerProfile; }

    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
}