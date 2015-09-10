package com.ytint.wloaa.bean;

import java.io.Serializable;
import java.net.URLEncoder;

import com.ytint.wloaa.app.MyApplication;

/**
 * 接口URL实体类
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class URLs implements Serializable {
	// public final static String HOST = "10.1.101.3:9001";
//	public final static String HOST = "221.0.111.130:14401";
//    public final static String HOST = "221.0.111.130:9009";
//    public final static String HOST = "10.200.3.136:8002";
    public static String HOST = "windoer.com:9000";
	public  static String HTTP = "http://";
	public  static String HTTPS = "https://";

	private  static String URL_SPLITTER = "/";

//	public  static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;
	public  static String URL_API_HOST =  URL_SPLITTER;

	//获取用户列表
	public  static String USERLIST=URL_API_HOST+"api/user/get_user_list";
	//获取用户列表
	public  static String COMPANYLIST=URL_API_HOST+"api/company/get_company_list";
	
	//添加jpush推送标记
//	public  static String ADDREGIS=URL_API_HOST+"api/user/update_user_registration";
	
	//上传照片
	public  static String UPLOADPHOTO=URL_API_HOST+"api/upload/file_upload";
	//图片列表
//	public  static String PHOTOLIST=URL_API_HOST+"api/upload/get_file_list";
	
	//获取群发消息列表 任务列表
	public  static String QUNFALIST=URL_API_HOST+"api/notice/get_notice_list";
	
	//发送消息或任务
	public  static String ADDMSG=URL_API_HOST+"api/notice/add_notice_info";
	
	//发送消息或任务
	public  static String MSGDETAIL=URL_API_HOST+"api/notice/get_notice_info";
	
	//发送消息或任务
	public  static String ADDSHENPI=URL_API_HOST+"api/verify/add_apply_info";
	
	//上报下派
	public  static String ADDRS=URL_API_HOST+"api/task/add_task_info";
	
	//获取审批列表
	public  static String SHENPILIST=URL_API_HOST+"api/verify/get_apply_list";
	//获取任务列表
	public  static String TASKLIST=URL_API_HOST+"api/task/get_task_list";
	
	//获取审批详情
	public  static String SHENPIDETAIL=URL_API_HOST+"api/task/get_task_info";
	
	//同意 不同意 审批
	public  static String SHENPI=URL_API_HOST+"api/task/verify_task_info";
	
	// begin
//	public  static String VERIFY_TOKEN_URL = URL_API_HOST+ "loginservice/verifyToken";
	// 登录
	public  static String LOGINURL = URL_API_HOST + "api/user/login";
	// 修改密码
	public  static String PASSWORD = URL_API_HOST + "api/user/update_user_password";

	public  static String GETVERSION = URL_API_HOST+ "api/version/get_version_info";
	/**
	 * 对URL进行格式处理
	 * 
	 * @param path
	 * @return
	 */
	private final static String formatURL(String path) {
		if (path.startsWith("http://") || path.startsWith("https://"))
			return path;
		return "http://" + URLEncoder.encode(path);
	}
}