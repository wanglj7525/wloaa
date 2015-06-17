package com.ytint.wloaa.bean;

public class People implements java.io.Serializable{
	//用户ID
	public long id;
	//用户手机号
	public String phone;
	//用户名称 
	public String name;
	//imei
	public String  imei;
	public int  status;// 0：发布申请；1：一级审批人；2：二级审批人
	public String jpush_registration_id;//极光注册号

}
