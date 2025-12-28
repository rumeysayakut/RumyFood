package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entities.OwnerProfile;

@Repository
public interface OwnerProfileRepository extends JpaRepository<OwnerProfile,Integer>{
	OwnerProfile findByUser_Id(Integer userId);
}
