package com.github.sa_cchu.stock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.github.sa_cchu.stock.entity.Inquery;

@Repository
public interface InquiryRepository extends JpaRepository<Inquery, Integer> {
	
}
