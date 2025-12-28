package com.example.demo.business.concretes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.business.abstracts.CustomerProfileService;
import com.example.demo.entities.CustomerProfile;
import com.example.demo.entities.User;
import com.example.demo.repository.CustomerProfileRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;

@Service
public class CustomerProfileManager implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomerProfileManager(CustomerProfileRepository customerProfileRepository,
                                  UserRepository userRepository) {
        this.customerProfileRepository = customerProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public CustomerProfile create(CustomerProfile profile, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        if (user.getCustomerProfile() != null) {
            throw new IllegalArgumentException("Bu kullanıcı için zaten customer profile var.");
        }

        profile.setUser(user);
        CustomerProfile saved = customerProfileRepository.save(profile);
        user.setCustomerProfile(saved);
        userRepository.save(user);
        return saved;
    }

    @Override
    public CustomerProfile update(CustomerProfile profile) {
        CustomerProfile existing = customerProfileRepository.findById(profile.getId())
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı!"));

        if (profile.getPhone() != null) existing.setPhone(profile.getPhone());
        return customerProfileRepository.save(existing);
    }

    
    @Override
    public void delete(int id) {
        customerProfileRepository.deleteById(id);
    }

    @Override
    public CustomerProfile getById(int id) {
        return customerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı!"));
    }

    @Override
    public CustomerProfile getByUserId(int userId) {
        CustomerProfile customerProfile = customerProfileRepository.findByUser_Id(userId);
        if (customerProfile == null) throw new RuntimeException("Profil bulunamadı!");
        return customerProfile;
    }

    @Override
    public List<CustomerProfile> getAll() {
        return customerProfileRepository.findAll();
    }
}
