package com.ytint.wloaa.bean;


/**
 * ��������
 * @author wlj
 * @date 2015-6-12����10:33:53
 */
public class Xiapai implements java.io.Serializable{
	public Integer id;//��Ϣ����
	public String title;// ���� VARCHAR(256)*
	public String create_time;// ���ʱ�� DATETIME
	public String content;// ��Ϣ����
	public Integer push_user_id;//�����˱��
	public String push_user_name;//�����˱��
	public String receive_user_name;//�����˱��
	public Integer push_flag;// �Ƿ�������
	public String push_range;// ���ͷ�Χ
	public Integer notice_type;//0��Ⱥ����Ϣ��1����������
	public String receive_user_ids;//�����˱���б�
	public Long push_msg_id = 0L;

}