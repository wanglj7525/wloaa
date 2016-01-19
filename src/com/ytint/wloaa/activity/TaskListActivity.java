package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.util.CacheManager;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.listener.AbOnListViewListener;
import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.ShenpiDetailActivity.ViewHolder;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Shenpi;
import com.ytint.wloaa.bean.ShenpiInfoList;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.widget.AbPullListView;

/**
 * 显示所有的任务列表 要可以查询
 * 
 * @author wlj
 * @date 2015-6-13上午11:14:05
 */
public class TaskListActivity extends AbActivity {
	Context context = null;
	private MyApplication application;
	private List<Shenpi> shenpiList = new ArrayList<Shenpi>();
	private AbImageDownloader mAbImageDownloader = null;
	private ShenpiListAdapter listItemAdapter;
	private int from;
	private int whichOne = 1;

	String TAG = "ZhiliangListActivity";
	private ProgressDialog mProgressDialog;
	private String loginKey;

	@AbIocView(id = R.id.shenpi_list)
	AbPullListView shenpiListView;

	@AbIocView(id = R.id.showtitle)
	LinearLayout showtitle;

//	@AbIocView(id = R.id.titlebar)
//	TextView titlebar;

	@AbIocView(id = R.id.addShenpi)
	RelativeLayout addShenpi;
//	@AbIocView(id = R.id.report_list)
//	RadioButton report_list;
//	@AbIocView(id = R.id.send_list)
//	RadioButton send_list;
	// @AbIocView(id = R.id.radiogroup1)
	// RadioGroup selectList;
	Button search;
	EditText edit_text;
	private int select_show = 1;
	private int page = 1;
	private ArrayList<String> imageList = new ArrayList<String>();
	String host;
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
		setContentView(R.layout.layout_tasklist);
		context = TaskListActivity.this;
		loginKey = application.getProperty("loginKey");

		initUI();
		initData();
		getGroupData();
		initListView();
	}


	public void initListView() {
		// 绑定刷新和加载更多
		shenpiListView.setAbOnListViewListener(new AbOnListViewListener() {

			@Override
			public void onRefresh() {
				getGroupData();
			}

			@Override
			public void onLoadMore() {
				loadMore();
			}

		});
	}

	private void loadMore() {
		//TODO
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		page++;
		String url = String.format(
				"%s?user_id=%s&p=%d&ps=%d&department_id=%d&task_type=%d",
				host+URLs.TASKLIST, loginKey, page, Constants.PAGE_SIZE, from,
				select_show);
		Log.e("url", url);

		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					ShenpiInfoList gList = ShenpiInfoList.parseJson(content);
					if (gList.code == 200) {
						 List<Shenpi> tempList = gList.getInfo();
						 if (tempList != null && tempList.size() > 0) {
							 shenpiList.addAll(tempList);
							 listItemAdapter.notifyDataSetChanged();
							 tempList.clear();
						}else{
							page--;
						}
						 if (shenpiList.size() <= 0) {
								UIHelper.ToastMessage(TaskListActivity.this,
										"网络连接失败！");
							}
					} else {
						UIHelper.ToastMessage(context, gList.msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
					UIHelper.ToastMessage(TaskListActivity.this, "数据解析失败");
				}
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				UIHelper.ToastMessage(TaskListActivity.this, "网络连接失败！");
				page--;
			}

			@Override
			public void onStart() {
			}

			// 完成后调用
			@Override
			public void onFinish() {
				shenpiListView.stopLoadMore();

			};
		});
	}

	// 初始化绑定数据 获取该用户参与的所有审批列表
	private void getGroupData() {
		page=1;
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			if (shenpiList.size() <= 0) {
				listItemAdapter.notifyDataSetChanged();
			}
			UIHelper.ToastMessage(context, "请检查网络连接");
			shenpiListView.stopRefresh();
			return;
		}
		String searchText = edit_text.getText().toString();
		String url = String.format(
				"%s?user_id=%s&p=%d&ps=%d&department_id=%d&task_type=%d&keywords=%s",
				host+URLs.TASKLIST, loginKey, page, Constants.PAGE_SIZE, from,
				select_show,searchText);
		Log.d(TAG, url);
		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				Log.d(TAG, content);
				try {
					ShenpiInfoList gList = ShenpiInfoList.parseJson(content);
					if (gList.code == 200) {
						shenpiList = gList.getInfo();
						listItemAdapter.notifyDataSetChanged();
					} else {
						UIHelper.ToastMessage(context, gList.msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					UIHelper.ToastMessage(context, "数据解析失败");
				}
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				UIHelper.ToastMessage(context, "网络连接失败！");
			}

			@Override
			public void onStart() {
				showProgressDialog(null);
			}

			// 完成后调用
			@Override
			public void onFinish() {
				mProgressDialog.dismiss();
				if (shenpiList.size() <= 0) {
					listItemAdapter.notifyDataSetChanged();
				}
				shenpiListView.stopRefresh();
			};
		});

	}

	/**
	 * 初始化要用到的元素
	 */
	private void initUI() {
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
//		titlebar.setText("任务列表");
//		if (from == 1) {
//			titlebar.setText("质量检查任务列表");
//		} else if (from == 2) {
//			titlebar.setText("安全检查任务列表");
//		} else {
//			titlebar.setText("执法管理任务列表");
//		}

		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
		// statusBarHeight是上面所求的状态栏的高度
		int titleBarHeight = contentTop - statusBarHeight;
		showtitle.setMinimumHeight(titleBarHeight);

		addShenpi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		edit_text = (EditText) findViewById(R.id.EditText);
		edit_text.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
		edit_text
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							// 关闭键盘
							InputMethodManager imm = (InputMethodManager) TaskListActivity.this
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
							getGroupData();
//							recommend_search_layout.setVisibility(View.GONE);
//							change_button.setVisibility(View.GONE);
//							mAbPullListView.setVisibility(View.VISIBLE);
//							searchItemLayout.setVisibility(View.VISIBLE);
//							mAbPullListView.startRefresh();
							return true;
						}
						return false;
					}

				});
		search = (Button) findViewById(R.id.search);
		search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String search_text = edit_text.getText().toString();
