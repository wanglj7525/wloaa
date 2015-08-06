package com.ytint.wloaa.activity;

import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;

public class PushResultActivity extends AbActivity {
	private MyApplication application;
	private TextView detail_title;
	private TextView detail_time;
	private TextView detail_content;
	private Button push_detail_title;
	private ImageView push_detail_back;
	private String doc_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_push_doc_detail);
		detail_title = (TextView) findViewById(R.id.doc_detail_title);
		detail_time = (TextView) findViewById(R.id.doc_detail_time);
		detail_content = (TextView) findViewById(R.id.doc_detail_content);
		push_detail_title = (Button) findViewById(R.id.push_detail_title);
		push_detail_back = (ImageView) findViewById(R.id.push_detail_back);
		push_detail_title.setText("新闻");
		
		push_detail_back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PushResultActivity.this.finish();
//				// 跳转到首页
//				Intent intent = new Intent(PushResultActivity.this,
//						WelcomeActivity.class);
//				PushResultActivity.this.startActivity(intent);
//				PushResultActivity.this.finish();
			}
		});

		application = (MyApplication) this.getApplication();
		Intent intent = getIntent();
		if (null != intent) {
	        Bundle bundle = getIntent().getExtras();
	        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
	        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
	        String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
	        JSONObject obj;
			try {
				obj = new JSONObject(extra);
				doc_id = obj.getString("doc_id");
			} catch (JSONException e) {
				e.printStackTrace();
			}
        }
//		// 获取Http工具类
//		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
//		mAbHttpUtil.get(Constants.PUSHDOCDETAIL +"?doc_id=" + doc_id, new AbStringHttpResponseListener() {
//			// 获取数据成功会调用这里
//			@Override
//			public void onSuccess(int statusCode, String content) {
//				try {
//					DocDetail dd = DocDetail.parseJson(content);
//					int code = dd.code;
//					if (code == Constants.SUCCESS) {
//						Doc doc = dd.getInfo();
//						detail_title.setText(doc.title);
//						detail_time.setText(doc.create_time);
//						ImageGetter imgGetter = new ImageGetter() {
//							@Override
//							public Drawable getDrawable(String source) {
//								Drawable drawable = null;
//								URL url;
//								try {
//									url = new URL(source);
//									drawable = Drawable.createFromStream(
//											url.openStream(), ""); // 获取网路图片
//								} catch (Exception e) {
//									return null;
//								}
//								if(drawable!=null){
//									int x = drawable.getIntrinsicWidth();
//									int y = drawable.getIntrinsicHeight();
//									drawable.setBounds(0, 0,
//											x*2,
//											y*2);
//								}
//								return drawable;
//							}
//						};
//						detail_content.setText(Html.fromHtml(doc.content,
//								imgGetter, null));
//					} else {
////						showToast(dd.msg);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//
//			}
//
//			// 开始执行前
//			@Override
//			public void onStart() {
//				// 显示进度框
////				showProgressDialog();
//			}
//
//			// 失败，调用
//			@Override
//			public void onFailure(int statusCode, String content,
//					Throwable error) {
////				showToast("网络连接失败！");
//			}
//
//			// 完成后调用，失败，成功
//			@Override
//			public void onFinish() {
//				// 移除进度框
////				removeProgressDialog();
//			};
//
//		});
//		mAbTitleBar.getLogoView().setOnClickListener(
//				new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						finish();
//					}
//				});
    }

}
