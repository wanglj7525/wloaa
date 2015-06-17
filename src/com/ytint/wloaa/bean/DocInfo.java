package com.ytint.wloaa.bean;

/**
 * 文章信息
 * 
 * @author zhangyg
 * 
 */
public class DocInfo implements java.io.Serializable {
	public Long _id;// 文章id
	public String ct;// 文章正文
	public String u;// url,不包含http://的路径
	public int ch;// 通道类型
	public String t;// 标题
	public String a;// 摘要
	public String k;// 关键词
	public String sa;// 网站地域信息
	public String pt;// 发布时间
	public String au;// 作者
	public int rpc;// 转载数
	public int cc;// 点击数
	public int rc;// 转发数
	public int mc;// 回复、评论数
	public String bn;// 板块名称
	public String sn;// 网站名称
	public int si;// 网站id

	public String[] pic;// 图片列表
	public String[] bigPic;// 大图片列表
	public String[] video;// 视频列表
	public String ui;//微博人物id
	public String iu;//微博任务头像链接
	public Integer fn;//帖子楼层
	
	public String kv;// 命中关键词
	public String pl;// 人名列表
	public String rl;// 地名列表
	public String ol;// 机构名列表
}
