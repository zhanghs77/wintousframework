package com.wintous.core.impl.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.wintous.model.test.Student;
import com.wintousframework.service.StudentService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:common/context-common-application.xml","classpath:common/context-jdbc-application.xml","classpath:persist/context-persist-application.xml","classpath:core/context-core-service.xml"})
@TransactionConfiguration(defaultRollback=false)//设置是否回滚
public class StudentServiceTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	public StudentService service;
	@Test
	public void getStudentByIdTest(){
		Student student=service.getById(8);
		System.out.println(student.getName());
	}
}
