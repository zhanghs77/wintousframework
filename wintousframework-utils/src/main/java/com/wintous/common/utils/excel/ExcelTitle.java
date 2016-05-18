package com.wintous.common.utils.excel;

/**
 * excel头字段描述
 * @author zhanghs
 *
 */
public class ExcelTitle {

	private String desc;//excel头cell显示
	private String column;//对应数据属性名称
	private String defaultValue;//cell默认值
	private  EnumMessage message[];
	public ExcelTitle(String column,String desc,String defaultValue){
		this.desc=desc;
		this.column=column;
		this.defaultValue=defaultValue;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public EnumMessage[] getMessage() {
		return message;
	}
	public void setMessage(EnumMessage[] message) {
		this.message = message;
	}
	
}
