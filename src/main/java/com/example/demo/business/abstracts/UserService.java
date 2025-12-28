package com.example.demo.business.abstracts;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.entities.User;


@Service
public interface UserService {
	User add(User user);
    void delete(int id);
    User update(User updatedUser);
    User getById(int id);
    List<User> getAll();
    User getByEmail(String email);
}
