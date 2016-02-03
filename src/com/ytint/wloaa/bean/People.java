package com.ytint.wloaa.bean;

public class People implements java.io.Serializable{
	public long id;
	public String phone;
	public String name;
	public String  imei;
	public int  status;
	public String jpush_registration_id;
	public String department_id;
	public String department_name;
	public int if_task_power;//是否有权利 点击工程任务 工程列表 0:无权限；1有权限

}
