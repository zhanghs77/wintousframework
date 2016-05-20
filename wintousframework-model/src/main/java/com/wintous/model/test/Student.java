package com.wintous.model.test;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * The persistent class for the yun_app database table.
 * 
 */
@Entity
@Table(name="t_test_student")
@NamedQuery(name="Student.findAll", query="SELECT y FROM Student y")
public class Student implements Serializable {
	private static final long serialVersionUID = 1L;
	private int sid;
	private String name;
	private int age;
	
	public Student() {
	}
	
	@Id
	@Column( name = "sid", unique = true, nullable = false )
	@TableGenerator( name = "tg_t_test_student", pkColumnValue = "t_test_student", table = "t_id_table", pkColumnName = "tablename", valueColumnName = "pkid", initialValue = 1, allocationSize = 1 )
	@GeneratedValue( strategy = GenerationType.TABLE, generator = "tg_t_test_student" )
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	
}