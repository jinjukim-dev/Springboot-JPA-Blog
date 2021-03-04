# Springboot-JPA-Blog (Study Springboot project)

> 📚 Springboot, JPA, Spring scurity, OAuth2.0을 공부하면서 간단한 블로그를 만들어보는 스터디 프로젝트

### 개발환경
- 개발 프로그램 : SpringToolSuite4
- DBMS : Mysql, Spring Data JPA
- CSS : bootstrap4
- api : kakaoLogin

### 기능
- 회원가입, 일반로그인, 카카오로그인, 정보수정
- 게시글 작성, 수정, 삭제
- 댓글 작성, 삭제

### UI
<img src = "https://user-images.githubusercontent.com/62095517/109520515-ab9e9000-7aef-11eb-86cf-aed7765364b0.png" width="60%">

## Study 정리

### 🚩 먼저 Spring에 대해서 알아보기
[Spring 이란?](https://velog.io/@jinjukim-dev/Spring%EC%9D%B4%EB%9E%80)

### 🚩 Springboot 동작원리
[Springboot 동작원리 정리](https://velog.io/@jinjukim-dev/Springboot-%EB%8F%99%EC%9E%91%EC%9B%90%EB%A6%AC)
- 로그인과정 로직
<div><img src = "https://images.velog.io/images/jinjukim-dev/post/57b26277-b928-4027-9732-e662f284a0d8/1F5F6D04-BDFF-4007-B136-038A43F6FC09.jpeg" width="60%"></div>

### 🚩 JPA
[JAP란?](https://velog.io/@jinjukim-dev/JPA-%EB%9E%80)
- ORM을 이용하여 Object를 먼저 만들고 JPA 인터페이스를 통해서 데이터베이스 자동 생성

#### Lombok 이용
- java 라이브러리로 반복되는 getter, setter 등등 줄여주는 코드 다이어트 라이브러리 사용
- @Data - getter, setter 생성
- @NoArgsConstructor - 기본생성자
- @AllArgsConstructor - 모든 필드값을 파라미터로 받는 생성자
- @Builder - 빌더 패턴
- @Entity - User 클래스가 Mysql에 테이블이 생성된다.

- com.cos.blog.model - User.class
``` java
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
@Data //lo
@NoArgsConstructor
@AllArgsConstructor
@Builder // 빌더 패턴
@Entity // User 클래스가 Mysql에 테이블이 생성된다.
//@DynamicInsert // insert 시에 null인 필드 제외시켜줌
public class User {
	
	@Id //Primary Key
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 프로젝트에서 연결된 DB의 넘버링 전략을 따라간다. Mysql(AI)
	private int id; //mysql : auto_increment
	
	@Column(nullable = false, length = 100, unique = true) // 아이디 중복 방지
	private String username; //아이디
	
	@Column(nullable = false, length = 100) // 123456 => 해쉬(비밀번호 암호화)를 위해 길이 100으로 설정
	private String password;
	
	@Column(nullable = false,  length = 50)
	private String email;
	
	// @ColumnDefault("'user'")
	// DB는 RoleType이 라는 게 없다.
	// ADMIN, USER 정해진 값 내에서만 사용되도록 하기
	// String으로 사용하면 role 입력시 오타가 나서 이상한 값이 들어갈 수 있다.
	// Enum을 쓰는게 좋다. 
	// Enum을 사용하게 되면 도메인(범위)이 정해지기 때문에 그 중 한 개 값만 들어가게 된다.
  @Enumerated(EnumType.STRING)
	private RoleType role; 
	
	private String oauth; // kakao, google
	
	@CreationTimestamp // 시간이 자동 입력
	private Timestamp createDate;
}

package com.cos.blog.model;

public enum RoleType {
	USER, ADMIN
}

```
### 🚩 JSON
[JSON](https://velog.io/@jinjukim-dev/JSON)

### 🚩 Spring Security
[Spring Security 동작원리](https://velog.io/@jinjukim-dev/Spring-Security)
- .antMatchers("/", "/auth/**", "/js/**", "/css/**", "/image/**", "/dummy/**") 주소는 제외하고 나머니지
주소에 대해서 세션이 없으면Spring Security login page로 이동시킨다.

- com.cos.blog.config - SecurityConfig.java
``` java
package com.cos.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.cos.blog.config.auth.PrincipalDetailService;

import ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter;

// 빈 등록 : 스프링 컨테이너에서 객체를 관리할 수 있게 하는 것
@Configuration // 빈등록 (IoC 관리)
@EnableWebSecurity // 시큐리티 필터가 등록이 된다.
@EnableGlobalMethodSecurity(prePostEnabled = true) // 특정 주소로 접근을 하면 권한 및 인증을 미리 체크하겠다는 뜻.
public class SecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private PrincipalDetailService principalDetailService;
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean // IoC 가 됨.
	public BCryptPasswordEncoder encodePWD() {
		return new BCryptPasswordEncoder();
	}
	
	// 시큐리티가 대신 로그인해주는데 password를 가로채기를 하는데
	// 해당 password가 뭘로 해쉬가 되어 회원가입이 되었는지 알아야
	// 같은 해쉬로 암호화해서 DB에 있는 해쉬랑 비교할 수 있다.
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(principalDetailService).passwordEncoder(encodePWD());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.csrf().disable() // csrf 토큰 비활성화 (테스트 시 걸우두는게 좋음)
			.authorizeRequests() // 인증이 필요한 요청
				.antMatchers("/", "/auth/**", "/js/**", "/css/**", "/image/**", "/dummy/**")
				.permitAll()
				.anyRequest()
				.authenticated()
			.and()
				.formLogin()
				.loginPage("/auth/loginForm")
				.loginProcessingUrl("/auth/loginProc")
				.defaultSuccessUrl("/"); // 스프링 시큐리티가 해당 주소로 오는 로그인을 가로채서 대신 로그인 해준다.
	}
}

```
### 🚩 OAuth2.0
[OAuth2.0 개념](https://velog.io/@jinjukim-dev)
- KakaoLogin 구현
- KakaoProfile.java
- OAuthToken.java

- UserController.java
```java
@GetMapping("/auth/kakao/callback")
	public String kakaoCallback(String code) { // Data를 리턴해주는 컨트롤러 함수
		
		// POST방식으로 key=value 데이터를 요청(카카오 쪽으로)
		// Retrifit2
		// OkHttp
		// RestTemplate
		
		RestTemplate rt = new RestTemplate();
		
		/* 헤더 값 넣어주기*/
		// HttpHeader 오브젝트 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		/* 바디 값 넣어주기*/
		// HttpBody 오브젝트 생성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "/* kakao에서 받은 cliendkey 넣어주기*/");
		params.add("redirect_uri", "http://localhost:8000/auth/kakao/callback");
		params.add("code", code); // 코드는 동적
		
		// HttpHeader와 HttpBody를 하나의 오브젝트(Map)에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
		
		// Http 요청하기 - POST 방식으로 - response 변수의 응답 받음
		ResponseEntity<String> response = rt.exchange(
					"https://kauth.kakao.com/oauth/token", // 토큰 요청 발급 주소
					HttpMethod.POST,
					kakaoTokenRequest,
					String.class  // responseEntity의 응답을 String으로 받음
		);
		
		// json data를 자바 오브젝트로 처리하기 위해서 
		// Gson, Json Simple, ObjectMapper
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		
		try {
			oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
		} catch (JsonProcessingException e) {			
			e.printStackTrace();
		}
		
		System.out.println("카카오 엑세스 토큰 : "+oauthToken.getAccess_token());
		
		RestTemplate rt2 = new RestTemplate();
		
		// HttpHeader 오브젝트 생성
		HttpHeaders headers2 = new HttpHeaders();
		headers2.add("Authorization", "Bearer "+oauthToken.getAccess_token());
		headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// HttpHeader와 HttpBody를 하나의 오브젝트에 담기
		HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = 
				new HttpEntity<>(headers2);
		
		// Http 요청하기 - POST 방식으로 - response 변수의 응답 받음
		ResponseEntity<String> response2 = rt2.exchange(
					"https://kapi.kakao.com/v2/user/me", // 토큰 요청 발급 주소 (토큰을 통한 사용자 정보 조회)
					HttpMethod.POST,
					kakaoProfileRequest2,
					String.class  // responseEntity의 응답을 String으로 받음
		);

		ObjectMapper objectMapper2 = new ObjectMapper();
		KakaoProfile kakaoProfile = null;
		
		try {
			kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
		} catch (JsonProcessingException e) {			
			e.printStackTrace();
		}
		
		// User 오브젝트 : username, password, email
		// kakaoLogin username : 카카오계정이메일 + 카카오 프로필 아이디
		// kakaoLogin password : coskey값
		// kakaoLogin email : 카카오 이메일
		User kakaoUser = User.builder()
				.username(kakaoProfile.getKakao_account().getEmail()+"_"+kakaoProfile.getId())
				.password(cosKey)
				.email(kakaoProfile.getKakao_account().getEmail())
				.oauth("kakao")
				.build();
		
		// 가입자 혹은 비가입자 체크 처리
		User originUser = userService.회원찾기(kakaoUser.getUsername());
		
		if(originUser.getUsername() == null) {
			System.out.println("신규 회원으로 자동 회원가입이 진행됩니다.");
			userService.회원가입(kakaoUser);
		}
		
		System.out.println("자동로그인을 진행합니다.");
		// 로그인 처리
		// 세션등록 - 토큰 만들어서 유저아이디, 비밀번호 날리기
		Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(kakaoUser.getUsername(), cosKey));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		return "redirect:/";
	}
```


### 🚩 Ajax
[Ajax 란?](https://velog.io/@jinjukim-dev/Ajax)
회원가입 로직으로 Ajax 알아보기
- joinForm.jsp
``` jsp
<form></form> <!--회원가입 정보(username, password, email)가 담긴 form -->
<button id="btn-save">회원가입 완료</button>
<scpint src="/js/user.js"></script> <!-- user.js 실행 -->
```
- js/user.js
``` js
let index = {
  init: function(){
  $("#btn-save").on("click",()=>{
    this.save();
  });
 },
 
 save: function(){
  let data = {
     username: $("#username").val(),
     password: $("#password").vale(),
     email: $("#email").val()
  };
  
  // ajax 호출 시 default가 비동기 호출
  // ajax 통신을 이용해서 3개의 데이터를 json으로 변경하여 insert요청
  // ajax가 통신을 성공하고 서버가 json을 리턴해주면 자동으로 자바 오브젝트로 변환해준다.
  
  $.ajax({
    type: "POST",
    url: "/auth/joinProc", // PostMatppring("/auth/joinProc")으로 이동
    data: JSON.stringfy(data), //java가 이해할 수 있게 JSON 문자열로 변경하여 보내줌
    contentType: "application/json; charset=UTF-8", // MIME type
    dataType: "json"
  }).done(function(resp){
      if(resp.status === 500){
        alert("회원가입에 실패"); // 500에러 시 회원가입 실패
      }else{
        alert("회원가입 완료");
        location.href = "/"; // 이동
      }
  }).fail(function(){
      alert(JSON.stringify(error));
  });
 },
}

index.init(); // init() 실행
```


### 🚩 Javascript

### 🚩 데이터 요청방식

### 🚩 만났던 에러
