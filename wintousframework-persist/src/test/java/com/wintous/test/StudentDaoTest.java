package com.wintous.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.wintous.model.test.Student;
import com.wintous.persist.test.StudentDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:common/context-common-application.xml","classpath:common/context-jdbc-application.xml","classpath:persist/context-persist-application.xml"})
@TransactionConfiguration(defaultRollback=false)//设置是否回滚
public class StudentDaoTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private StudentDao dao;
	@Test
	public void saveTest(){
		Student student=new Student();
		student.setAge(20);
		student.setName("zhanghs");
		dao.save(student);
	}
}
