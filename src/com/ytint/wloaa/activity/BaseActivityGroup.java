package com.ytint.wloaa.activity;

import android.app.ActivityGroup;
import android.content.DialogInterface;
import android.view.KeyEvent;

import com.ytint.wloaa.R;
import com.ytint.wloaa.app.CustomDialog;


public class BaseActivityGroup extends ActivityGroup {
	 @Override
	 public boolean onKeyDown(int keyCode, KeyEvent event) {
	  if (keyCode == KeyEvent.KEYCODE_BACK) {
     	CustomDialog.Builder builder = new CustomDialog.Builder(
					this);
			builder.setTitle("��ʾ")
					.setMessage("��ȷ��Ҫ�˳���������ϵͳ��")
					.setNegativeButton("ȡ��",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								})
					.setPositiveButton("ȷ��", getResources()
					.getColor(R.color.global_blue),
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							System.exit(0);

						}
					});
			builder.create().show(); 
			return true;
	  } else {
	   return super.onKeyDown(keyCode, event);
	  }
	 }
}
