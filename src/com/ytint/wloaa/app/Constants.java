package com.ytint.wloaa.app;

/**
 * 应用程序配置类：用于保存用户相关信息及设置
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class Constants {

	public final static String APP_CONFIG = "config";
	public final static String CONF_COOKIE = "cookie";

	// 网络类型
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	
	public static final int PAGE_SIZE = 10;// 默认分页大小
	public static final int IMAGE_PAGE_SIZE = 100;// 默认图片
	static final int CACHE_TIME = 60 * 60000;// 缓存失效时间
	
	public static final int SUCCESS =200;
	
	public static final String GATHERSOURCELIST = "GatherSourceist";
	public static final String GATHERSOURCELIST_SHOW = "GatherSourceist_show";
	public static final String GATHERSOURCELIST_HIDE = "GatherSourceist_hide";
	
	public static final String TOPICLIST = "TopicList";
	public static final String TOPICLIST_SHOW = "TopicList_show";
	public static final String TOPICLIST_HIDE = "TopicList_hide";
	//public static final String GATHERSOURCELIST_HIDE = "GatherSourceist_hide";
	
	public static final String HOTTOPNEWS = "HotTopNews";
	public static final String HOTTOPWORDS = "HotTopWords";
	
	
	//数据的类型
	public static final String DATA_TYPE_FLAG = "data_type_flag";
	public final static int GATHERSOURCE_TYPE =1;
	public final static int TOPIC_TYPE = 2;
	public final static int CLASS_TYPE = 3;
	public final static int YUN_SEARCH_TYPE = 4;
	public final static int HOMEHOT_TYPE = 0;
	public static enum DATA_TYPE {
		GATHERSOURCE_TYPE,
		TOPIC_TYPE,
		HOMEHOT_TYPE,
		CLASS_TYPE;
	}
	
	
	
	/** 加载框的文字说明. */
	public static String PROGRESSMESSAGE = "请稍候...";
	
	public final static int HEAD_SHOWCOUNT = 6;
	public static final int MAXGROUPCOUNT = 10;
	
	
    /** 位移动画类型：从上往下移动。 */  
    public static final int UP_TO_DOWN = 1;  
    /** 位移动画类型：从下往上。 */  
    public static final int DOWN_TO_UP = 2;
    
    public static final String END_TIME_DEFAULT= "2020-01-01";
    // 本机imei
 	public static String USER_IMEI_ID = "";

}
