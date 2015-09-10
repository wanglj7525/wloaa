package com.ytint.wloaa.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.URLs;

/**
 * @author wlj
 * @date 2014-4-17上午10:40:17
 */
public class SetNetworksActivity extends AbActivity {
	private static final String TAG = "SetNetworksActivity";
	private LinearLayout full_screen_layout;
	private MyApplication application;
	Context context = null;

	@AbIocView(id = R.id.networks_ip)
	private EditText networks_ip; 
	@AbIocView(id = R.id.networks_closePass)
	private RelativeLayout networks_closePass; 
	@AbIocView(id = R.id.networks_port)
	private EditText networks_port; 
	@AbIocView(id = R.id.networks_name)
	private EditText networks_name; 
	@AbIocView(id = R.id.networks_port_button)
	private Button networks_port_button; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application= (MyApplication) this.getApplication();
		context=SetNetworksActivity.this;
		setContentView(R.layout.layout_networks);
		networks_name.setText(null==application.getProperty("IPNAME")?"默认":application.getProperty("IPNAME"));
		networks_ip.setText(application.getProperty("HOST"));
		networks_port.setText(application.getProperty("PORT"));
		networks_closePass.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		networks_port_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String host=networks_ip.getText().toString().trim();
				String port=networks_port.getText().toString().trim();
				String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
				String regexyu="^(?=^.{3,255}$)[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+$";
				if (!Pattern.matches(regex, host)) {
					if (!Pattern.matches(regexyu, host)) {
						showToast("地址不合法！");
						return;
					}
				}
				if (port.length()==0) {
					showToast("端口不能为空！");
					return;
				}
				application.setProperty("IPNAME",networks_name.getText().toString().trim());
				application.setProperty("HOST",host);
				application.setProperty("PORT",port);
				showToast("设置成功！");
			}
		});
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		full_screen_layout.setClickable(true);
		full_screen_layout.setOnClickListener(keyboard_hide);

	}

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

}