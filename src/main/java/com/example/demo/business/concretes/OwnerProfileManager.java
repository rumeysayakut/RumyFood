package com.example.demo.business.concretes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.business.abstracts.OwnerProfileService;
import com.example.demo.entities.OwnerProfile;
import com.example.demo.entities.User;
import com.example.demo.repository.OwnerProfileRepository;
import com.example.demo.repository.UserRepository;

import java.util.List;

@Service
public class OwnerProfileManager implements OwnerProfileService {

    private final OwnerProfileRepository ownerProfileRepository;
    private final UserRepository userRepository;

    @Autowired
    public OwnerProfileManager(OwnerProfileRepository ownerProfileRepository,
                               UserRepository userRepository) {
        this.ownerProfileRepository = ownerProfileRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OwnerProfile create(OwnerProfile profile, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));

        if (user.getOwnerProfile() != null) {
            throw new IllegalArgumentException("Bu kullanıcı için zaten owner profile var.");
        }

        profile.setUser(user);
        OwnerProfile saved = ownerProfileRepository.save(profile);
        user.setOwnerProfile(saved);
        userRepository.save(user);
        return saved;
    }

    @Override
    public OwnerProfile update(OwnerProfile profile) {
        OwnerProfile existing = ownerProfileRepository.findById(profile.getId())
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı!"));

        if (profile.getRestaurantName() != null) existing.setRestaurantName(profile.getRestaurantName());
        if (profile.getPhone() != null) existing.setPhone(profile.getPhone());
        if (profile.getDescription() != null) existing.setDescription(profile.getDescription());
        return ownerProfileRepository.save(existing);
    }

    @Override
    public void delete(int id) {
        ownerProfileRepository.deleteById(id);
    }

    @Override
    public OwnerProfile getById(int id) {
        return ownerProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profil bulunamadı!"));
    }

    @Override
    public OwnerProfile getByUserId(int userId) {
        OwnerProfile p = ownerProfileRepository.findByUser_Id(userId);
        if (p == null) throw new RuntimeException("Profil bulunamadı!");
        return p;
    }


      public List<OwnerProfile> getAll() {
        return ownerProfileRepository.findAll();
    }
}
