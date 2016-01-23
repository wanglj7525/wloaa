package com.ytint.wloaa.bean;

import java.util.Date;


public class Project implements java.io.Serializable{
	
	public int id;
	public String name;
	public long regionid;
	public String address;
    public String area;
    public String cost;
    public String starttime;
    public String starttimeString;
    public String endtime;
    public String endtimeString;
    public String structure;
    public String layers;
    public String height;
    public String goals;
    public int completed;
    public int delete_flag;
    public int schedule;
    public String manager;
    public String telephone;
    public int construction_id;
    public String cname;//施工单位
    public int build_id;
    public String bname;//建设单位
    public int supervise_id;
    public String sname;//监理单位
    public String checktime;
    public String result;
    
    public String regionname;//所属区域

}
