package com.wintous.web.controller.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wintous.model.test.Student;
import com.wintous.web.controller.base.BaseController;
import com.wintousframework.service.StudentService;

@Controller
@RequestMapping("/test")
public class TestController extends BaseController {

	@Autowired
	public StudentService studentService;
	
	public TestController(){
		log.debug(this.getClass().getName()+"初始化");
	}
	
	@RequestMapping(value="/index.do",method=RequestMethod.GET)
	public String doIndex(Model map){
		Student student=studentService.getById(8);
		log.debug(""+student.getAge());
		map.addAttribute("student",student);
		return "index";
	}
}