//				if (search_text == null
//						|| search_text.trim().equalsIgnoreCase("")) {
//					Toast.makeText(TaskListActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
//				} else { // 关闭键盘
					InputMethodManager imm = (InputMethodManager) TaskListActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					getGroupData();
//				}

			}
		});

//		report_list.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				select_show = 1;
//				getGroupData();
//			}
//		});
//		send_list.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				select_show = 2;
//				getGroupData();
//			}
//		});

	}

	// 初始化绑定数据
	private void initData() {
		application = (MyApplication) this.getApplication();
		if (shenpiListView == null)
			return;
		listItemAdapter = new ShenpiListAdapter(this);
		// 第三步：给listview设置适配器（view）
		shenpiListView.setAdapter(listItemAdapter);
		shenpiListView
				.setOnItemClickListener(new ListView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int index, long arg3) {
						// 点击进入 审批事项 详情页
						Integer shenpi_id = shenpiList.get(index-1).id;
						Intent intent = new Intent(TaskListActivity.this,
								ShenpiDetailActivity.class);
						intent.putExtra("shenpi_id", shenpi_id);
						intent.putExtra("from", from);
						System.out.println(intent.getIntExtra("shenpi_id", 0));
						startActivity(intent);

					}
				});
	}

	public void showProgressDialog(String message) {
		// 创建一个显示进度的Dialog
		if (AbStrUtil.isEmpty(message)) {
			message = Constants.PROGRESSMESSAGE;
		}
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setCanceledOnTouchOutside(false);
		}
		mProgressDialog.setMessage(message);
		mProgressDialog.show();
	}

	public void refresh_show_page() {
		getGroupData();
	}

	/** 自定义适配器 */
	public class ShenpiListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;// 动态布局映射

		public ShenpiListAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		// 决定ListView有几行可见
		@Override
		public int getCount() {
			return shenpiList.size();// ListView的条目数
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Shenpi news = shenpiList.get(position);

			TextView frompeo = null;
			TextView timeView = null;
			TextView topeo = null;
			GridView gridView_image_list = null;

			convertView = mInflater.inflate(
					R.layout.listitem_shenpilist_noimage, null);
			frompeo = (TextView) convertView.findViewById(R.id.apply_user_name);
			topeo = (TextView) convertView
					.findViewById(R.id.first_verify_user_name);
			gridView_image_list = (GridView) convertView
					.findViewById(R.id.gridView_image_list);
			if (news.attachment == "" && news.media == "") {
				gridView_image_list.setVisibility(View.GONE);
			}
			timeView = (TextView) convertView
					.findViewById(R.id.shenpi_create_time);
//			String html = "【";
//			if (from == 1) {
//				html += "质量检查】 ";
//			} else if (from == 2) {
//				html += "安全检查】";
//			} else {
//				html += "执法管理】";
//			}
			String html="";
			html += "<font color='#A00000'>" + news.create_user_name
					+ "&nbsp;</font>" + news.name
					+ "&nbsp;<font color='#505050'>" + news.company_name
					+ "</font>&nbsp;&nbsp;";
			int flag1 = 0;
			int flag2 = 0;
			int flag3 = 0;
			if (news.attachment.length() > 0) {
				flag1 = news.attachment.split(",").length;
				if (news.attachment.contains("3gp")
						|| news.attachment.contains("mp4")) {
					flag2 = 1;
					flag1 -= 1;
				}
			}
			if (news.task_type == 1) {
				html += "<font color='#6495ED'>" + flag1
						+ "</font>图片，<font color='#6495ED'>" + flag2
						+ "</font>视频，";
			}
			if (news.media.length() > 0) {
				flag3 = news.media.split(",").length;
			}
			html += "<font color='#6495ED'>" + flag3 + "</font>录音";
			
//			if (news.task_type==1) {
//				if (news.status==2) {
//					html += "<font color='red'>&nbsp;&nbsp;【未完成】</font>";
//				}else{
//					html += "<font color='green'>&nbsp;&nbsp;【已完成】</font>";
//				}
//			}
			frompeo.setText(Html.fromHtml(html));
			topeo.setText("发布人：" + news.create_user_name);
			// abstr.setText(news.content);
			timeView.setText("发布时间：" + news.create_time_string);
			imageList = new ArrayList<String>();
			if (news.attachment != "") {
				for (int i = 0; i < news.attachment.split(",").length; i++) {
					imageList.add(host+URLs.URL_API_HOST
							+ news.attachment.split(",")[i]);
				}
			}
			MAdapter mAdapter = new MAdapter(context);
			gridView_image_list.setAdapter(mAdapter);
			LayoutParams params = new LayoutParams(mAdapter.getCount()
					* (120 + 10), LayoutParams.WRAP_CONTENT);
			gridView_image_list.setLayoutParams(params);
			gridView_image_list.setColumnWidth(120);
			gridView_image_list.setHorizontalSpacing(10);
			gridView_image_list.setStretchMode(GridView.NO_STRETCH);
			gridView_image_list.setNumColumns(mAdapter.getCount());
			return convertView;
		}

	}

	class MAdapter extends BaseAdapter {
		Context mContext;
		LayoutInflater mInflater;

		public MAdapter(Context c) {
			mContext = c;
			mInflater = LayoutInflater.from(mContext);
			// 图片下载器
//			mAbImageDownloader = new AbImageDownloader(mContext);
//			mAbImageDownloader.setWidth(120);
//			mAbImageDownloader.setHeight(100);
//			mAbImageDownloader.setType(AbConstant.SCALEIMG);
//			mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
//			mAbImageDownloader.setErrorImage(R.drawable.image_error);
//			mAbImageDownloader.setNoImage(R.drawable.image_no);
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
			if (image.contains("3gp") || image.contains("mp4")) {
				image =host+ URLs.URL_API_HOST + "public/images/video_play_btn.png";
			}
			ViewHolder holder;
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = mInflater.inflate(R.layout.gridview_item, null);
				holder.mImg = (ImageView) contentView.findViewById(R.id.mImage);
				application.IMAGE_CACHE.get(image, holder.mImg);
//				mAbImageDownloader.display(holder.mImg, image);
			} else {
				holder = (ViewHolder) contentView.getTag();
			}
			contentView.setTag(holder);
			return contentView;
		}

	}
}