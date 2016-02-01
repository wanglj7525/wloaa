package com.ytint.wloaa.activity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Task;
import com.ytint.wloaa.bean.TaskInfo;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.fragment.TaskFragment;
import com.ytint.wloaa.widget.TitleBar;

public class TaskDetailActivity extends AbActivity {
	String TAG = "ShenpiDetailActivity";
	private MyApplication application;
	private AbImageDownloader mAbImageDownloader = null;
	Context context = null;
	private List<String> imageList = new ArrayList<String>();
	private List<String> imageBigList = new ArrayList<String>();
	String host;

	static class ViewHolder {
		ImageView mImg;
	}

	@AbIocView(id = R.id.gridView_voice)
	GridView gridView_voice;
	@AbIocView(id = R.id.horizontalScrollView_voicelist_detail)
	HorizontalScrollView horizontalScrollView_voicelist_detail;
	@AbIocView(id=R.id.task_open)
	Button task_open;
	@AbIocView(id=R.id.task_reply)
	Button task_reply;
	/** 语音列表 */
	private List<String> mVoicesList = new ArrayList<String>();
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

	@AbIocView(id = R.id.taskForwardInfo)
	TextView taskForwardInfo;
	@AbIocView(id = R.id.taskRemarkInfo)
	TextView taskRemarkInfo;
	@AbIocView(id = R.id.showImageText)
	TextView showImageText;

	@AbIocView(id = R.id.shenpi_detail_full)
	LinearLayout shenpi_detail_full;

	@AbIocView(id = R.id.first_verify_user_name)
	TextView first_verify_user_name;
	@AbIocView(id = R.id.scrollView_image)
	HorizontalScrollView scrollView_image;
	@AbIocView(id = R.id.gridView_image)
	GridView gridView_image;

