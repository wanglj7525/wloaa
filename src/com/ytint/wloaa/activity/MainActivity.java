package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ytint.wloaa.R;
import com.ytint.wloaa.fragment.CommunityFragment;
import com.ytint.wloaa.fragment.FindPeopleFragment;
import com.ytint.wloaa.fragment.HomeFragment;
import com.ytint.wloaa.fragment.MenuFragment;
import com.ytint.wloaa.fragment.MenuFragment.SLMenuListOnItemClickListener;
import com.ytint.wloaa.fragment.PagesFragment;
import com.ytint.wloaa.fragment.PhotosFragment;
import com.ytint.wloaa.fragment.WhatsHotFragment;

public class MainActivity extends SlidingFragmentActivity implements SLMenuListOnItemClickListener{

private SlidingMenu mSlidingMenu;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("wloaa");
//		setTitle(R.string.sliding_title);
        setContentView(R.layout.frame_content);

        //set the Behind View
        setBehindContentView(R.layout.frame_left_menu);
        
        // customize the SlidingMenu
        mSlidingMenu = getSlidingMenu();
        mSlidingMenu.setMode(SlidingMenu.LEFT);//设置左右都可以划出SlidingMenu菜单
        mSlidingMenu.setSecondaryShadowDrawable(R.drawable.drawer_shadow);
        
//      mSlidingMenu.setShadowWidth(5);
//      mSlidingMenu.setBehindOffset(100);
        mSlidingMenu.setShadowDrawable(R.drawable.drawer_shadow);//设置阴影图片
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width); //设置阴影图片的宽度
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset); //SlidingMenu划出时主页面显示的剩余宽度
        mSlidingMenu.setFadeDegree(0.35f);
        //设置SlidingMenu 的手势模式
        //TOUCHMODE_FULLSCREEN 全屏模式，在整个content页面中，滑动，可以打开SlidingMenu
        //TOUCHMODE_MARGIN 边缘模式，在content页面中，如果想打开SlidingMenu,你需要在屏幕边缘滑动才可以打开SlidingMenu
        //TOUCHMODE_NONE 不能通过手势打开SlidingMenu
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        
        //设置 SlidingMenu 内容
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.left_menu, new MenuFragment());
        fragmentTransaction.replace(R.id.content, new HomeFragment());
        
        fragmentTransaction.commit();
        
        //使用左上方icon可点，这样在onOptionsItemSelected里面才可以监听到R.id.home
//        getActionBar().setDisplayHomeAsUpEnabled(true);
//        getActionBar().setLogo(R.drawable.ic_logo);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            
            toggle(); //动态判断自动关闭或开启SlidingMenu
//          getSlidingMenu().showMenu();//显示SlidingMenu
//          getSlidingMenu().showContent();//显示内容
            return true;
        case R.id.action_refresh:
            
        	Toast.makeText(getApplicationContext(), R.string.refresh, Toast.LENGTH_SHORT).show();
            
            return true;
        case R.id.action_person:
            
        	if(mSlidingMenu.isSecondaryMenuShowing()){
        		mSlidingMenu.showContent();
        	}else{
        		mSlidingMenu.showSecondaryMenu();
        	}
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
        
    }

	@SuppressLint("NewApi")
	@Override
	public void selectItem(int position, String title) {
		// update the main content by replacing fragments  
	    Fragment fragment = null;  
	    switch (position) {  
	    case 0:  
	        fragment = new HomeFragment();  
	        break;  
	    case 1:  
	        fragment = new FindPeopleFragment();  
	        break;  
	    case 2:  
	        fragment = new PhotosFragment();  
	        break;  
	    case 3:  
	        fragment = new CommunityFragment();  
	        break;  
	    case 4:  
	        fragment = new PagesFragment();  
	        break;  
	    case 5:  
	        fragment = new WhatsHotFragment();  
	        break;  
	    default:  
	        break;  
	    }  
	  
	    if (fragment != null) {  
	        FragmentManager fragmentManager = getSupportFragmentManager();
	        fragmentManager.beginTransaction()  
	                .replace(R.id.content, fragment).commit();  
	        // update selected item and title, then close the drawer  
	        setTitle(title);
	        mSlidingMenu.showContent();
	    } else {  
	        // error in creating fragment  
	        Log.e("MainActivity", "Error in creating fragment");  
	    }  
	}
	
