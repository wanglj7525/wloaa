package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.listener.AbOnListViewListener;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Qunfa;
import com.ytint.wloaa.bean.QunfaInfoList;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.widget.AbPullListView;

/**
 *消息列表 公告列表
 * @author wlj
 * @date 2015-6-13上午11:14:05
 */
public class XiaoxGaoListActivity extends AbActivity{

	Context context = null;
	private MyApplication application;
	private List<Qunfa> qunfaList = new ArrayList<Qunfa>();
	private AbImageDownloader mAbImageDownloader = null;
	private QunfaListAdapter listItemAdapter;
	
	String TAG = "XiaoxGaoListActivity";
	private ProgressDialog mProgressDialog;
	private String loginKey;
	
	@AbIocView(id = R.id.qunfa_list)
	AbPullListView qunfaListView;
	@AbIocView(id = R.id.titlebarxs)
	TextView titlebar;
	@AbIocView(id = R.id.showtitle)
	LinearLayout showtitle;
	@AbIocView(id = R.id.addShenpi)
	RelativeLayout addShenpi;
	private int from;
	private int page = 1;
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
	String host;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application= (MyApplication) this.getApplication();
		host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		setContentView(R.layout.layout_xiaoxi);
		context = XiaoxGaoListActivity.this;
		loginKey = application.getProperty("loginKey");
		initData();
		getGroupData();
		
		if (from==0) {
			titlebar.setText("公告列表");
		}else{
			titlebar.setText("消息列表");
		}
		
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;
		int contentTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();  
		//statusBarHeight是上面所求的状态栏的高度  
		int titleBarHeight = contentTop - statusBarHeight ;
		showtitle.setMinimumHeight(titleBarHeight);
		addShenpi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		initListView();
	}

	public void initListView() {
		// 绑定刷新和加载更多
		qunfaListView.setAbOnListViewListener(new AbOnListViewListener() {

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
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		page++;
		String url=String.format("%s?user_id=%s&notice_type=%d&p=%d&ps=%d", host+URLs.QUNFALIST,
				loginKey,from,page, Constants.PAGE_SIZE);
		Log.e("url", url);

		mAbHttpUtil.get(url, new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					QunfaInfoList gList = QunfaInfoList
							.parseJson(content);
					if (gList.code == 200) {
						 List<Qunfa> tempList = gList.getInfo();
						 if (tempList != null && tempList.size() > 0) {
							 qunfaList.addAll(tempList);
							 listItemAdapter.notifyDataSetChanged();
							 tempList.clear();
						}else{
							page--;
						}
						 if (qunfaList.size() <= 0) {
								UIHelper.ToastMessage(XiaoxGaoListActivity.this,
										"网络连接失败！");
							}
					} else {
						UIHelper.ToastMessage(context, gList.msg);
					}

				} catch (Exception e) {
					e.printStackTrace();
					UIHelper.ToastMessage(XiaoxGaoListActivity.this, "数据解析失败");
				}
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				UIHelper.ToastMessage(XiaoxGaoListActivity.this, "网络连接失败！");
				page--;
			}

			@Override
			public void onStart() {
			}

			// 完成后调用
			@Override
			public void onFinish() {
				qunfaListView.stopLoadMore();

			};
		});
	}
	//初始化绑定数据 获取该用户参与的所有审批列表
	private void getGroupData() {
		page=1;
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			if (qunfaList.size() <= 0) {
				listItemAdapter.notifyDataSetChanged();
			}
			UIHelper.ToastMessage(context, "请检查网络连接");
			qunfaListView.stopRefresh();
			return;
		}
		String url=String.format("%s?user_id=%s&notice_type=%d&p=%d&ps=%d", host+URLs.QUNFALIST,
				loginKey,from,page, Constants.PAGE_SIZE);
		Log.e(TAG,url);
		mAbHttpUtil.get(url,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							QunfaInfoList gList = QunfaInfoList
									.parseJson(content);
							if (gList.code == 200) {
								qunfaList = gList.getInfo();
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
						if (qunfaList.size() <= 0) {
							listItemAdapter.notifyDataSetChanged();
						}
						qunfaListView.stopRefresh();
					};
				});

	}
	
	// 初始化绑定数据
    private void initData() {
    	application=(MyApplication) this.getApplication();
        if (qunfaListView == null)
            return;
        listItemAdapter = new QunfaListAdapter(this);
        // 第三步：给listview设置适配器（view）
        qunfaListView.setAdapter(listItemAdapter);
        qunfaListView.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
					Integer message_id = qunfaList.get(index-1).id;
		           Intent intent = new Intent(XiaoxGaoListActivity.this, XiaoxiShowActivity.class);  
		           intent.putExtra("message_id", message_id);
		           intent.putExtra("from", from);
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
//		if (topicInfoList_show.size() <= 0) {
//			reCreate();
//		} else {
//			CommonDocsListActivity cl = (CommonDocsListActivity) getLocalActivityManager()
//					.getActivity("topic_activity" + selectedId);
//			cl.showRefreshHead();
//			cl.loadDocs(false, true);
//		}
	}
	
    /** 自定义适配器 */  
    public class QunfaListAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;// 动态布局映射  
  
        public QunfaListAdapter(Context context) {  
            this.mInflater = LayoutInflater.from(context);  
    		// 图片下载器
    		mAbImageDownloader = new AbImageDownloader(context);
    		mAbImageDownloader.setWidth(80);
    		mAbImageDownloader.setHeight(60);
    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
    		mAbImageDownloader.setNoImage(R.drawable.image_no);
        }  
  
        // 决定ListView有几行可见  
        @Override  
        public int getCount() {  
            return qunfaList.size();// ListView的条目数  
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
        	Qunfa news = qunfaList.get(position);
        	
        	TextView frompeo = null;
        	TextView abstr = null;
        	TextView timeView = null;
        	TextView topeo = null;
	            convertView = mInflater.inflate(R.layout.listitem_qunfalist_noimage, null);
	            frompeo = (TextView) convertView
						.findViewById(R.id.push_user_id);
	            topeo = (TextView) convertView
	            		.findViewById(R.id.receive_user_ids);
	            abstr = (TextView) convertView
						.findViewById(R.id.content);
				timeView = (TextView) convertView
						.findViewById(R.id.create_time);
			frompeo.setText(news.title);
			topeo.setText(" "+news.push_user_name);
			String abstrs=news.content;
			if (abstrs.length()>=100) {
				abstrs=abstrs.substring(0, 99)+"...";
			}
			abstr.setText(abstrs);
			timeView.setText(news.create_time_string);
            return convertView;  
        }

    }  

}