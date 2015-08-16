package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ab.http.AbHttpUtil;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.fragment.AnquanFragment;
import com.ytint.wloaa.fragment.HomeFragment;
import com.ytint.wloaa.fragment.MenuFragment;
import com.ytint.wloaa.fragment.MenuFragment.SLMenuListOnItemClickListener;
import com.ytint.wloaa.fragment.PagesFragment;
import com.ytint.wloaa.fragment.QiyeFragment;
import com.ytint.wloaa.fragment.ZhifaFragment;

public class MainActivity extends SlidingFragmentActivity implements SLMenuListOnItemClickListener{

private SlidingMenu mSlidingMenu;
private MyApplication application;
private String loginKey;
final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
Context context = null;
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		application = (MyApplication) this.getApplication();
		context=MainActivity.this;
		mAbHttpUtil.setDebug(true);
		//TODO 以后可以改成该手机电话号 或者 用户ID 等唯一标识
		application.setProperty("loginKey", "1");
		
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
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
//        case R.id.action_refresh:
//            
//        	Toast.makeText(getApplicationContext(), R.string.refresh, Toast.LENGTH_SHORT).show();
//            
//            return true;
//        case R.id.action_person:
//            
//        	if(mSlidingMenu.isSecondaryMenuShowing()){
//        		mSlidingMenu.showContent();
//        	}else{
//        		mSlidingMenu.showSecondaryMenu();
//        	}
//            return true;
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
	        fragment = new AnquanFragment();  
	        break;  
	    case 2:  
	        fragment = new ZhifaFragment();  
	        break;  
	    case 3:  
	        fragment = new QiyeFragment();  
	        break;  
	    case 4:  
	        fragment = new PagesFragment();  
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
}