package com.github.sa_cchu.stock.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    Optional<Authority> findByAuthorityName(String authorityName);
}
//DBの窓口