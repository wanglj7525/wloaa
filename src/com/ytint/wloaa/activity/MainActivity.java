package com.ytint.wloaa.activity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.BadgeView;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.bean.Version;
import com.ytint.wloaa.bean.VersionInfo;
import com.ytint.wloaa.fragment.GonggaoFragment;
import com.ytint.wloaa.fragment.ShezhiFragment;
import com.ytint.wloaa.fragment.TaskFragment;
import com.ytint.wloaa.fragment.XiaoxiFragment;
import com.ytint.wloaa.service.AppUpgradeService;

public class MainActivity extends BaseActivity {

	private MyApplication application;
	private String loginKey;
	private String userName;
	private String userType;
	private String departmentId;
	private String isLogin;
	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
	Context context = null;
	private FragmentManager fm;
	Fragment fragment1,fragment2,fragment3,fragment4 ;
	
	RelativeLayout main_show_keshi_rela;
	RelativeLayout main_show_gonggao_rela;
	RelativeLayout main_show_xiaoxi_rela;
	RelativeLayout main_show_shezhi_rela;
	ImageView main_show_keshi;
	TextView main_show_keshiname;
	ImageView main_show_gonggao;
	ImageView main_show_xiaoxi;
	ImageView main_show_shezhi;
	TextView main_show_user;
	
	private int mVersionCode;
	private String mVersionName;
	private int mLatestVersionCode = 1;
	private String mLatestVersionUpdate = "mLatestVersionUpdate";
	private String mLatestVersionDownload = "";
	private boolean showUpdate = true;
	private Date lastShowUpdateTime;
	String host="";
	int news=0;
	int news0=0;
	BadgeView badgeView ;
	BadgeView badgeView0 ;
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
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application= (MyApplication) this.getApplication();
		context = MainActivity.this;
		mAbHttpUtil.setDebug(true);
		setContentView(R.layout.activity_main);
		loginKey = application.getProperty("loginKey");
		userName = application.getProperty("userName");
		isLogin = application.getProperty("is_login");
		userType = application.getProperty("userType");
		departmentId = application.getProperty("departmentId");
		String if_task_power=application.getProperty("if_task_power");
		if (null==if_task_power) {
			//没有取到权限 需要重新登录
			Intent intent=new Intent(MainActivity.this,LoginActivity.class);
			startActivity(intent);
			return;
		}
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		if (isLogin==null) {
			//未登录
			Intent intent=new Intent(MainActivity.this,LoginActivity.class);
			startActivity(intent);
			return;
		}else if(!isLogin.equals("1")){
			//未登录
			Intent intent=new Intent(MainActivity.this,LoginActivity.class);
			startActivity(intent);
			return;
		}
		badgeView = new BadgeView(this);
		badgeView0 = new BadgeView(this);
		main_show_keshi_rela=(RelativeLayout)findViewById(R.id.main_show_keshi_rela);
		main_show_gonggao_rela=(RelativeLayout)findViewById(R.id.main_show_gonggao_rela);
		main_show_xiaoxi_rela=(RelativeLayout)findViewById(R.id.main_show_xiaoxi_rela);
		main_show_shezhi_rela=(RelativeLayout)findViewById(R.id.main_show_shezhi_rela);
		main_show_keshi=(ImageView)findViewById(R.id.main_show_keshi);
		main_show_keshiname=(TextView)findViewById(R.id.main_show_keshiname);
		main_show_gonggao=(ImageView)findViewById(R.id.main_show_gonggao);
		main_show_xiaoxi=(ImageView)findViewById(R.id.main_show_xiaoxi);
		main_show_shezhi=(ImageView)findViewById(R.id.main_show_shezhi);
		main_show_user=(TextView)findViewById(R.id.main_show_user);
		main_show_user.setText("您好，" + userName);
		
		String keshiname="任务管理";
		int keshiimage=R.drawable.menu_zhiliang;
		fm = getSupportFragmentManager();
		fragment1 = new TaskFragment();
		fragment2 = new GonggaoFragment();
		fragment3 = new XiaoxiFragment();
		fragment4 = new ShezhiFragment();
		if (userType.equals("0")||userType.equals("1")||userType.equals("2")) {
			//、管理员，局长、副局长
			keshiname="任务列表";
			keshiimage=R.drawable.menu_liebiao;
			fragment1 = new TaskFragment();
		}else{
			keshiimage=R.drawable.menu_zhifa;
			fragment1 =new TaskFragment();
			
//			//科长，科员
//			if (departmentId.equals("1")) {
//				//质量
////				keshiname="质量检查";
//				keshiimage=R.drawable.menu_zhiliang;
//				fragment1 = new ZhiliangFragment();
//			}else if (departmentId.equals("2")) {
//				//安全
////				keshiname="安全检查";
//				keshiimage=R.drawable.menu_anquan;
//				fragment1 = new AnquanFragment();
//			}else if (departmentId.equals("3")){
//				//执法
////				keshiname="执法管理";
//				keshiimage=R.drawable.menu_zhifa;
//				fragment1 = new ZhifaFragment();
//			}else{
//				//其他部门
////				keshiname="日常办公";
//				keshiimage=R.drawable.menu_richangbangong;
//				fragment1 = new RichangbangongFragment();
//			}
		}
			
		
		main_show_keshi.setImageResource(keshiimage);
		main_show_keshiname.setText(keshiname);
		
