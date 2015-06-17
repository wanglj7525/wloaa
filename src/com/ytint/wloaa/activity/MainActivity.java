package com.ytint.wloaa.activity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import cn.jpush.android.api.JPushInterface;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.PeopleInfo;
import com.ytint.wloaa.bean.URLs;

public class MainActivity extends TabActivity {
	private TabHost tabHost;
	private RadioGroup radioGroup;
	private RadioButton radio_home;
	private RadioButton radio_chat;
	private RadioButton radio_shenpi;
	private RadioButton radio_xiapai;
	private int checked_id;
	private int clicked_id;
	private MyApplication application;
	private String loginKey;
	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
	Context context = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		application = (MyApplication) this.getApplication();
		context=MainActivity.this;
		mAbHttpUtil.setDebug(true);
		//TODO 以后可以改成该手机电话号 或者 用户ID 等唯一标识
		application.setProperty("loginKey", "1");
		
		radio_home = (RadioButton) findViewById(R.id.radio_home);
		radio_chat = (RadioButton) findViewById(R.id.radio_chat);
		radio_shenpi = (RadioButton) findViewById(R.id.radio_shenpi);
		radio_xiapai = (RadioButton) findViewById(R.id.radio_xiapai);

		tabHost = getTabHost();
		tabHost.addTab(tabHost.newTabSpec("Home").setIndicator("Home")
				.setContent(new Intent(this, HomeActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("CollectingSource")
				.setIndicator("CollectingSource")
				.setContent(new Intent(this, QunfaActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("shenpi").setIndicator("Shenpi")
				.setContent(new Intent(this, ShenpiActivity.class)));
		tabHost.addTab(tabHost.newTabSpec("xiapai").setIndicator("Xiapai")
				.setContent(new Intent(this, XiapaiActivity.class)));

		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioGroup.setOnCheckedChangeListener(checkedChangeListener);

		radio_home.setOnClickListener(clickListener);
		radio_chat.setOnClickListener(clickListener);
		radio_shenpi.setOnClickListener(clickListener);
		radio_xiapai.setOnClickListener(clickListener);
		
		checked_id = R.id.radio_home;
		clicked_id = R.id.radio_home;
		
		init();
	}
	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
	private void init(){
		 JPushInterface.init(getApplicationContext());
		 String registrationid=JPushInterface.getRegistrationID(context);
		 
		 //给用户添加jpush标记
		 System.out.println("******************"+registrationid);
		 if (registrationid!=null) {
			 // 获取Http工具类
			 final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
			 mAbHttpUtil.setDebug(true);
			 if (!application.isNetworkConnected()) {
				 UIHelper.ToastMessage(context, "请检查网络连接");
				 return;
			 }
			 loginKey = application.getProperty("loginKey");
			 AbRequestParams params = new AbRequestParams();
			 params.put("user_id", loginKey);
			 params.put("jpush_registration_id", registrationid);
			 mAbHttpUtil.post(URLs.ADDREGIS ,params,
					 new AbStringHttpResponseListener() {
				 @Override
				 public void onSuccess(int statusCode, String content) {
					 System.out.println(content);
					 try {
						 PeopleInfo cList = PeopleInfo.parseJson(content);
						 if (cList.code == 200) {
							 People peoples = cList.getInfo();
							 application.setProperty("status", peoples.status+"");
						 } else {
							 UIHelper.ToastMessage(context, cList.msg);
						 }
					 } catch (Exception e) {
						 e.printStackTrace();
						 UIHelper.ToastMessage(context, "数据解析失败");
					 }
				 }
				 
				 @Override
				 public void onFailure(int statusCode, String content,
						 Throwable error) {
					 UIHelper.ToastMessage(context, "网络连接失败！");
				 }
				 
				 @Override
				 public void onStart() {
				 }
				 
				 // 完成后调用
				 @Override
				 public void onFinish() {
				 };
			 });
			
		}
	}
	
	
	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.radio_home:
					if(checked_id == v.getId()){
						if(clicked_id!=checked_id){
							clicked_id = checked_id;
						}else{
							((HomeActivity)(getLocalActivityManager().getCurrentActivity())).refresh();;
						}
					}
					break;
				case R.id.radio_chat:
					if(checked_id == v.getId()){
						if(clicked_id!=checked_id){
							clicked_id = checked_id;
						}else{
							@SuppressWarnings("deprecation")
							QunfaActivity a = ((QunfaActivity)(getLocalActivityManager().getCurrentActivity()));
							a.refresh_show_page();
						}
					}
					break;
				case R.id.radio_shenpi:
					if(checked_id == v.getId()){
						if(clicked_id!=checked_id){
							clicked_id = checked_id;
						}else{
							@SuppressWarnings("deprecation")
							ShenpiActivity a = ((ShenpiActivity)(getLocalActivityManager().getCurrentActivity()));
							a.refresh_show_page();
						}
					}
					break;
				case R.id.radio_xiapai:
					if(checked_id == v.getId()){
						if(clicked_id!=checked_id){
							clicked_id = checked_id;
						}else{
							@SuppressWarnings("deprecation")
							XiapaiActivity a = ((XiapaiActivity)(getLocalActivityManager().getCurrentActivity()));
							a.refresh_show_page();
						}
					}
					break;
				default:
					break;
			}
		}
	};


	@Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			checked_id = checkedId;
			switch (checkedId) {
			case R.id.radio_home:
				tabHost.setCurrentTab(0);
				break;
			case R.id.radio_chat:
				tabHost.setCurrentTab(1);
				break;
			case R.id.radio_shenpi:
				tabHost.setCurrentTab(2);
				break;
			case R.id.radio_xiapai:
				tabHost.setCurrentTab(3);
				break;
//			case R.id.radio_my:
//				tabHost.setCurrentTab(4);
//				break;
			default:
				break;
			}
		}
	};

}
