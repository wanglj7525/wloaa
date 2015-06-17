package com.ytint.wloaa.bean;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;

/**
 * 下派任务
 * @author wlj
 * @date 2015-6-12上午10:33:53
 */
public class Xiapai implements java.io.Serializable{
	public Integer id;//消息标题
	public String title;// 标题 VARCHAR(256)*
	public String create_time;// 入库时间 DATETIME
	public String content;// 消息内容
	public Integer push_user_id;//发送人编号
	public Integer push_flag;// 是否已推送
	public String push_range;// 推送范围
	public Integer notice_type;//0：群发消息；1：下派任务
	public String receive_user_ids;//接收人编号列表
	public Long push_msg_id = 0L;

}