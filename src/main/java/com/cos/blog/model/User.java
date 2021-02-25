package com.cos.blog.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ORM -> Java(다른언어) Object -> 테이블로 매핑
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴
@Entity // User 클래스가 Mysql에 테이블이 생성된다.
//@DynamicInsert // insert 시에 null인 필드 제외시켜줌
public class User {
	
	@Id //Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 프로젝트에서 연결된 DB의 넘버링 전략을 따라간다.
	private int id; //mysql : auto_increment
	
	@Column(nullable = false, length = 100, unique = true) // 아이디 중복 방지
	private String username; //아이디
	
	@Column(nullable = false, length = 100) // 123456 => 해쉬 (비밀번호 암호화)
	private String password;
	
	@Column(nullable = false,  length = 50)
	private String email;
	
	//@ColumnDefault("'user'")
	//DB는 RoleType이 라는 게 없다.
	@Enumerated(EnumType.STRING)
	private RoleType role; 
	// ADMIN, USER 정해진 값 내에서만 사용되도록 하기
	// String으로 사용하면 role 입력시 오타가 나서 이상한 값이 들어갈 수 있다.
	// Enum을 쓰는게 좋다. 
	// Enum을 사용하게 되면 도메인(범위)이 정해지기 때문에 그 중 한 개 값만 들어가게 된다.
	
	private String oauth; // kakao, google
	
	@CreationTimestamp // 시간이 자동 입력
	private Timestamp createDate;
}
