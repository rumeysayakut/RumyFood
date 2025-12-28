package com.example.demo.business.concretes;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.business.abstracts.UserService;
import com.example.demo.entities.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserManager implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User add(User user) {
        if (user.getFirstName() == null ||
            user.getLastName() == null ||
            user.getEmail() == null ||
            user.getPassword() == null ||
            user.getRole() == null) {
            throw new IllegalArgumentException("TÜM ALANLARI EKSİKSİZ DOLDURUN !!!");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Bu email zaten kullanılıyor!");
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(int id) {
        userRepository.deleteById(id);
    }

    @Override
    public User update(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        if (updatedUser.getFirstName() != null) existingUser.setFirstName(updatedUser.getFirstName());
        if (updatedUser.getLastName() != null) existingUser.setLastName(updatedUser.getLastName());
        if (updatedUser.getEmail() != null) existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null) existingUser.setPassword(updatedUser.getPassword());
        if (updatedUser.getRole() != null) existingUser.setRole(updatedUser.getRole());

        return userRepository.save(existingUser);
    }

    @Override
    public User getById(int id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
