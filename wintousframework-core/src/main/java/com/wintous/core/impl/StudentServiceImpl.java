package com.wintous.core.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wintous.model.test.Student;
import com.wintous.persist.test.StudentDao;
import com.wintousframework.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

	@Autowired
	public StudentDao dao;
	
	@Override
	public Student getById(int sid) {
		return dao.findOne(sid);
	}

	
}
