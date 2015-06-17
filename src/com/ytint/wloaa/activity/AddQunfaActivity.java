package com.ytint.wloaa.activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;

public class AddQunfaActivity extends BaseActivity {
	String TAG = "AddQunfaActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	@AbIocView(id = R.id.addxiaoxi_full)
	LinearLayout addxiaoxi_full;
	@AbIocView(id = R.id.commitXiaoxi)
	Button add;
	@AbIocView(id = R.id.xiaoxi_info)
	EditText xiaoxi_info;
	@AbIocView(id = R.id.xiaoxi_title)
	EditText xiaoxi_title;
	@AbIocView(id = R.id.xiaoxi_close)
	TextView xiaoxi_close;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		setAbContentView(R.layout.layout_addqunfai);
		context = AddQunfaActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		initUi();
		
	}

	private void initUi() {
		
		//���
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// �رռ���
				InputMethodManager imm = (InputMethodManager) AddQunfaActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
				submitXiaoxi();
			}
		});
		xiaoxi_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) AddQunfaActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiaoxi_full.setClickable(true);
		addxiaoxi_full.setOnClickListener(keyboard_hide);
	}
	
	/**
	 * Ⱥ����Ϣ
	 * @author wlj
	 * @date 2015-6-16����7:21:20
	 */
	private void submitXiaoxi(){
		// ��ȡHttp������
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "������������");
			return;
		}
		
		AbRequestParams params = new AbRequestParams();
		params.put("androidNoticeInfo.title", xiaoxi_title.getText().toString());
		params.put("androidNoticeInfo.content", xiaoxi_info.getText().toString());
		params.put("androidNoticeInfo.push_user_id", loginKey);
		params.put("androidNoticeInfo.notice_type", "0");
		Log.d(TAG, String.format("%s?", URLs.ADDMSG,
				params));
		mAbHttpUtil.post(URLs.ADDMSG ,params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							QunfaInfo gList = QunfaInfo
									.parseJson(content);
							if (gList.code == 200) {
								showToast("��Ϣ�Ѿ����ͣ�");
								finish();
							} else {
								UIHelper.ToastMessage(context, gList.msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							UIHelper.ToastMessage(context, "���ݽ���ʧ��");
						}
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						UIHelper.ToastMessage(context, "��������ʧ�ܣ�");
					}

					@Override
					public void onStart() {
						showProgressDialog(null);
					}

					// ��ɺ����
					@Override
					public void onFinish() {
						finish();
					};
				});
	}
//	@SuppressLint("NewApi")
//	private void loadPeoples() {
//		try {
//			String content="{\"result\":\"success\",\"msg\":\"�����ɹ�\",\"code\":\"200\",\"info\":[" +
//					"{\"pid\":\"1\",\"pcall\":\"18660019999\",\"pn\":\"��Ǯ��\",\"sid\":\"0\"}," +
//					"{\"pid\":\"2\",\"pcall\":\"18660019998\",\"pn\":\"����֣\",\"sid\":\"1\"}," +
//					"{\"pid\":\"3\",\"pcall\":\"18660019997\",\"pn\":\"���ұ�\",\"sid\":\"2\"}]}";
//			
//			PeopleList cList = PeopleList.parseJson(content);
//			if (cList.code == 200) {
//				peoples = cList.getInfo();
//				application.saveObject((Serializable) peoples,"peoples");
//				people_names = new String[peoples.size()];
//				int i = 0;
//				for (People cn : peoples) {
//					people_names[i] = cn.pn;
//					i++;
//				}
//			} else {
//				showToast(cList.msg);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			showToast("���ݽ���ʧ��");
//		}
//		
////		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
////		String loginKey = application.getProperty("loginKey");
////		if (!application.isNetworkConnected()) {
////			showToast("������������");
////			return;
////		}
////		mAbHttpUtil.get(URLs.CHANNELS + "?access_token=" + loginKey,
////				new AbStringHttpResponseListener() {
////					// ��ȡ���ݳɹ����������
////					@Override
////					public void onSuccess(int statusCode, String content) {
////						try {
////							content="{\"result\":\"success\",\"msg\":\"�����ɹ�\",\"code\":\"200\",\"info\":[" +
////									"{\"pid\":\"1\",\"pcall\":\"18660019999\",\"pn\":\"��Ǯ��\",\"sid\":\"0\"}," +
////									"{\"pid\":\"2\",\"pcall\":\"18660019998\",\"pn\":\"����֣\",\"sid\":\"1\"}," +
////									"{\"pid\":\"3\",\"pcall\":\"18660019997\",\"pn\":\"���ұ�\",\"sid\":\"2\"}]}";
////							
////							PeopleList cList = PeopleList.parseJson(content);
////							if (cList.code == 200) {
////								peoples = cList.getInfo();
////								application.saveObject((Serializable) peoples,"peoples");
////								people_names = new String[peoples.size()];
////								int i = 0;
////								for (People cn : peoples) {
////									people_names[i] = cn.pn;
////									i++;
////								}
////								initSpinner();
////							} else {
////								showToast(cList.msg);
////							}
////						} catch (Exception e) {
////							e.printStackTrace();
////							showToast("���ݽ���ʧ��");
////						}
////					};
////
////					// ��ʼִ��ǰ
////					@Override
////					public void onStart() {
////						// ��ʾ���ȿ�
////						showProgressDialog();
////					}
////
////					@Override
////					public void onFailure(int statusCode, String content,
////							Throwable error) {
////						showToast("��������ʧ�ܣ�");
////					}
////
////					// ��ɺ���ã�ʧ�ܣ��ɹ�
////					@Override
////					public void onFinish() {
////						// �Ƴ����ȿ�
////						removeProgressDialog();
////					};
////
////				});
//	}
//
}
