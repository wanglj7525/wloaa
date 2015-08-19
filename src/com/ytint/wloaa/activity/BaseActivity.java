package com.ytint.wloaa.activity;

import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.ytint.wloaa.widget.ExitActivityManger;

public class BaseActivity extends FragmentActivity {
	long firstTime=0L;
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   
        	 long secondTime = System.currentTimeMillis(); 
	            if (secondTime - firstTime > 2000) {//如果两次按键时间间隔大于800毫秒，则不退出 
	                Toast.makeText(BaseActivity.this, "再按一次退出程序", 
	                        Toast.LENGTH_SHORT).show();
	                firstTime = secondTime;//更新firstTime 
	                return true; 
	            } else { 
	            	ExitActivityManger.getInstance().finish();
	                System.exit(0);//否则退出程序 
	            } 
	        } 
	        return super.onKeyUp(keyCode, event); 
        }
}
