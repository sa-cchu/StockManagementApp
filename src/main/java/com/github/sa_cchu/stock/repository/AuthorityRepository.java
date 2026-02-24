package com.github.sa_cchu.stock.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

	Optional<Authority> findByAuthorityName(String authorityName);

	List<Authority> findByDeleteFlag(Integer deleteFlag);
}
//DBの窓口