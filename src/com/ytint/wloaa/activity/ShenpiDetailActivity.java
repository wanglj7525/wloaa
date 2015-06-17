package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Shenpi;
import com.ytint.wloaa.bean.ShenpiInfo;
import com.ytint.wloaa.bean.ShenpiInfoList;
import com.ytint.wloaa.bean.URLs;

public class ShenpiDetailActivity extends AbActivity {
	String TAG = "ShenpiDetailActivity";
	private MyApplication application;
	private AbImageDownloader mAbImageDownloader = null;
	Context context = null;
	
	@AbIocView(id = R.id.sure_shenpi)
	Button sure_shenpi;
	@AbIocView(id = R.id.no_shenpi)
	Button no_shenpi;
	
	@AbIocView(id = R.id.shenpi_detail)
	TextView shenpi_detail;
	@AbIocView(id = R.id.shenpi_title)
	TextView shenpi_title;
	@AbIocView(id = R.id.from_shenpi)
	TextView from_shenpi;
	
	@AbIocView(id = R.id.shenpi_detail_full)
	LinearLayout shenpi_detail_full;
	@AbIocView(id = R.id.shenpi_detail_close)
	TextView shenpi_detail_close;
	
	@AbIocView(id = R.id.show_shenpi_button)
	LinearLayout show_shenpi_button;
	
	@AbIocView(id = R.id.first_verify_user_name)
	TextView first_verify_user_name;
	@AbIocView(id = R.id.first_verify_comment)
	TextView first_verify_comment;
	@AbIocView(id = R.id.second_verify_user_name)
	TextView second_verify_user_name;
	@AbIocView(id = R.id.second_verify_comment)
	TextView second_verify_comment;
	
	
	Integer shenpi_id;
	private Shenpi shenpi = new Shenpi();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		setAbContentView(R.layout.layout_shenpidetail);
		application = (MyApplication) abApplication;
		context=ShenpiDetailActivity.this;
		
		Intent intent = getIntent();
		shenpi_id=intent.getIntExtra("shenpi_id",0);
		
		initUi();
		loadDatas();
	}

	private void initUi() {
		//���
		sure_shenpi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shenpi(1);
			}
		});
		//
		no_shenpi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				shenpi(2);
			}
		});
		shenpi_detail_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) ShenpiDetailActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		shenpi_detail_full.setClickable(true);
		shenpi_detail_full.setOnClickListener(keyboard_hide);
	}
	
	private void shenpi(final int verify_status){

		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("������������");
			return;
		}
		AbRequestParams params = new AbRequestParams();
		params.put("id", shenpi_id+"");
		
		//1��һ����ˣ�2���������
		if (shenpi.first_verify_status==0) {
			//һ�����
			params.put("verify_step", 1+"");
		}else if (shenpi.first_verify_status==1){
			//һ��ͨ�� ���ж������
			params.put("verify_step", 2+"");
		}
		
		//0��δ��ˣ�1�����ͨ����2�����δͨ��
		params.put("verify_status", verify_status+"");
		if (verify_status==1) {
			params.put("comment", "���ͨ��");
		}else{
			params.put("comment", "��˲�ͨ��");
		}
		params.put("user_id", loginKey);
		
		
		mAbHttpUtil.post(URLs.SHENPI ,params,
				new AbStringHttpResponseListener() {
					// ��ȡ���ݳɹ����������
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							
							ShenpiInfo gList = ShenpiInfo
									.parseJson(content);
							if (gList.code == 200) {
								if (verify_status==1) {
									showToast("�����Ѿ�ͬ�⣡");
								}else if (verify_status==2) {
									showToast("�����Ѿ��ܾ���");
								}
								finish();
							} else {
								UIHelper.ToastMessage(context, gList.msg);
							}
							
						} catch (Exception e) {
							e.printStackTrace();
							showToast("���ݽ���ʧ��");
						}
					};

					// ��ʼִ��ǰ
					@Override
					public void onStart() {
						// ��ʾ���ȿ�
						showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast("��������ʧ�ܣ�");
					}

					// ��ɺ���ã�ʧ�ܣ��ɹ�
					@Override
					public void onFinish() {
						// �Ƴ����ȿ�
						removeProgressDialog();
					};

				});
	}
	
	@SuppressLint("NewApi")
	private void loadDatas() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("������������");
			return;
		}
		mAbHttpUtil.get(URLs.SHENPIDETAIL + "?id=" + shenpi_id,
				new AbStringHttpResponseListener() {
					// ��ȡ���ݳɹ����������
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							
							ShenpiInfo gList = ShenpiInfo
									.parseJson(content);
							if (gList.code == 200) {
								shenpi = gList.getInfo();
								from_shenpi.setText(shenpi.apply_user_name);
								shenpi_title.setText(shenpi.title);
								shenpi_detail.setText(shenpi.content);
								
								int status = Integer.parseInt(application.getProperty("status").toString());
								if (status==0) {
									//û������Ȩ��
									show_shenpi_button.setVisibility(View.GONE);
								}
								
								if (status==1&&shenpi.first_verify_status==0) {
									show_shenpi_button.setVisibility(View.VISIBLE);
								}
								
								if (status==2&&shenpi.first_verify_status==1&&shenpi.second_verify_status==0) {
									show_shenpi_button.setVisibility(View.VISIBLE);
								}
								
								if (shenpi.first_verify_status==0) {
									first_verify_user_name.setText("δ����");
								}else{
									first_verify_user_name.setText(shenpi.first_verify_user_name);
									first_verify_comment.setText(shenpi.first_verify_comment);
								}
								if (shenpi.second_verify_status!=0) {
									second_verify_user_name.setText(shenpi.second_verify_user_name);
									second_verify_comment.setText(shenpi.second_verify_comment);
								}
							} else {
								UIHelper.ToastMessage(context, gList.msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							showToast("���ݽ���ʧ��");
						}
					};

					// ��ʼִ��ǰ
					@Override
					public void onStart() {
						// ��ʾ���ȿ�
						showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast("��������ʧ�ܣ�");
					}

					// ��ɺ���ã�ʧ�ܣ��ɹ�
					@Override
					public void onFinish() {
						// �Ƴ����ȿ�
						removeProgressDialog();
					};

				});
	}

}
