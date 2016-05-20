package com.wintous.persist.test;

import java.io.Serializable;

import org.springframework.data.repository.CrudRepository;

import com.wintous.model.test.Student;

public interface StudentDao extends CrudRepository<Student, Serializable>{

}
