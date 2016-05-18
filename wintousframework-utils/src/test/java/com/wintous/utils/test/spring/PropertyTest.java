package com.wintous.utils.test.spring;

import java.util.Properties;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wintous.common.utils.Property;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:utils/context-utils-application.xml"})
public class PropertyTest extends AbstractJUnit4SpringContextTests {
	
	@Resource
	private Property property;
	
	@Test
	public void PropertyGet(){
		Properties properties=property.getProperties("com.wintous");
		System.out.println(properties.get("test"));
	}
}
