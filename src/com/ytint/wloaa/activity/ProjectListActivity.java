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
import com.ytint.wloaa.activity.TaskDetailActivity.ViewHolder;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Project;
import com.ytint.wloaa.bean.ProjectList;
import com.ytint.wloaa.bean.Shenpi;
import com.ytint.wloaa.bean.ShenpiInfoList;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.widget.AbPullListView;

/**
 * 显示所有的工程列表 要可以查询
 * 
 * @author wlj
 *
 */
public class ProjectListActivity extends AbActivity {
	Context context = null;
	private MyApplication application;
	private List<Project> projects=new ArrayList<Project>();
	private AbImageDownloader mAbImageDownloader = null;
	private ShenpiListAdapter listItemAdapter;
	private int from;
	private int whichOne = 1;
	Button search;
	EditText edit_text;
	String TAG = "ProjectListActivity";
	private ProgressDialog mProgressDialog;
	private String loginKey;

	@AbIocView(id = R.id.project_list)
	AbPullListView projectListView;

	@AbIocView(id = R.id.showsearch)
	LinearLayout showsearch;


	@AbIocView(id = R.id.backproject)
	RelativeLayout backproject;

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
		setContentView(R.layout.layout_projectlist);
		context = ProjectListActivity.this;
		loginKey = application.getProperty("loginKey");

		initUI();
		initData();
		getGroupData();
		initListView();
	}
	/**
	 * 初始化要用到的元素
	 */
	private void initUI() {
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		Rect frame = new Rect();
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT)
				.getTop();
		// statusBarHeight是上面所求的状态栏的高度
		int titleBarHeight = contentTop - statusBarHeight;
		showsearch.setMinimumHeight(titleBarHeight);
		backproject.setOnClickListener(new View.OnClickListener() {
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
							InputMethodManager imm = (InputMethodManager) ProjectListActivity.this
									.getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
							getGroupData();
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
					InputMethodManager imm = (InputMethodManager) ProjectListActivity.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
					getGroupData();

			}
		});
		

	}


	public void initListView() {
		// 绑定刷新和加载更多
		projectListView.setAbOnListViewListener(new AbOnListViewListener() {

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
		String searchText = edit_text.getText().toString();
		String url = String.format(
				"%s?p=%d&ps=%d&keywords=%s",
				host+URLs.PROJECTLIST,  page, Constants.PAGE_SIZE,searchText);
		Log.e("url", url);

		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					ProjectList cList = ProjectList.parseJson(content);
					if (cList.code == 200) {
						 List<Project> tempList = cList.getInfo();
						 if (tempList != null && tempList.size() > 0) {
							 projects.addAll(tempList);
							 listItemAdapter.notifyDataSetChanged();
							 tempList.clear();
						}else{
							page--;
						}
						 if (projects.size() <= 0) {
								UIHelper.ToastMessage(ProjectListActivity.this,
										"网络连接失败！");
							}
					} else {
						UIHelper.ToastMessage(context, cList.msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
					UIHelper.ToastMessage(ProjectListActivity.this, "数据解析失败");
				}
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				UIHelper.ToastMessage(ProjectListActivity.this, "网络连接失败！");
				page--;
			}

			@Override
			public void onStart() {
			}

			// 完成后调用
			@Override
			public void onFinish() {
				projectListView.stopLoadMore();

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
			if (projects.size() <= 0) {
				listItemAdapter.notifyDataSetChanged();
			}
			UIHelper.ToastMessage(context, "请检查网络连接");
			projectListView.stopRefresh();
			return;
		}
		String searchText = edit_text.getText().toString();
		String url = String.format(
				"%s?p=%d&ps=%d&keywords=%s",
				host+URLs.PROJECTLIST, page, Constants.PAGE_SIZE,searchText);
		Log.d(TAG, url);
		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				Log.d(TAG, content);
				try {
					ProjectList cList = ProjectList.parseJson(content);
					if (cList.code == 200) {
						projects = cList.getInfo();
						listItemAdapter.notifyDataSetChanged();
					} else {
						UIHelper.ToastMessage(context, cList.msg);
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
				if (projects.size() <= 0) {
					listItemAdapter.notifyDataSetChanged();
				}
				projectListView.stopRefresh();
			};
		});

	}

	
	// 初始化绑定数据
	private void initData() {
		application = (MyApplication) this.getApplication();
		if (projectListView == null)
			return;
		listItemAdapter = new ShenpiListAdapter(this);
		// 第三步：给listview设置适配器（view）
		projectListView.setAdapter(listItemAdapter);
		projectListView
				.setOnItemClickListener(new ListView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int index, long arg3) {
						//点击进入项目详情
						Integer project_id = projects.get(index-1).id;
						Intent intent = new Intent(ProjectListActivity.this,
								ProjectDetailActivity.class);
						intent.putExtra("project_id", project_id);
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
			return projects.size();// ListView的条目数
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
			Project news = projects.get(position);

			TextView name = null;
			TextView address = null;
			TextView starttime = null;
			TextView endtime = null;
            convertView = mInflater.inflate(R.layout.listitem_project, null);
            name = (TextView) convertView
					.findViewById(R.id.projectname);
            address = (TextView) convertView
            		.findViewById(R.id.address);
            starttime = (TextView) convertView
            		.findViewById(R.id.starttime);
            endtime = (TextView) convertView
            		.findViewById(R.id.endtime);
            name.setText("工程名称："+news.name);
            address.setText("工程地址："+news.address);
            starttime.setText("开始时间："+news.starttime);
            endtime.setText("结束时间："+news.endtime);
			return convertView;
		}

	}

}