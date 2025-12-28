package com.example.demo.business.concretes;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.business.abstracts.CartItemService;
import com.example.demo.entities.CartItem;
import com.example.demo.entities.CustomerProfile;
import com.example.demo.entities.Food;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CustomerProfileRepository;
import com.example.demo.repository.FoodRepository;

@Service
public class CartItemManager implements CartItemService{

	private final CartItemRepository cartItemRepository;
	private final CustomerProfileRepository customerProfileRepository;
	private final FoodRepository foodRepository;
	
	@Autowired
	public CartItemManager(CartItemRepository cartItemRepository,
			CustomerProfileRepository customerProfileRepository,
			FoodRepository foodRepository) {
		this.foodRepository=foodRepository;
		this.customerProfileRepository=customerProfileRepository;
		this.cartItemRepository=cartItemRepository;
	}
	
	@Override
	public void deleteByCart(int id) {
		cartItemRepository.deleteById(id);
	}
	
	@Override
    public List<CartItem> getCartByCustomer(int customerId) {
	        return cartItemRepository.findByCustomerProfileId(customerId);
	}
	
	@Override
    public CartItem updateQuantity(int cartItemId, int quantity) {
        CartItem ci = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("CartItem bulunamadı"));
        if (quantity <= 0) {
            cartItemRepository.delete(ci);
            return null;
        }
        ci.setQuantity(quantity);
        return cartItemRepository.save(ci);
    }
    
	@Override
    public CartItem addToCart(int customerId, int foodId, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be > 0");

        CustomerProfile customer = customerProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer bulunamadı"));
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Food bulunamadı"));

        // Aynı customer için aynı food varsa quantity artır
        Optional<CartItem> existing = cartItemRepository
                .findByCustomerProfileIdAndFoodId(customerId, foodId); // repo'da böyle bir metod olabilir

        if (existing.isPresent()) {
            CartItem ci = existing.get();
            ci.setQuantity(ci.getQuantity() + quantity);
            return cartItemRepository.save(ci);
        } else {
            CartItem ci = new CartItem();
            ci.setCustomerProfile(customer);
            ci.setFood(food);
            ci.setQuantity(quantity);
            return cartItemRepository.save(ci);
        }
    }

	
	
}
