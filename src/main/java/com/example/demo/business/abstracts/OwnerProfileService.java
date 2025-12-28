package com.example.demo.business.abstracts;

import com.example.demo.entities.OwnerProfile;
import java.util.List;

public interface OwnerProfileService {
    OwnerProfile create(OwnerProfile profile, Integer userId);
    OwnerProfile update(OwnerProfile profile);
    void delete(int id);
    OwnerProfile getById(int id);
    OwnerProfile getByUserId(int userId);

    // getAll metodu eklendi
    List<OwnerProfile> getAll();
}
