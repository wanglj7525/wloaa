package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.http.AbHttpUtil;
import com.ab.view.ioc.AbIocView;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.fragment.AnquanFragment;
import com.ytint.wloaa.fragment.PagesFragment;
import com.ytint.wloaa.fragment.ZhifaFragment;

public class MainActivity extends FragmentActivity {

	private MyApplication application;
	private String loginKey;
	private String userName;
	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
	Context context = null;
	private FragmentManager fm;
	Fragment fragment1,fragment2,fragment3,fragment4 ;
	
	RelativeLayout main_show_keshi_rela;
	RelativeLayout main_show_gonggao_rela;
	RelativeLayout main_show_xiaoxi_rela;
	RelativeLayout main_show_shezhi_rela;
	ImageButton main_show_keshi;
	ImageButton main_show_gonggao;
	ImageButton main_show_xiaoxi;
	ImageButton main_show_shezhi;
	TextView main_show_user;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (MyApplication) this.getApplication();
		context = MainActivity.this;
		mAbHttpUtil.setDebug(true);
		loginKey = application.getProperty("loginKey");
		userName = application.getProperty("userName");
		setContentView(R.layout.activity_main);
		
		//TODO 判断角色 确定第一个按钮显示
		
		fm = getSupportFragmentManager();
		fragment1 = new AnquanFragment();
		fragment2 = new PagesFragment();
		fragment3 = new ZhifaFragment();
		fragment4 = new PagesFragment();
		//初始化的时候需要显示一个fragment，假设我们显示第二个fragment
				//向容器中添加或者替换fragment时必须  开启事务  操作完成后   提交事务
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.showContentFrame, fragment1).commit();
		initUi();
	}
	
	private void initUi(){
		main_show_keshi_rela=(RelativeLayout)findViewById(R.id.main_show_keshi_rela);
		main_show_gonggao_rela=(RelativeLayout)findViewById(R.id.main_show_gonggao_rela);
		main_show_xiaoxi_rela=(RelativeLayout)findViewById(R.id.main_show_xiaoxi_rela);
		main_show_shezhi_rela=(RelativeLayout)findViewById(R.id.main_show_shezhi_rela);
		main_show_keshi=(ImageButton)findViewById(R.id.main_show_keshi);
		main_show_gonggao=(ImageButton)findViewById(R.id.main_show_gonggao);
		main_show_xiaoxi=(ImageButton)findViewById(R.id.main_show_xiaoxi);
		main_show_shezhi=(ImageButton)findViewById(R.id.main_show_shezhi);
		main_show_user=(TextView)findViewById(R.id.main_show_user);
		main_show_user.setText(userName);
		OnClickListener relaClick = new OnClickListener() {
			@Override
			public void onClick(View v) {
				main_show_keshi_rela.setBackgroundResource(R.drawable.bg_leftbutton);
				main_show_gonggao_rela.setBackgroundResource(R.drawable.bg_leftbutton);
				main_show_xiaoxi_rela.setBackgroundResource(R.drawable.bg_leftbutton);
				main_show_shezhi_rela.setBackgroundResource(R.drawable.bg_leftbutton);
				FragmentTransaction ft = fm.beginTransaction();
				switch (v.getId()) {
				case R.id.main_show_keshi_rela:
					main_show_keshi_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment1 );
					ft.commit();
					break;
				case R.id.main_show_gonggao_rela:
					main_show_gonggao_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment2 );
					ft.commit();		
					break;
				case R.id.main_show_xiaoxi_rela:
					main_show_xiaoxi_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment3 );
					ft.commit();
					break;
				case R.id.main_show_shezhi_rela:
					main_show_shezhi_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment4 );
					ft.commit();		
					break;
				case R.id.main_show_keshi:
					main_show_keshi_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment1 );
					ft.commit();
					break;
				case R.id.main_show_gonggao:
					main_show_gonggao_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment2 );
					ft.commit();		
					break;
				case R.id.main_show_xiaoxi:
					main_show_xiaoxi_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment3 );
					ft.commit();
					break;
				case R.id.main_show_shezhi:
					main_show_shezhi_rela.setBackgroundResource(R.drawable.bg_leftbutton_selected);
					ft.replace(R.id.showContentFrame,fragment4 );
					ft.commit();		
					break;
				default:
					break;
				}
			}

		};
		
		
		main_show_keshi_rela.setOnClickListener(relaClick);
		main_show_gonggao_rela.setOnClickListener(relaClick);
		main_show_xiaoxi_rela.setOnClickListener(relaClick);
		main_show_shezhi_rela.setOnClickListener(relaClick);
		main_show_keshi.setOnClickListener(relaClick);
		main_show_gonggao.setOnClickListener(relaClick);
		main_show_xiaoxi.setOnClickListener(relaClick);
		main_show_shezhi.setOnClickListener(relaClick);
	}

}