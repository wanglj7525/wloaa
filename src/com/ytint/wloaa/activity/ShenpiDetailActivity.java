package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Shenpi;
import com.ytint.wloaa.bean.ShenpiInfo;
import com.ytint.wloaa.bean.URLs;

public class ShenpiDetailActivity extends AbActivity {
	String TAG = "ShenpiDetailActivity";
	private MyApplication application;
	private AbImageDownloader mAbImageDownloader = null;
	Context context = null;
	
	
	@AbIocView(id = R.id.shenpi_detail)
	TextView shenpi_detail;
	@AbIocView(id = R.id.shenpi_title)
	TextView shenpi_title;
	@AbIocView(id = R.id.from_shenpi)
	TextView from_shenpi;
	
	@AbIocView(id = R.id.shenpi_detail_full)
	LinearLayout shenpi_detail_full;
	
	
	@AbIocView(id = R.id.first_verify_user_name)
	TextView first_verify_user_name;
	@AbIocView(id = R.id.first_verify_comment)
	TextView first_verify_comment;
	
	@AbIocView(id = R.id.gridView_image)
	GridView gridView_image;
	@AbIocView(id = R.id.gridView_voice)
	GridView gridView_voice;
	
	private int from;
	Integer shenpi_id;
	private Shenpi shenpi = new Shenpi();
	
	
	 private List<Map<String, Object>> data_list;
	    private SimpleAdapter sim_adapter;
	    // 图片封装为一个数组
	    private int[] icon = { R.drawable.contact_0, R.drawable.contact_1,
	            R.drawable.contact_3, R.drawable.azhifa, R.drawable.aanquan,
	            R.drawable.azhiliang};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		if (from==1) {
			mAbTitleBar.setTitleText("质量检查-任务详情");
		}else if(from==2){
			mAbTitleBar.setTitleText("安全检查-任务详情");
		}else{
			mAbTitleBar.setTitleText("执法管理-任务详情");
		}
		mAbTitleBar.setLogo(R.drawable.button_selector_back); 
//		 设置文字边距，常用来控制高度：
		 mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
//		 设置标题栏背景：
		 mAbTitleBar.setTitleBarBackground(R.drawable.abg_top); 
//		 左边图片右边的线：
		 mAbTitleBar.setLogoLine(R.drawable.aline);
//		  左边图片的点击事件：
		 mAbTitleBar.getLogoView().setOnClickListener(new View.OnClickListener() {
		     @Override
		     public void onClick(View v) {
		        finish();
		     }

		 }); 
		 
		setAbContentView(R.layout.layout_shenpidetail);
		application = (MyApplication) abApplication;
		context=ShenpiDetailActivity.this;
		
		shenpi_id=intent.getIntExtra("shenpi_id",0);
		
		loadDatas();
	}
	
	@SuppressLint("NewApi")
	private void loadDatas() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(URLs.SHENPIDETAIL + "?id=" + shenpi_id,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							
							ShenpiInfo gList = ShenpiInfo
									.parseJson(content);
							if (gList.code == 200) {
								shenpi = gList.getInfo();
								from_shenpi.setText(shenpi.apply_user_name);
								shenpi_title.setText(shenpi.title);
								shenpi_detail.setText(shenpi.content);
								
								int status = Integer.parseInt(application.getProperty("status").toString());
								
								if (shenpi.first_verify_status==0) {
									first_verify_user_name.setText("未审批");
								}else{
									first_verify_user_name.setText(shenpi.first_verify_user_name);
									first_verify_comment.setText(shenpi.first_verify_comment);
								}
								
								
								//TODO
//								//新建List
//						        data_list = new ArrayList<Map<String, Object>>();
//						        //获取数据
//						        getData();
//						        //新建适配器
//						        String [] from ={"image"};
//						        int [] to = {R.id.image};
//						        sim_adapter = new SimpleAdapter(this, data_list, R.layout.image_item, from, to);
//						        //配置适配器
//						        gridView_image.setAdapter(sim_adapter);
								
							} else {
								UIHelper.ToastMessage(context, gList.msg);
							}
						} catch (Exception e) {
							e.printStackTrace();
							showToast("数据解析失败");
						}
					};

					// 开始执行前
					@Override
					public void onStart() {
						// 显示进度框
						showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast("网络连接失败！");
					}

					// 完成后调用，失败，成功
					@Override
					public void onFinish() {
						// 移除进度框
						removeProgressDialog();
					};

				});
	}
	 public List<Map<String, Object>> getData(){        
	        //cion和iconName的长度是相同的，这里任选其一都可以
	        for(int i=0;i<icon.length;i++){
	            Map<String,Object> map = new HashMap<String, Object>();
	            map.put("image", icon[i]);
	            data_list.add(map);
	        }
	            
	        return data_list;
	    }

}
