package com.wintous.common.utils.excel;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface JxlExcelService<T> {

	/**
	 *  读取Excel记录
	 * @param input
	 * @param items
	 * @return
	 */
	public boolean readExcel(InputStream input,List<ExcelTitle> titles,List<T> items)throws ExcelParseException;
	
	/**
	 * 生成Excel 
	 * @param fileName 文件名称
	 * @param items  需要写入的数据
	 * @param titles 文件头设置
	 * @return
	 */
	public  void writeExcel(String fileName,OutputStream output, List<ExcelTitle> titles, List<T> items)throws ExcelParseException;

	
}
