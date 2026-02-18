package com.github.sa_cchu.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Category;


public interface CategoryRepository extends JpaRepository<Category, Integer>{
	
	Optional<Category> findByCategoryName(String categoryName);
}