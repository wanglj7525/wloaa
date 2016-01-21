package com.ytint.wloaa.activity;

import android.app.Application;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.widget.ExitActivityManger;

public class BaseActivity extends FragmentActivity {
	long firstTime=0L;
	private MyApplication application = (MyApplication) this.getApplication();
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   
        	 long secondTime = System.currentTimeMillis(); 
	            if (secondTime - firstTime > 2000) {//如果两次按键时间间隔大于800毫秒，则不退出 
	                Toast.makeText(BaseActivity.this, "再按一次退出程序", 
	                        Toast.LENGTH_SHORT).show();
	                firstTime = secondTime;//更新firstTime 
	                return true; 
	            } else { 
	            	application.IMAGE_CACHE.saveDataToDb(this, "wloaa");
	            	ExitActivityManger.getInstance().finish();
//	                System.exit(0);//否则退出程序 
	            	this.finish();
	            } 
	        } 
	        return super.onKeyUp(keyCode, event); 
        }
}
