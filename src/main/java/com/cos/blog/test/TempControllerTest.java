package com.cos.blog.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TempControllerTest {

	//http://localhost:8000/blog/temp/home
	@GetMapping("/temp/home")
	public String tempHome() {
		System.out.println("temphome()");
		// 파일리턴 기본 경로 : src/main/resources/static
		// 리턴명 : /home.html
		// 풀경로 : src/main/resources/static/home.html
		return "/home.html";
	}
	
	@GetMapping("/temp/img")
	public String tempImg() {
		return "/a.jpg";
	}
	
	@GetMapping("/temp/jsp")
	public String tempJsp() {
		// prifix : /WEB-INF/views
		// suffix: .jsp
		// 풀경로 : /WEB-INF/views/text.jsp
		
		return "test";
	}
}
