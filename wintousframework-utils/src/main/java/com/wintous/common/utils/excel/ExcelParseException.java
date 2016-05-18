package com.wintous.common.utils.excel;

/**
 * excel异常处理
 * @author zhanghs
 *
 */
public class ExcelParseException extends Exception {

	private static final long serialVersionUID = 1L;

	public ExcelParseException(String message,Throwable cause){
		super(message,cause);
	}
}
