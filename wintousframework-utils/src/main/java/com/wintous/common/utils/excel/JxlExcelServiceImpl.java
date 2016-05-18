package com.wintous.common.utils.excel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class JxlExcelServiceImpl<T> implements JxlExcelService<T> {

	private static final JxlExcelServiceImpl jxlexcel=new JxlExcelServiceImpl();
	
	private  JxlExcelServiceImpl() {}
	
	public static JxlExcelServiceImpl getInstance(){
		return jxlexcel;
	}
	
	@Override
	public  boolean readExcel(InputStream input, List<ExcelTitle> titles, List<T> items) throws ExcelParseException  {
		
		return false;
	}

	@Override
	public  void writeExcel(String fileName,OutputStream output, List<ExcelTitle> titles, List<T> items) throws ExcelParseException {
		  WritableWorkbook wwb;
	  try {
			  wwb = Workbook.createWorkbook(output);
			  WritableSheet ws = wwb.createSheet(fileName,10);// 创建一个工作表
			 //设置单元格的文字格式
			 WritableFont wf = new WritableFont(WritableFont.ARIAL,12,WritableFont.NO_BOLD,false,UnderlineStyle.NO_UNDERLINE,Colour.BLUE);
			 WritableCellFormat wcf = new WritableCellFormat(wf);
			 wcf.setVerticalAlignment(VerticalAlignment.CENTRE); 
			 wcf.setAlignment(Alignment.CENTRE); 
			 ws.setRowView(1, 500);
			 dataHandle(ws, wcf, items, titles);
			 wwb.write();
	         wwb.close();
		} catch (RowsExceededException e) {
			throw new ExcelParseException("RowsExceededException",e);
		} catch (FileNotFoundException e) {
			throw new ExcelParseException("FileNotFoundException",e);
		} catch (WriteException e) {
			throw new ExcelParseException("WriteException",e);
		} catch (IOException e) {
			throw new ExcelParseException("IO异常",e);
		}
	}
	
	
	private  void dataHandle(WritableSheet ws,WritableCellFormat wcf,List<T> items, List<ExcelTitle> titles)throws ExcelParseException{
		try {
			//生成文件头
			ExcelTitle title;
			T obj;
			for(int i=0;i<titles.size();i++){
				title=titles.get(i);
				ws.addCell(new Label(i+1, 1,title.getDesc(), wcf));//添加表头
				for (int j = 0; j < items.size(); j++) {
					obj=items.get(j);
					Object value=getLasValue(title.getColumn(),obj);
					String v="";
					if(value!=null){
						v=new String(value.toString());
					}
					EnumMessage[] message=title.getMessage();
					if(message!=null){
						int tmp=Integer.valueOf(v);
						v=message[tmp].getValue(tmp).toString();
					}
					ws.addCell(new Label(i+1,j+2,v, wcf));//添加表内容
				}
			}
		} catch (RowsExceededException e) {
			throw new ExcelParseException("RowsExceededException",e);
		} catch (WriteException e) {
			throw new ExcelParseException("WriteException",e);
		} catch (IllegalArgumentException e) {
			throw new ExcelParseException("IllegalArgumentException",e);
		} 
	}
	
	/**
	 * 获取数据
	 * @param column
	 * @return
	 */
	public Object getLasValue(String column,Object obj){
		try {
			String columns[]=null;
			if(column.contains(".")){
				columns=column.split("\\.");
				for (int i = 0; i < columns.length; i++) {
					obj=getValue(columns[i], obj);
					if(i+1==columns.length){
						return obj;
					}
				}
			}else{
				return getValue(column, obj);
			}
			
		}  catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	
	private Object getValue(String column,Object bean){
		char[] buffer=column.toCharArray();
		buffer[0]=Character.toUpperCase(column.charAt(0));
		column=new String(buffer);//将第一个字符转换成大写
		StringBuilder sb=new StringBuilder(30);
		String methodName="";
		if(column.startsWith("N_")){//单独处理list集合，
			methodName=column.substring(2);
		}else{
			methodName=sb.append("get").append(column).toString();
		}
		Class<Object> obj=(Class<Object>)bean.getClass();
		try {
			Method method=obj.getMethod(methodName);
			return method.invoke(bean);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}



	
}
