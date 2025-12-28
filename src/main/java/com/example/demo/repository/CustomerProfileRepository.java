package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.CustomerProfile;

@Repository
public interface CustomerProfileRepository extends JpaRepository<CustomerProfile,Integer>{
	CustomerProfile findByUser_Id(Integer userId);
}