	// @AbIocView(id=R.id.task_finish)
	// Button task_finish;
	private String userType;
	private int from;
	private int reply_from=1;
	Integer shenpi_id;
	String shenpi_name;
	String shenpi_topeople;
	private Task shenpi = new Task();

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
		application = (MyApplication) this.getApplication();
		host = URLs.HTTP + application.getProperty("HOST") + ":"
				+ application.getProperty("PORT");
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());

		setAbContentView(R.layout.layout_shenpidetail);
		context = TaskDetailActivity.this;
		userType = application.getProperty("userType");
		shenpi_id = intent.getIntExtra("shenpi_id", 0);
		scrollView_image.setHorizontalScrollBarEnabled(true);

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		final TitleBar titleBar = (TitleBar) findViewById(R.id.title_bardetail);
		titleBar.setLeftImageResource(R.drawable.back_green);
		titleBar.setLeftText("返回");
		titleBar.setLeftTextColor(Color.WHITE);
		titleBar.setLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		titleBar.setTitle("任务详情");
		titleBar.setTitleColor(Color.WHITE);
		titleBar.setDividerColor(Color.GRAY);
		loadDatas();
		
		task_open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				openTask();
			}
		});
		
		task_reply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TaskDetailActivity.this,
						SendTaskActivity.class);
				intent.putExtra("from", reply_from+2);
				intent.putExtra("reply_task_id", shenpi_id.toString());
				intent.putExtra("reply_task_name", shenpi_name);
				intent.putExtra("reply_task_topeople", shenpi_topeople);
				startActivity(intent);
			}
		});
	}

	@SuppressLint("NewApi")
	private void openTask() {
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		// 绑定参数
		AbRequestParams params = new AbRequestParams();
		params.put("task_id", shenpi_id.toString());
		params.put("user_id", loginKey);
		mAbHttpUtil.post(host + URLs.OPEN, params,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							TaskInfo gList = TaskInfo.parseJson(content);
							if (gList.code == 200) {
								UIHelper.ToastMessage(context, "任务已公开");
								// task_finish.setVisibility(View.GONE);
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
		String url=host + URLs.SHENPIDETAIL + "?id=" + shenpi_id+"&user_id="+loginKey;
		Log.i(TAG, url);
		mAbHttpUtil.get(url,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.i(TAG, content);
						try {

							TaskInfo gList = TaskInfo.parseJson(content);
							if (gList.code == 200) {
								shenpi = gList.getInfo();
								task_name_detail.setText(shenpi.name);
								shenpi_name=shenpi.name;
								shenpi_topeople=shenpi.create_user_id;
								task_create.setText(shenpi.create_user_name);
								task_project.setText(shenpi.project_name);
								if (shenpi.task_type==2) {
									reply_from=shenpi.task_type;
									task_project.setText("自定义任务");
								}
								task_tell_detail.setText(shenpi.contact);
								taskForwardInfo.setText(shenpi.taskForwardInfo);
								taskRemarkInfo.setText(shenpi.remark);
								
									//发给自己的任务 区分 是局长或者副局长 显示公开按钮  是科员或者科长显示回复按钮
								if (userType.equals("0")||userType.equals("1")||userType.equals("2")) {
									//、管理员，局长、副局长
								}else{
									if (shenpi.if_receive_user==1) {
										task_reply.setVisibility(View.VISIBLE);
									}
									if (shenpi.if_open_power==1) {
										task_open.setVisibility(View.VISIBLE);
									}
								}
								
								if (shenpi.attachment_simp != "") {
									for (int i = 0; i < shenpi.attachment_simp
											.split(",").length; i++) {
										imageList.add(host
												+ URLs.URL_API_HOST
												+ shenpi.attachment_simp.split(",")[i]);
										imageBigList.add(host
												+ URLs.URL_API_HOST
												+ shenpi.attachment.split(",")[i]);
									}
								}
								setValue();
								setListener();

								
//								if (shenpi.task_type == 2) {
//									// taskRemarkInfo.setVisibility(View.GONE);
//									showImageText.setVisibility(View.GONE);
//									// task_finish.setVisibility(View.GONE);
//								} else {
//									if (userType.equals("3")) {
//										// task_finish.setVisibility(View.VISIBLE);
//									}
//								}
								mPlayer = new MediaPlayer();
								if (shenpi.media != "") {
									for (int i = 0; i < shenpi.media.split(",").length; i++) {
										String voice = shenpi.media.split(",")[i];
										mVoicesList.add(host
												+ URLs.URL_API_HOST + voice);
									}
								}
								horizontalScrollView_voicelist_detail
										.setHorizontalScrollBarEnabled(true);
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

	private void initGridVoiceView() {
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
		LayoutParams params = new LayoutParams(
				mAdapter.getCount() * (200 + 10), LayoutParams.WRAP_CONTENT);
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
				String url = imageBigList.get(position);
				if (url.contains("3gp") || url.contains("mp4")) {
					Intent intent = new Intent(TaskDetailActivity.this,
							MediaPlayerDemo_Video.class);
					intent.putExtra("url", url);
					startActivity(intent);
				} else {
					Intent intent = new Intent(TaskDetailActivity.this,
							PicturePreviewActivity.class);
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
			// mAbImageDownloader = new AbImageDownloader(mContext);
			// mAbImageDownloader.setWidth(200);
			// mAbImageDownloader.setHeight(100);
			// mAbImageDownloader.setType(AbConstant.SCALEIMG);
			// mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
			// mAbImageDownloader.setErrorImage(R.drawable.image_error);
			// mAbImageDownloader.setNoImage(R.drawable.image_no);
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
			String image = imageList.get(position);
			ViewHolder holder;
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = mInflater.inflate(R.layout.gridview_item, null);
				holder.mImg = (ImageView) contentView.findViewById(R.id.mImage);
				if (image.contains("3gp") || image.contains("mp4")) {
					holder.mImg.setImageResource(R.drawable.video_play_btn);
				}else{
					application.IMAGE_CACHE.get(image, holder.mImg);
				}
				// mAbImageDownloader.display(holder.mImg,image);
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
//			TextView tv = (TextView) contentView.findViewById(R.id.tv_armName);
//			tv.setText(mVoicesList.get(position));
			return contentView;
		}

	}
}
