package com.cos.blog.Handler;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice //  모든 exception 발생 시 
@RestController
public class GlobalExceptionHandler {

	@ExceptionHandler(value=Exception.class)
	public String handleArgumentException(Exception e) {
		return "<h1>"+e.getMessage()+"</h1>";
	}
}
