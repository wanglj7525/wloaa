package com.ytint.wloaa.activity;

import android.content.DialogInterface;
import android.view.KeyEvent;

import com.ab.activity.AbActivity;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.CustomDialog;

public class BaseActivity extends AbActivity {
    public boolean onKeyDown(int keyCode, KeyEvent event) {//捕捉返回键
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {   
        	CustomDialog.Builder builder = new CustomDialog.Builder(
					this);
			builder.setTitle("提示")
					.setMessage("您确定要退出系统吗")
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							});
			builder.setPositiveButton("确定", getResources()
					.getColor(R.color.global_blue),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							System.exit(0);

						}
					});
			builder.create().show(); 
			return true;
        }
        else
        {
        	return super.onKeyDown(keyCode, event);   
        }
        
    }

}
