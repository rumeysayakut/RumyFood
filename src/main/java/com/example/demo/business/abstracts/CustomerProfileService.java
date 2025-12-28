package com.example.demo.business.abstracts;

import com.example.demo.entities.CustomerProfile;
import java.util.List;

public interface CustomerProfileService {
    CustomerProfile create(CustomerProfile profile, Integer userId);
    CustomerProfile update(CustomerProfile profile);
    void delete(int id);
    CustomerProfile getById(int id);
    CustomerProfile getByUserId(int userId);
    List<CustomerProfile> getAll();
}
