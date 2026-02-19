package com.github.sa_cchu.stock.repository;

//Userテーブルを操作する
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.sa_cchu.stock.entity.User;//Userテーブルを実装

public interface UserRepository extends JpaRepository<User, Integer> {//<>の中はエンティティ名、主キーの型名
//JpaRepositoryを継承することでSQL文を自動的に使用することができる
	
    Optional<User> findByUserName(String userName);
    //定型文　Optional<クラス名> SQLコード(型　変数名）
    
    //SQLコード
    //findAll
    //findByカラム名
    //delete
    //save
}
