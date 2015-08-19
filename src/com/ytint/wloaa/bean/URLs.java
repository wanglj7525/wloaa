package com.ytint.wloaa.bean;

import java.io.Serializable;
import java.net.URLEncoder;

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
    public final static String HOST = "windoer.com:9000";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	private final static String URL_SPLITTER = "/";

	public final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;

	//获取用户列表
	public final static String USERLIST=URL_API_HOST+"api/user/get_user_list";
	//获取用户列表
	public final static String COMPANYLIST=URL_API_HOST+"api/company/get_company_list";
	
	//添加jpush推送标记
	public final static String ADDREGIS=URL_API_HOST+"api/user/update_user_registration";
	
	//上传照片
	public final static String UPLOADPHOTO=URL_API_HOST+"api/upload/file_upload";
	//图片列表
	public final static String PHOTOLIST=URL_API_HOST+"api/upload/get_file_list";
	
	//获取群发消息列表 任务列表
	public final static String QUNFALIST=URL_API_HOST+"api/notice/get_notice_list";
	
	//发送消息或任务
	public final static String ADDMSG=URL_API_HOST+"api/notice/add_notice_info";
	
	//发送消息或任务
	public final static String MSGDETAIL=URL_API_HOST+"api/notice/get_notice_info";
	
	//发送消息或任务
	public final static String ADDSHENPI=URL_API_HOST+"api/verify/add_apply_info";
	
	//上报下派
	public final static String ADDRS=URL_API_HOST+"api/task/add_task_info";
	
	//获取审批列表
	public final static String SHENPILIST=URL_API_HOST+"api/verify/get_apply_list";
	//获取任务列表
	public final static String TASKLIST=URL_API_HOST+"api/task/get_task_list";
	
	//获取审批详情
	public final static String SHENPIDETAIL=URL_API_HOST+"api/task/get_task_info";
	
	//同意 不同意 审批
	public final static String SHENPI=URL_API_HOST+"api/verify/verify_apply_info";
	
	// begin
	public final static String VERIFY_TOKEN_URL = URL_API_HOST
			+ "loginservice/verifyToken";
	// 登录
	public final static String LOGINURL = URL_API_HOST + "api/user/login";
	// 修改密码
	public final static String PASSWORD = URL_API_HOST + "api/user/update_user_password";

	// 公告
	public final static String LOAD_NOTICE_URL = URL_API_HOST
			+ "notice/noticeHistory";
	// 用户信息
	public final static String LOAD_USERINFO_URL = URL_API_HOST
			+ "userService/userInfo";
	// 修改密码
	public final static String UPADTE_PASSWORD_URL = URL_API_HOST
			+ "UserService/setPassword";

	// 取分类配置信息接口：/category/categoryConfig
	public final static String CATEGORYCONFIG = URL_API_HOST
			+ "category/categoryConfig";
	public final static String GETVERSION = URL_API_HOST
			+ "cdpt/api/getversion";
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