//	private TabHost tabHost;
//	private RadioGroup radioGroup;
//	private RadioButton radio_home;
//	private RadioButton radio_chat;
//	private RadioButton radio_shenpi;
//	private RadioButton radio_xiapai;
//	private int checked_id;
//	private int clicked_id;
//	private MyApplication application;
//	private String loginKey;
//	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
//	Context context = null;
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_main);
//		
//        // configure the SlidingMenu  
//        SlidingMenu menu = new SlidingMenu(this);  
//        menu.setMode(SlidingMenu.LEFT);  
//        // 设置触摸屏幕的模式  
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
//        menu.setShadowWidthRes(R.dimen.shadow_width);  
//        menu.setShadowDrawable(R.drawable.shadow);  
//  
//        // 设置滑动菜单视图的宽度  
//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
//        // 设置渐入渐出效果的值  
//        menu.setFadeDegree(0.35f);  
//        /** 
//         * SLIDING_WINDOW will include the Title/ActionBar in the content 
//         * section of the SlidingMenu, while SLIDING_CONTENT does not. 
//         */  
//        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);  
//        //为侧滑菜单设置布局  
//        menu.setMenu(R.layout.leftmenu);  
//		
//		application = (MyApplication) this.getApplication();
//		context=MainActivity.this;
//		mAbHttpUtil.setDebug(true);
//		//TODO 以后可以改成该手机电话号 或者 用户ID 等唯一标识
//		
//		application.setProperty("loginKey", "1");
//		
//		radio_home = (RadioButton) findViewById(R.id.radio_home);
//		radio_chat = (RadioButton) findViewById(R.id.radio_chat);
//		radio_shenpi = (RadioButton) findViewById(R.id.radio_shenpi);
//		radio_xiapai = (RadioButton) findViewById(R.id.radio_xiapai);
//
////		tabHost = getTabHost();
////		tabHost.addTab(tabHost.newTabSpec("Home").setIndicator("Home")
////				.setContent(new Intent(this, HomeActivity.class)));
////		tabHost.addTab(tabHost.newTabSpec("CollectingSource")
////				.setIndicator("CollectingSource")
////				.setContent(new Intent(this, QunfaActivity.class)));
////		tabHost.addTab(tabHost.newTabSpec("shenpi").setIndicator("Shenpi")
////				.setContent(new Intent(this, ShenpiActivity.class)));
////		tabHost.addTab(tabHost.newTabSpec("xiapai").setIndicator("Xiapai")
////				.setContent(new Intent(this, XiapaiActivity.class)));
////
////		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
////		radioGroup.setOnCheckedChangeListener(checkedChangeListener);
////
////		radio_home.setOnClickListener(clickListener);
////		radio_chat.setOnClickListener(clickListener);
////		radio_shenpi.setOnClickListener(clickListener);
////		radio_xiapai.setOnClickListener(clickListener);
////		
////		checked_id = R.id.radio_home;
////		clicked_id = R.id.radio_home;
////		
////		init();
//	}
//	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
//	private void init(){
//		 JPushInterface.init(getApplicationContext());
//		 String registrationid=JPushInterface.getRegistrationID(context);
//		 
//		 //给用户添加jpush标记
//		 System.out.println("******************"+registrationid);
//		 if (registrationid!=null) {
//			 // 获取Http工具类
//			 final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
//			 mAbHttpUtil.setDebug(true);
//			 if (!application.isNetworkConnected()) {
//				 UIHelper.ToastMessage(context, "请检查网络连接");
//				 return;
//			 }
//			 loginKey = application.getProperty("loginKey");
//			 AbRequestParams params = new AbRequestParams();
//			 params.put("user_id", loginKey);
//			 params.put("jpush_registration_id", registrationid);
//			 mAbHttpUtil.post(URLs.ADDREGIS ,params,
//					 new AbStringHttpResponseListener() {
//				 @Override
//				 public void onSuccess(int statusCode, String content) {
//					 System.out.println(content);
//					 try {
//						 PeopleInfo cList = PeopleInfo.parseJson(content);
//						 if (cList.code == 200) {
//							 People peoples = cList.getInfo();
//							 application.setProperty("status", peoples.status+"");
//						 } else {
//							 UIHelper.ToastMessage(context, cList.msg);
//						 }
//					 } catch (Exception e) {
//						 e.printStackTrace();
//						 UIHelper.ToastMessage(context, "数据解析失败");
//					 }
//				 }
//				 
//				 @Override
//				 public void onFailure(int statusCode, String content,
//						 Throwable error) {
//					 UIHelper.ToastMessage(context, "网络连接失败！");
//				 }
//				 
//				 @Override
//				 public void onStart() {
//				 }
//				 
//				 // 完成后调用
//				 @Override
//				 public void onFinish() {
//				 };
//			 });
//			
//		}
//	}
//	
//	
//	private OnClickListener clickListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//				case R.id.radio_home:
//					if(checked_id == v.getId()){
//						if(clicked_id!=checked_id){
//							clicked_id = checked_id;
//						}else{
//							HomeActivity a=((HomeActivity)(getLocalActivityManager().getCurrentActivity()));
//							a.refresh_show_page();
//						}
//					}
//					break;
//				case R.id.radio_chat:
//					if(checked_id == v.getId()){
//						if(clicked_id!=checked_id){
//							clicked_id = checked_id;
//						}else{
//							@SuppressWarnings("deprecation")
//							QunfaActivity a = ((QunfaActivity)(getLocalActivityManager().getCurrentActivity()));
//							a.refresh_show_page();
//						}
//					}
//					break;
//				case R.id.radio_shenpi:
//					if(checked_id == v.getId()){
//						if(clicked_id!=checked_id){
//							clicked_id = checked_id;
//						}else{
//							@SuppressWarnings("deprecation")
//							ShenpiActivity a = ((ShenpiActivity)(getLocalActivityManager().getCurrentActivity()));
//							a.refresh_show_page();
//						}
//					}
//					break;
//				case R.id.radio_xiapai:
//					if(checked_id == v.getId()){
//						if(clicked_id!=checked_id){
//							clicked_id = checked_id;
//						}else{
//							@SuppressWarnings("deprecation")
//							XiapaiActivity a = ((XiapaiActivity)(getLocalActivityManager().getCurrentActivity()));
//							a.refresh_show_page();
//						}
//					}
//					break;
//				default:
//					break;
//			}
//		}
//	};
//
//
//	@Override
//    protected void onResume() {
//        super.onResume();
//        JPushInterface.onResume(this);
//    }
//    @Override
//    protected void onPause() {
//        super.onPause();
//        JPushInterface.onPause(this);
//    }
//
//	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {
//
//		@Override
//		public void onCheckedChanged(RadioGroup group, int checkedId) {
//			checked_id = checkedId;
//			switch (checkedId) {
//			case R.id.radio_home:
//				tabHost.setCurrentTab(0);
//				break;
//			case R.id.radio_chat:
//				tabHost.setCurrentTab(1);
//				break;
//			case R.id.radio_shenpi:
//				tabHost.setCurrentTab(2);
//				break;
//			case R.id.radio_xiapai:
//				tabHost.setCurrentTab(3);
//				break;
////			case R.id.radio_my:
////				tabHost.setCurrentTab(4);
////				break;
//			default:
//				break;
//			}
//		}
//	};

}