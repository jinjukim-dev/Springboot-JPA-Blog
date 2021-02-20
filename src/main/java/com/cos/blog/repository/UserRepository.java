package com.cos.blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.blog.model.User;

// DAO
// 자동으로 bean 등록이 된다
// @Repository 어노테이션 생략 가능
public interface UserRepository extends JpaRepository<User, Integer>{ // User 테이블의 PK Integer이다.
	
	
}
