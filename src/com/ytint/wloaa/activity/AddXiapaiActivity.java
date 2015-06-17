package com.ytint.wloaa.activity;

import java.io.Serializable;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.PeopleList;
import com.ytint.wloaa.bean.QunfaInfo;
import com.ytint.wloaa.bean.URLs;

public class AddXiapaiActivity extends AbActivity {
	String TAG = "AddXiapaiActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	private long people = 0;
	private List<People> peoples;
	
	@AbIocView(id = R.id.select_people)
	Spinner peopleSpinner;
	@AbIocView(id = R.id.addTask)
	Button add;
	@AbIocView(id = R.id.task_info)
	EditText task_info;
	@AbIocView(id = R.id.task_title)
	EditText task_title;
	@AbIocView(id = R.id.search_close)
	TextView search_close;
	@AbIocView(id = R.id.addxiapai_full)
	LinearLayout addxiapai_full;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		setAbContentView(R.layout.layout_addxiapai);
		context = AddXiapaiActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		
		initUi();
		//������ϵ��������
		if(null==peoples||peoples.size()<=0){
			loadPeoples();
		}else{
			people_names = new String[peoples.size()];
			int i = 0;
			for (People cn : peoples) {
				people_names[i] = cn.name;
				i++;
			}
			initSpinner();
		}
		
	}

	private void initUi() {
		
		//���
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// �رռ���
				InputMethodManager imm = (InputMethodManager) AddXiapaiActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
				submitXiaoxi();
			}
		});
		search_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) AddXiapaiActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiapai_full.setClickable(true);
		addxiapai_full.setOnClickListener(keyboard_hide);
	}
	private void initSpinner() {
		// ����ѡ������ArrayAdapter��������
		adapter = new ArrayAdapter<String>(AddXiapaiActivity.this,
				R.layout.spinner_item, people_names);
		// ���������б�ķ��
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// ��adapter ��ӵ�spinner��
		peopleSpinner.setAdapter(adapter);
		// ����Ĭ��ѡ��
//		channelSpinner.setSelection(0);
		// ����Ĭ��ֵ
//		channelSpinner.setVisibility(View.VISIBLE);
		peopleSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						people = peoples.get(arg2).id;
						TextView tv = (TextView) view;
						tv.setTextColor(getResources().getColor(R.color.white)); // ������ɫ
						tv.setGravity(android.view.Gravity.CENTER); // ���þ���

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						arg0.setVisibility(View.VISIBLE);
					}

				});

	}
	/**
	 * �·�����
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
		params.put("androidNoticeInfo.title", task_title.getText().toString());
		params.put("androidNoticeInfo.content", task_info.getText().toString());
		params.put("androidNoticeInfo.push_user_id", loginKey);
		params.put("androidNoticeInfo.notice_type", "1");
		params.put("androidNoticeInfo.receive_user_ids", people+"");
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
								showToast("�����·��ɹ���");
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
	@SuppressLint("NewApi")
	private void loadPeoples() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("������������");
			return;
		}
		mAbHttpUtil.get(URLs.USERLIST ,
				new AbStringHttpResponseListener() {
					// ��ȡ���ݳɹ����������
					@Override
					public void onSuccess(int statusCode, String content) {
						try {
							PeopleList cList = PeopleList.parseJson(content);
							if (cList.code == 200) {
								peoples = cList.getInfo();
								peoples.remove(Integer.parseInt(loginKey)-1);
								application.saveObject((Serializable) peoples,"peoples");
								people_names = new String[peoples.size()];
								int i = 0;
								for (People cn : peoples) {
									people_names[i] = cn.name;
									i++;
								}
								initSpinner();
							} else {
								showToast(cList.msg);
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
