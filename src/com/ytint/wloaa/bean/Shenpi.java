package com.ytint.wloaa.bean;



/**
 * 现场执法 照片 视频 列表 
 * @author wlj
 * @date 2015-6-12上午10:33:53
 */
public class Shenpi implements java.io.Serializable{
	public Integer id;//doc的索引
	public Integer apply_user_id;//申请人编号
	public String apply_user_name;//申请人名称
	public String create_time;// 入库时间 DATETIME
	public String title;// 
	public String content;// 
	public Integer first_verify_status;// 一级审批状态：0：未审核；1：一级审核完成；2：二级审核完成
	public Integer second_verify_status;// 二级审批状态：0：待审批；1：通过审批；2：审批未通过
	public Integer first_verify_user_id;// 一级批准审核人
	public String first_verify_user_name;//一级批准审核人
	public String first_verify_comment;// 一级批准审核说明
	public String first_verify_time;// 一级批准审核时间
	public Integer second_verify_user_id;// 二级批准审核人
	public String second_verify_user_name;//二级批准审核人
	public String second_verify_comment;// 二级批准审核说明
	public String second_verify_time;// 二级批准审核时间
	
	public String name;
	public String create_user_name;
	public String receive_user_id;
	public String receive_user_name;
	public String contact;
	public String attachment;
	public String media;
	public String company_name;
	public String handle_mode;
	public String taskForwardInfo;
	public String remark;
	public int task_type;
	public int status;
	public String create_time_string;

}
