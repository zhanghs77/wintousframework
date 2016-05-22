package com.wintous.web.controller.test;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wintous.web.controller.base.BaseController;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController {

	public TestController(){
		System.out.println("TestController");
	}
	@RequestMapping(value="/index.do",method=RequestMethod.GET)
	public String doIndex(){
		return "index";
	}
}
