package com.ytint.wloaa.bean;

import java.util.Date;

/**
 * 图片 视频
 * @author wlj
 * @date 2015-6-16下午10:29:31
 */
public class HotNews implements java.io.Serializable{
	public int  id;// 编号
	public String content;//内容
	public String file_name;// 新文件名
	public String original_file_name;//原始文件名
	public int type;// 文件类型，1：图片；2：视频
	public String url;// 文件存放路径
	public String create_time;// 创建时间；
	public Long user_id;
	public String user_name;
	
}