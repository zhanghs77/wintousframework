package com.wintous.utils.test.spring;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wintous.common.utils.excel.ExcelParseException;
import com.wintous.common.utils.excel.ExcelTitle;
import com.wintous.common.utils.excel.JxlExcelService;
import com.wintous.common.utils.excel.JxlExcelServiceImpl;
import com.wintous.common.utils.excel.Person;
import com.wintous.common.utils.excel.Sex;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:common/context-utils-application.xml"})
public class ExcelTest extends AbstractJUnit4SpringContextTests {

	@Autowired
	private JxlExcelService jxlExcelService;
	@Test
	public void TestWriteExcel(){
		try {
			FileOutputStream output=new FileOutputStream(new File("e:\\test.xlsx"));
			List<ExcelTitle> titles=new ArrayList<ExcelTitle>();
			ExcelTitle title=new ExcelTitle("name","姓名","");
			ExcelTitle title1=new ExcelTitle("age","年龄","0");
			ExcelTitle title2=new ExcelTitle("sex.name","性别","0");
			titles.add(title);
			titles.add(title1);
			titles.add(title2);
			Person p=new Person();
			p.setName("张三");
			p.setAge("20");
			Sex sex=new Sex();
			sex.setName("男");
			p.setSex(sex);
			List<Object> person=new ArrayList<Object>();
			for(int i=0;i<100;i++){
				person.add(p);
			}
			try {
				jxlExcelService.writeExcel("test", output, titles, person);
			} catch (ExcelParseException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
