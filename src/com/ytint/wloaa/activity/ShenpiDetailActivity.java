package com.ytint.wloaa.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.util.CacheManager;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.Constants;
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
	 private List<String> imageList = new ArrayList<String>();
	 String host;
	 static class ViewHolder {
	        ImageView mImg;
	    }

	 	@AbIocView(id = R.id.gridView_voice)
		GridView gridView_voice;
		@AbIocView(id=R.id.horizontalScrollView_voicelist_detail)
		HorizontalScrollView horizontalScrollView_voicelist_detail;
		/** 语音列表 */
		private List<String> mVoicesList=new ArrayList<String>();
		/** 用于语音播放 */
		private MediaPlayer mPlayer = null;
		/**
		 * 列宽
		 */
		private int cWidth = 500;
		/**
		 * 水平间距
		 */
		private int hSpacing = 10;
	@AbIocView(id = R.id.task_tell_detail)
	TextView task_tell_detail;
	@AbIocView(id = R.id.task_create)
	TextView task_create;
	@AbIocView(id = R.id.task_project)
	TextView task_project;
	@AbIocView(id = R.id.task_name_detail)
	TextView task_name_detail;
	@AbIocView(id = R.id.detail_handle_mode)
	TextView detail_handle_mode;
	
	@AbIocView(id = R.id.taskForwardInfo)
	TextView taskForwardInfo;
	@AbIocView(id = R.id.taskRemarkInfo)
	TextView taskRemarkInfo;
	@AbIocView(id = R.id.showImageText)
	TextView showImageText;
	
	@AbIocView(id = R.id.showmodeLiner)
	LinearLayout showmodeLiner;
	@AbIocView(id = R.id.shenpi_detail_full)
	LinearLayout shenpi_detail_full;
	
	
	@AbIocView(id = R.id.first_verify_user_name)
	TextView first_verify_user_name;
	@AbIocView(id = R.id.scrollView_image)
	HorizontalScrollView scrollView_image;
	@AbIocView(id = R.id.gridView_image)
	GridView gridView_image;
	
	@AbIocView(id=R.id.task_finish)
	Button task_finish;
	private String userType;
	private int from;
	Integer shenpi_id;
	private Shenpi shenpi = new Shenpi();
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application= (MyApplication) this.getApplication();
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		AbTitleBar mAbTitleBar = this.getTitleBar();
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		mAbTitleBar.setTitleText("任务详情");
//		if (from==1) {
//			mAbTitleBar.setTitleText("质量检查-任务详情");
//		}else if(from==2){
//			mAbTitleBar.setTitleText("安全检查-任务详情");
//		}else{
//			mAbTitleBar.setTitleText("执法管理-任务详情");
//		}
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
		context=ShenpiDetailActivity.this;
		userType = application.getProperty("userType");
		shenpi_id=intent.getIntExtra("shenpi_id",0);
		scrollView_image.setHorizontalScrollBarEnabled(true);
		loadDatas();
		task_finish.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// 科长点击 上报任务 完成
				finishTask();
			}
		});
	}
	
	@SuppressLint("NewApi")
	private void finishTask() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		// 绑定参数
		AbRequestParams params = new AbRequestParams();
		params.put("task_id", shenpi_id.toString());
		params.put("verify_status", "1");
		params.put("verify_user_id", loginKey);
		params.put("verify_commit", "");
		params.put("verify_type", "1");
		params.put("receive_user_id",loginKey);
		mAbHttpUtil.post(host+URLs.SHENPI ,params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							ShenpiInfo gList = ShenpiInfo
									.parseJson(content);
							if (gList.code == 200) {
								UIHelper.ToastMessage(context, "任务已完成");
								task_finish.setVisibility(View.GONE);
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
	@SuppressLint("NewApi")
	private void loadDatas() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		final String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(host+URLs.SHENPIDETAIL + "?id=" + shenpi_id,
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
						task_name_detail.setText(shenpi.name);
						task_create.setText(shenpi.create_user_name);
						task_project.setText(shenpi.project_name);
						task_tell_detail.setText(shenpi.contact);
						detail_handle_mode.setText(shenpi.handle_mode);
						taskForwardInfo.setText(shenpi.taskForwardInfo);
						taskRemarkInfo.setText(shenpi.remark);
						if (shenpi.attachment!="") {
							for (int i = 0; i < shenpi.attachment.split(",").length; i++) {
								imageList.add(host+URLs.URL_API_HOST+shenpi.attachment.split(",")[i]);
							}
						}
						setValue();
						setListener();
						
						if (shenpi.task_type==2) {
//							taskRemarkInfo.setVisibility(View.GONE);
							detail_handle_mode.setVisibility(View.GONE);
							showImageText.setVisibility(View.GONE);
							showmodeLiner.setVisibility(View.GONE);
							task_finish.setVisibility(View.GONE);
						}else{
							if (userType.equals("3")) {
								task_finish.setVisibility(View.VISIBLE);
							}
						}
						mPlayer = new MediaPlayer();
						if (shenpi.media!="") {
							for (int i = 0; i < shenpi.media.split(",").length; i++) {
								String voice=shenpi.media.split(",")[i];
								mVoicesList.add(host+URLs.URL_API_HOST+voice);
							}
						}
						horizontalScrollView_voicelist_detail.setHorizontalScrollBarEnabled(true);
						initGridVoiceView();
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
	private void initGridVoiceView(){
		MyGridAdapter mAdapter = new MyGridAdapter(context);
		gridView_voice.setAdapter(mAdapter);
		LayoutParams params = new LayoutParams(mAdapter.getCount()
				* (cWidth + hSpacing), LayoutParams.WRAP_CONTENT);
		gridView_voice.setLayoutParams(params);
		gridView_voice.setColumnWidth(cWidth);
		gridView_voice.setHorizontalSpacing(hSpacing);
		gridView_voice.setStretchMode(GridView.NO_STRETCH);
		gridView_voice.setNumColumns(mAdapter.getCount());

		gridView_voice.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					mPlayer.reset();
					mPlayer.setDataSource(mVoicesList.get(position));
					mPlayer.prepare();
					mPlayer.start();
				} catch (IOException e) {
					Log.e(TAG, "播放失败");
				}
			}
		});
	}
	    private void setValue() {
	        MAdapter mAdapter = new MAdapter(context);
	        gridView_image.setAdapter(mAdapter);
	        LayoutParams params = new LayoutParams(mAdapter.getCount() * (200 + 10),
	                LayoutParams.WRAP_CONTENT);
	        gridView_image.setLayoutParams(params);
	        gridView_image.setColumnWidth(200);
	        gridView_image.setHorizontalSpacing(hSpacing);
	        gridView_image.setStretchMode(GridView.NO_STRETCH);
	        gridView_image.setNumColumns(mAdapter.getCount());
	    }

	    private void setListener() {
	    	gridView_image.setOnItemClickListener(new OnItemClickListener() {

	            @Override
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                Log.e(TAG, "position = " + position);
	                String url=imageList.get(position);
	                if (url.contains("3gp")||url.contains("mp4")) {
	                	Intent intent = new Intent(ShenpiDetailActivity.this, MediaPlayerDemo_Video.class);  
	    				intent.putExtra("url", url);
	    				startActivity(intent);
	    			}else{
	    				Intent intent = new Intent(ShenpiDetailActivity.this, PicturePreviewActivity.class);  
	    				intent.putExtra("url", url);
	    				startActivity(intent);
	    			}
	            }
	        });
	    }

	    class MAdapter extends BaseAdapter {
	    	 Context mContext;
	         LayoutInflater mInflater;

	         public MAdapter(Context c) {
	             mContext = c;
	             mInflater = LayoutInflater.from(mContext);
	    		// 图片下载器
//	    		mAbImageDownloader = new AbImageDownloader(mContext);
//	    		mAbImageDownloader.setWidth(200);
//	    		mAbImageDownloader.setHeight(100);
//	    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
//	    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
//	    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
//	    		mAbImageDownloader.setNoImage(R.drawable.image_no);
	        }


			@Override
	        public int getCount() {
	            // TODO Auto-generated method stub
	            return imageList.size();
	        }

	        @Override
	        public Object getItem(int arg0) {
	            // TODO Auto-generated method stub
	            return arg0;
	        }

	        @Override
	        public long getItemId(int arg0) {
	            // TODO Auto-generated method stub
	            return arg0;
	        }

	        @Override
	        public View getView(int position, View contentView, ViewGroup arg2) {
	        	String image=imageList.get(position);
	        	if (image.contains("3gp")) {
					image=host+URLs.URL_API_HOST+"public/images/video_play_btn.png";
				}
	            ViewHolder holder;
	            if (contentView == null) {
	                holder = new ViewHolder();
	                contentView = mInflater.inflate(R.layout.gridview_item, null);
	                holder.mImg = (ImageView) contentView.findViewById(R.id.mImage);
	                application.IMAGE_CACHE.get(image, holder.mImg);
//	                mAbImageDownloader.display(holder.mImg,image);
	            } else {
	                holder = (ViewHolder) contentView.getTag();
	            }
	            contentView.setTag(holder);
	            return contentView;
	        }

	    }
		class MyGridAdapter extends BaseAdapter {
			Context mContext;
			LayoutInflater mInflater;

			public MyGridAdapter(Context c) {
				mContext = c;
				mInflater = LayoutInflater.from(mContext);
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mVoicesList.size();
			}

			@Override
			public Object getItem(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public long getItemId(int arg0) {
				// TODO Auto-generated method stub
				return arg0;
			}

			@Override
			public View getView(int position, View contentView, ViewGroup arg2) {
				contentView = mInflater.inflate(R.layout.item_voicelist, null);
				TextView tv = (TextView) contentView.findViewById(R.id.tv_armName);
				tv.setText(mVoicesList.get(position));
				return contentView;
			}

		}
}
