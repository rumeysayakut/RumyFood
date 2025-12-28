package com.example.demo.business.abstracts;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entities.CartItem;

@Service
public interface CartItemService {

	void deleteByCart(int id);
	List<CartItem> getCartByCustomer(int customerId);
	CartItem updateQuantity(int cartItemId, int quantity);
	CartItem addToCart(int customerId, int foodId, int quantity);
	
}
