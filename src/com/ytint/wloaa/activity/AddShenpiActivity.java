package com.ytint.wloaa.activity;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.ytint.wloaa.bean.ShenpiInfo;
import com.ytint.wloaa.bean.URLs;

public class AddShenpiActivity extends AbActivity {
	String TAG = "AddShenpiActivity";
	private MyApplication application;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	private long people = 0;
	private List<People> peoples;
	
	Context context = null;
	private String loginKey;
	
	@AbIocView(id = R.id.addshenpi_full)
	LinearLayout addxiapai_full;
//	@AbIocView(id = R.id.select_shenpi_people)
//	Spinner peopleSpinner;
	@AbIocView(id = R.id.commitShenpi)
	Button add;
	@AbIocView(id = R.id.shenpi_title)
	EditText shenpi_title;
	@AbIocView(id = R.id.shenpi_content)
	EditText shenpi_content;
	@AbIocView(id = R.id.shenpi_close)
	TextView shenpi_close;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		setAbContentView(R.layout.layout_addshenpi);
		context = AddShenpiActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		initUi();
//		//������ϵ��������
//		peoples = (List<People>) application.readObject("peoples");
//		if(null==peoples||peoples.size()<=0){
//			loadPeoples();
//		}else{
//			people_names = new String[peoples.size()];
//			int i = 0;
//			for (People cn : peoples) {
//				people_names[i] = cn.pn;
//				i++;
//			}
//			initSpinner();
//		}
		
	}

	private void initUi() {
		
		
		//���
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// �رռ���
				InputMethodManager imm = (InputMethodManager) AddShenpiActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
				submitShenpi();
			}
		});
		shenpi_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) AddShenpiActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		addxiapai_full.setClickable(true);
		addxiapai_full.setOnClickListener(keyboard_hide);
	}
	
	/**
	 * �ύ����
	 * @author wlj
	 * @date 2015-6-16����7:21:20
	 */
	private void submitShenpi(){
		// ��ȡHttp������
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "������������");
			return;
		}
		
		AbRequestParams params = new AbRequestParams();
		params.put("androidApplyVerifyInfo.title", shenpi_title.getText().toString());
		params.put("androidApplyVerifyInfo.content", shenpi_content.getText().toString());
		params.put("androidApplyVerifyInfo.apply_user_id", loginKey);
		Log.d(TAG, String.format("%s?", URLs.ADDSHENPI,
				params));
		mAbHttpUtil.post(URLs.ADDSHENPI ,params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							ShenpiInfo gList = ShenpiInfo
									.parseJson(content);
							if (gList.code == 200) {
								showToast("�����ύ�ɹ���");
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

}
