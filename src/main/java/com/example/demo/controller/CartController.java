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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.business.abstracts.CartItemService;
import com.example.demo.business.abstracts.FoodService;
import com.example.demo.entities.CartItem;
import com.example.demo.entities.Food;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartItemService cartItemService;
    private final FoodService foodService;

    @Autowired
    public CartController(CartItemService cartItemService, FoodService foodService) {
        this.cartItemService = cartItemService;
        this.foodService = foodService;
    }

    // Müşterinin sepetindeki tüm ürünler
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable int customerId) {
        return ResponseEntity.ok(cartItemService.getCartByCustomer(customerId));
    }

    // Sepete ürün ekleme
    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(
            @RequestParam int customerId,
            @RequestParam int foodId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(cartItemService.addToCart(customerId, foodId, quantity));
    }

    // SATIN ALMA (CHECKOUT)
    @PostMapping("/checkout/{customerId}")
    public ResponseEntity<String> checkout(@PathVariable int customerId) {

        List<CartItem> cartItems = cartItemService.getCartByCustomer(customerId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Sepet boş!");
        }

        for (CartItem item : cartItems) {
            Food food = item.getFood();

            try {
                foodService.increaseSoldCount(food.getId(), item.getQuantity());
            } catch (Exception e) {
                System.out.println("Sayaç güncellenemedi: " + e.getMessage());
            }
        }

        return ResponseEntity.ok("Satın alma işlemi başarılı.");
    }

    // Sepetten ürün silme
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<String> deleteCartItem(@PathVariable int cartItemId) {
        cartItemService.deleteByCart(cartItemId);
        return ResponseEntity.ok("Ürün sepetten silindi.");
    }

    // Sepet ürün miktarını güncelleme
    @PutMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateQuantity(
            @PathVariable int cartItemId,
            @RequestParam int quantity) {

        return ResponseEntity.ok(cartItemService.updateQuantity(cartItemId, quantity));
    }
}
