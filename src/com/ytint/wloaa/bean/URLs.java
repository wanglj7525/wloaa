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
    public final static String HOST = "10.200.3.136:9000";
//    public final static String HOST = "122.5.18.196:9061";
	public final static String HTTP = "http://";
	public final static String HTTPS = "https://";

	private final static String URL_SPLITTER = "/";

	public final static String URL_API_HOST = HTTP + HOST + URL_SPLITTER;

	//获取用户列表
	public final static String USERLIST=URL_API_HOST+"api/user/get_user_list";
	
	//添加jpush推送标记
	public final static String ADDREGIS=URL_API_HOST+"api/user/update_user_registration";
	
	//上传照片
	public final static String UPLOADPHOTO=URL_API_HOST+"api/upload/file_upload";
	//图片列表
	public final static String PHOTOLIST=URL_API_HOST+"api/upload/get_file_list";
	
	//获取群发消息列表 任务列表
	public final static String QUNFALIST=URL_API_HOST+"api/notice/get_user_notice_list";
	
	//发送消息或任务
	public final static String ADDMSG=URL_API_HOST+"api/notice/add_notice_doc";
	
	//发送消息或任务
	public final static String ADDSHENPI=URL_API_HOST+"api/verify/add_apply_info";
	
	//获取审批列表
	public final static String SHENPILIST=URL_API_HOST+"api/verify/get_apply_list";
	
	//获取审批详情
	public final static String SHENPIDETAIL=URL_API_HOST+"api/verify/get_apply_info";
	
	//同意 不同意 审批
	public final static String SHENPI=URL_API_HOST+"api/verify/verify_apply_info";
	
	// begin
	public final static String VERIFY_TOKEN_URL = URL_API_HOST
			+ "loginservice/verifyToken";
	// 登录
	public final static String LOGINURL = URL_API_HOST + "loginservice/login";

	// 公告
	public final static String LOAD_NOTICE_URL = URL_API_HOST
			+ "notice/noticeHistory";
	// 用户信息
	public final static String LOAD_USERINFO_URL = URL_API_HOST
			+ "userService/userInfo";
	// 修改密码
	public final static String UPADTE_PASSWORD_URL = URL_API_HOST
			+ "UserService/setPassword";

	public final static String LOAD_HOTTOPNEWS_URL = URL_API_HOST
			+ "Home/HotTopNews";
	public final static String LOAD_HOTTOPWORDS_URL = URL_API_HOST
			+ "Home/HotTopWords";

	// 信源分组
	public final static String GATHERSOURCE_MYGROUP = URL_API_HOST
			+ "GatherSource/mygroup";
	// 信源配置详情
	public final static String GATHERSOURCE_DETAIL = URL_API_HOST
			+ "GatherSource/mygroupConfig";
	// 信源文章列表
	public final static String GATHERSOURCE_GROUPDOCS = URL_API_HOST
			+ "GatherSource/groupDocs";
	// 话题
	public final static String TOPIC_MYGROUP = URL_API_HOST + "topic/mytopic";
	public final static String TOPIC_GROUPDOCS = URL_API_HOST
			+ "topic/topicDocs";
	public final static String TOPIC_ADDTOPIC = URL_API_HOST + "topic/addTopic";

	// 分类舆情
	public final static String CATEGORY = URL_API_HOST
			+ "category/getAttentionList";
	public final static String CATEGORYDOCS = URL_API_HOST
			+ "category/categoryDocs";

	// 云搜索
	public final static String SEARCH = URL_API_HOST + "search/searchDocs";
	public final static String CHANNELS = URL_API_HOST + "search/getChannel";

	// 编报 查询用户所有编报标签
	public final static String ALL_REPORT = URL_API_HOST
			+ "TempFavorites/getFavoritesTag";
	// 查询一篇文章的已添加的编报标签
	public final static String HAS_REPORT = URL_API_HOST
			+ "tempFavorites/getHasFavoritesDocTag";
	// 给一篇文章添加一个编报标签
	public final static String ADD_DOC_REPORT = URL_API_HOST
			+ "tempFavorites/favoritesDoc";
	// 给一篇文章删除一个编报标签
	public final static String DELETE_DOC_REPORT = URL_API_HOST
			+ "tempFavorites/cancelFavoritesDoc";
	// 添加编报标签
	public final static String ADD_REPORT = URL_API_HOST
			+ "tempFavorites/addFavoritesTag";
	// 删除编报标签
	public final static String DELETE_REPORT = URL_API_HOST
			+ "tempFavorites/removeFavoritesTag";

	// 标记 查询用户所有标记标签
	public final static String ALL_MARK = URL_API_HOST + "mark/getUsefulMarks";
	// 查询一篇文章的已添加的标记标签
	public final static String HAS_MARK = URL_API_HOST
			+ "mark/getDocMarkByDocId";
	// 给一篇文章添加一个标记标签
	public final static String ADD_DOC_MARK = URL_API_HOST + "mark/markDoc";
	// 给一篇文章删除一个标记标签
	public final static String DELETE_DOC_MARK = URL_API_HOST
			+ "mark/cancelMarkDoc";
	// 添加标记标签
	public final static String ADD_MARK = URL_API_HOST + "mark/addMark";
	// 删除标记标签
	public final static String DELETE_MARK = URL_API_HOST + "mark/deleteMark";

	// 文章详情
	public final static String DOC_DETAIL = URL_API_HOST
			+ "docService/docDetail";
	// 微博用户信息
	public final static String WEIBO_PERSON = URL_API_HOST
			+ "docService/getWeiboPerson";
	// 最新微博
	public final static String WEIBORELA = URL_API_HOST
			+ "docService/getWeiboRela";
	// 获取文章评论
	public final static String DOC_COMMENT = URL_API_HOST
			+ "docService/getComments";

	// 取信源配置信息接口：/GatherSource/mygroupConfig
	public final static String GATHERSOURCECONFIG = URL_API_HOST
			+ "GatherSource/mygroupConfig";
	// 取主题配置信息接口：/topic/topicConfig
	public final static String TOPICCONFIG = URL_API_HOST + "topic/topicConfig";
	// 取分类配置信息接口：/category/categoryConfig
	public final static String CATEGORYCONFIG = URL_API_HOST
			+ "category/categoryConfig";

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
