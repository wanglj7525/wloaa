package com.ytint.wloaa.bean;

import java.io.Serializable;
import java.net.URLEncoder;

/**
 * �ӿ�URLʵ����
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

	//��ȡ�û��б�
	public final static String USERLIST=URL_API_HOST+"api/user/get_user_list";
	
	//���jpush���ͱ��
	public final static String ADDREGIS=URL_API_HOST+"api/user/update_user_registration";
	
	//�ϴ���Ƭ
	public final static String UPLOADPHOTO=URL_API_HOST+"api/upload/file_upload";
	//ͼƬ�б�
	public final static String PHOTOLIST=URL_API_HOST+"api/upload/get_file_list";
	
	//��ȡȺ����Ϣ�б� �����б�
	public final static String QUNFALIST=URL_API_HOST+"api/notice/get_user_notice_list";
	
	//������Ϣ������
	public final static String ADDMSG=URL_API_HOST+"api/notice/add_notice_doc";
	
	//������Ϣ������
	public final static String ADDSHENPI=URL_API_HOST+"api/verify/add_apply_info";
	
	//��ȡ�����б�
	public final static String SHENPILIST=URL_API_HOST+"api/verify/get_apply_list";
	
	//��ȡ��������
	public final static String SHENPIDETAIL=URL_API_HOST+"api/verify/get_apply_info";
	
	//ͬ�� ��ͬ�� ����
	public final static String SHENPI=URL_API_HOST+"api/verify/verify_apply_info";
	
	// begin
	public final static String VERIFY_TOKEN_URL = URL_API_HOST
			+ "loginservice/verifyToken";
	// ��¼
	public final static String LOGINURL = URL_API_HOST + "loginservice/login";

	// ����
	public final static String LOAD_NOTICE_URL = URL_API_HOST
			+ "notice/noticeHistory";
	// �û���Ϣ
	public final static String LOAD_USERINFO_URL = URL_API_HOST
			+ "userService/userInfo";
	// �޸�����
	public final static String UPADTE_PASSWORD_URL = URL_API_HOST
			+ "UserService/setPassword";

	public final static String LOAD_HOTTOPNEWS_URL = URL_API_HOST
			+ "Home/HotTopNews";
	public final static String LOAD_HOTTOPWORDS_URL = URL_API_HOST
			+ "Home/HotTopWords";

	// ��Դ����
	public final static String GATHERSOURCE_MYGROUP = URL_API_HOST
			+ "GatherSource/mygroup";
	// ��Դ��������
	public final static String GATHERSOURCE_DETAIL = URL_API_HOST
			+ "GatherSource/mygroupConfig";
	// ��Դ�����б�
	public final static String GATHERSOURCE_GROUPDOCS = URL_API_HOST
			+ "GatherSource/groupDocs";
	// ����
	public final static String TOPIC_MYGROUP = URL_API_HOST + "topic/mytopic";
	public final static String TOPIC_GROUPDOCS = URL_API_HOST
			+ "topic/topicDocs";
	public final static String TOPIC_ADDTOPIC = URL_API_HOST + "topic/addTopic";

	// ��������
	public final static String CATEGORY = URL_API_HOST
			+ "category/getAttentionList";
	public final static String CATEGORYDOCS = URL_API_HOST
			+ "category/categoryDocs";

	// ������
	public final static String SEARCH = URL_API_HOST + "search/searchDocs";
	public final static String CHANNELS = URL_API_HOST + "search/getChannel";

	// �౨ ��ѯ�û����б౨��ǩ
	public final static String ALL_REPORT = URL_API_HOST
			+ "TempFavorites/getFavoritesTag";
	// ��ѯһƪ���µ�����ӵı౨��ǩ
	public final static String HAS_REPORT = URL_API_HOST
			+ "tempFavorites/getHasFavoritesDocTag";
	// ��һƪ�������һ���౨��ǩ
	public final static String ADD_DOC_REPORT = URL_API_HOST
			+ "tempFavorites/favoritesDoc";
	// ��һƪ����ɾ��һ���౨��ǩ
	public final static String DELETE_DOC_REPORT = URL_API_HOST
			+ "tempFavorites/cancelFavoritesDoc";
	// ��ӱ౨��ǩ
	public final static String ADD_REPORT = URL_API_HOST
			+ "tempFavorites/addFavoritesTag";
	// ɾ���౨��ǩ
	public final static String DELETE_REPORT = URL_API_HOST
			+ "tempFavorites/removeFavoritesTag";

	// ��� ��ѯ�û����б�Ǳ�ǩ
	public final static String ALL_MARK = URL_API_HOST + "mark/getUsefulMarks";
	// ��ѯһƪ���µ�����ӵı�Ǳ�ǩ
	public final static String HAS_MARK = URL_API_HOST
			+ "mark/getDocMarkByDocId";
	// ��һƪ�������һ����Ǳ�ǩ
	public final static String ADD_DOC_MARK = URL_API_HOST + "mark/markDoc";
	// ��һƪ����ɾ��һ����Ǳ�ǩ
	public final static String DELETE_DOC_MARK = URL_API_HOST
			+ "mark/cancelMarkDoc";
	// ��ӱ�Ǳ�ǩ
	public final static String ADD_MARK = URL_API_HOST + "mark/addMark";
	// ɾ����Ǳ�ǩ
	public final static String DELETE_MARK = URL_API_HOST + "mark/deleteMark";

	// ��������
	public final static String DOC_DETAIL = URL_API_HOST
			+ "docService/docDetail";
	// ΢���û���Ϣ
	public final static String WEIBO_PERSON = URL_API_HOST
			+ "docService/getWeiboPerson";
	// ����΢��
	public final static String WEIBORELA = URL_API_HOST
			+ "docService/getWeiboRela";
	// ��ȡ��������
	public final static String DOC_COMMENT = URL_API_HOST
			+ "docService/getComments";

	// ȡ��Դ������Ϣ�ӿڣ�/GatherSource/mygroupConfig
	public final static String GATHERSOURCECONFIG = URL_API_HOST
			+ "GatherSource/mygroupConfig";
	// ȡ����������Ϣ�ӿڣ�/topic/topicConfig
	public final static String TOPICCONFIG = URL_API_HOST + "topic/topicConfig";
	// ȡ����������Ϣ�ӿڣ�/category/categoryConfig
	public final static String CATEGORYCONFIG = URL_API_HOST
			+ "category/categoryConfig";

	/**
	 * ��URL���и�ʽ����
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