		//初始化的时候需要显示一个fragment，假设我们显示第二个fragment
		//向容器中添加或者替换fragment时必须  开启事务  操作完成后   提交事务
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.showContentFrame, fragment1).commit();
		initUi();
		if (URLs.timer!= null) {  
			URLs.timer.cancel();  
			URLs.timer = null;  
        }  
		URLs.timer = new Timer();  
		URLs.timer.scheduleAtFixedRate(new Mytack(), 1, 10000);  
		// 检查版本
		initLocalVersion();
		if (showUpdate) {
			getNewVersion();
		}
	}
	String TAG = "MainActivity";
	protected void updateTitle() {  
		//请求查询消息和公告的未读数量
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		String url = String.format(
				"%s?user_id=%s&notice_type=0",
				host+URLs.NEWNUM, loginKey);
		Log.d(TAG, url);
		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				Log.d(TAG, content);
				try {
					JSONObject myJsonObject = new JSONObject(content); 
					JSONObject infoJsonObject=myJsonObject.getJSONObject("info");
						//公告
						news0=infoJsonObject.getInt("gg");
						badgeView0.setHideOnZero(news0);
						news=infoJsonObject.getInt("xx");
						badgeView.setHideOnZero(news);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
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
    private Handler changeTitleHandler = new Handler() {  
        @Override  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            case 1:  
                updateTitle();  
                break;  
            }  
        }  
    };  
    private class Mytack extends TimerTask {// public abstract class TimerTask implements Runnable{}  
        @Override  
        public void run() {  
            Message msg = new Message();  
            msg.what = 1;  
            changeTitleHandler.sendMessage(msg);  
        }  
    }  
	private void initUi(){
		
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
//					news=0;
//					badgeView.setHideOnZero(news);
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
		
		
		badgeView.setTargetView(main_show_xiaoxi_rela);
		badgeView.setHideOnZero(news);
		badgeView.setBadgeMargin(50, 20, 10, 40);
		
		badgeView0.setTargetView(main_show_gonggao_rela);
		badgeView0.setHideOnZero(news);
		badgeView0.setBadgeMargin(50, 20, 10, 40);
		
	}
	public void initLocalVersion() {
		PackageInfo pinfo;
		try {
			pinfo = this.getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			mVersionCode = pinfo.versionCode;
			mVersionName = pinfo.versionName;
	        application.setProperty(Constants.VERSION_NAME, mVersionName);
	        application.setProperty(Constants.VERSION_CODE, String.valueOf(mVersionCode));
			String lastDateStr = application.getProperty("showupdatetime");
			if (null != lastDateStr) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				try {
					lastShowUpdateTime = format.parse(lastDateStr);
					long diff = new Date().getTime()
							- lastShowUpdateTime.getTime();
					if (diff <= Constants.DIFFTIME) {
						showUpdate = false;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			Log.e("mVersionCode:", String.valueOf(mVersionCode));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取版本信息
	 */
	public void getNewVersion() {
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "网络连接失败！");
			return;
		} else {
			mAbHttpUtil.get(host+URLs.GETVERSION + "?id="+mVersionCode, new AbStringHttpResponseListener() {
				@Override
				public void onSuccess(int statusCode, String content) {
					try {
						VersionInfo vi = VersionInfo.parseJson(content);
						if (vi.code == 200) {
							Version v = vi.getInfo();
							mLatestVersionCode = v.id;
							mLatestVersionUpdate = "";
							for (String str : v.introduce) {
								mLatestVersionUpdate += str + "<br>";
							}
							mLatestVersionDownload = v.download_address;
							checkNewVersion();
						} else {
							UIHelper.ToastMessage(context, vi.msg);
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

	/**
	 * 检查版本
	 */
	public void checkNewVersion() {
		if (mVersionCode < mLatestVersionCode) {
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String t = format.format(new Date());
			application.setProperty("showupdatetime", t);
			new AlertDialog.Builder(this)
					.setTitle(R.string.check_new_version)
					.setMessage(Html.fromHtml(mLatestVersionUpdate))
					.setPositiveButton(R.string.app_upgrade_confirm,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											MainActivity.this,
											AppUpgradeService.class);
									intent.putExtra("downloadUrl",
											mLatestVersionDownload);
									startService(intent);
								}
							}).create().show();
//					.setNegativeButton(R.string.app_upgrade_cancel,
//							new DialogInterface.OnClickListener() {
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//								}
//							}).create().show();
		}

		if (mVersionCode >= mLatestVersionCode) {
			// Toast.makeText(this, R.string.check_new_version_latest,
			// Toast.LENGTH_SHORT).show();
			File updateFile = new File(Environment
					.getExternalStorageDirectory().getPath()
					+ Constants.DOWNLOADPATH, getResources().getString(
					R.string.app_name)
					+ ".apk");
			if (updateFile.exists()) {
				// 当不需要的时候，清除之前的下载文件，避免浪费用户空间
				updateFile.delete();
			}
		}
	}
}