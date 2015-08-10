package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.bean.Xiapai;
import com.ytint.wloaa.bean.XiapaiList;

/**
 * 下派任务列表
 * 显示所有该用户相关的任务列表
 * 可以点击添加下派新的任务
 * 经过自己的任务点击下派给下级
 * 显示作者 下派人 时间 内容
 * @author wlj
 * @date 2015-6-13下午4:30:29
 */
public class XiapaiActivity extends BaseActivity{

	Context context = null;
	private MyApplication application;
	private List<Xiapai> xiapaiList = new ArrayList<Xiapai>();
	private AbImageDownloader mAbImageDownloader = null;
	private XiapaiListAdapter listItemAdapter;
	
	String TAG = "CollectingSourceActivity";
	private ProgressDialog mProgressDialog;
	private String loginKey;
	
	@AbIocView(id = R.id.xiapai_list)
	ListView xiapaiListView;
	@AbIocView(id = R.id.addXiapai)
	RelativeLayout addXiapai;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_xiapai);

		context = XiapaiActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");

		initUI();
		initData();
		getGroupData();
	}

	private void reCreate() {
		setContentView(R.layout.layout_xiapai);
		initUI();
		initData();
		getGroupData();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getGroupData();
	}
	private void getGroupData() {
		
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		Log.d(TAG, String.format("%s?user_id=%s&user_type=2&notice_type=1&p=1&ps=20", URLs.QUNFALIST,
				loginKey));
		mAbHttpUtil.get(URLs.QUNFALIST + "?user_type=2&notice_type=1&p=1&ps=20&user_id=" + loginKey,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							XiapaiList gList = XiapaiList
									.parseJson(content);
							if (gList.code == 200) {
								xiapaiList = gList.getInfo();
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
					};
				});

	}



	/**
	 * 初始化要用到的元素
	 */
	private void initUI() {
		
		addXiapai.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(XiapaiActivity.this,
						AddZhiliangSendActivity.class);
				startActivity(intent);
			}
		});
	}
	// 初始化绑定数据
    private void initData() {
    	application=(MyApplication) this.getApplication();
        if (xiapaiListView == null)
            return;
        listItemAdapter = new XiapaiListAdapter(this);
        // 第三步：给listview设置适配器（view）
        xiapaiListView.setAdapter(listItemAdapter);
        xiapaiListView.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				//点击进入 审批事项 详情页
				//TODO
//				   String url = (String) hotNewsList.get(index).u;
//				   Long doc_id = hotNewsList.get(index)._id;
//		           Intent intent = new Intent(HomeActivity.this, DocWebDetailActivity.class);  
//		           intent.putExtra("docid", doc_id);
//		           intent.putExtra("doc_url", url);
//		           startActivity(intent);

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
//		if (categoryList_show.size() > 0) {
//			CommonDocsListActivity cl = (CommonDocsListActivity) getLocalActivityManager()
//					.getActivity("category_activity" + selectedId);
//			cl.showRefreshHead();
//			cl.loadDocs(false, true);
//		} else {
//			reCreate();
//		}
	}
	 /** 自定义适配器 */  
    public class XiapaiListAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;// 动态布局映射  
  
        public XiapaiListAdapter(Context context) {  
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
            return xiapaiList.size();// ListView的条目数  
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
        	Xiapai news = xiapaiList.get(position);
        	
        	TextView frompeo = null;
        	TextView abstr = null;
        	TextView timeView = null;
        	TextView topeo = null;
//			//列表中有图片
//			if (news.pic!=null&&news.pic!=""&&news.pic.split(",").length > 0) {
//	            convertView = mInflater.inflate(R.layout.listitem_xiapailist, null);//根据布局文件实例化view  
//	            frompeo = (TextView) convertView
//						.findViewById(R.id.frompeople);
//	            topeo = (TextView) convertView
//	            		.findViewById(R.id.topeople);
//	            
//	            abstr = (TextView) convertView
//						.findViewById(R.id.itemsAbstract);
//	            timeView = (TextView) convertView
//						.findViewById(R.id.time);
//				ImageView imageView = (ImageView) convertView
//						.findViewById(R.id.itemsImage);
//				mAbImageDownloader.display(imageView,news.pic.split(",")[0]);
//				if (news.pic.split(",").length>=2) {
//					ImageView imageView2 = (ImageView) convertView
//							.findViewById(R.id.itemsImage2);
//					mAbImageDownloader.display(imageView2,news.pic.split(",")[1]);
//				}
//				if (news.pic.split(",").length>=3) {
//					ImageView imageView3 = (ImageView) convertView
//							.findViewById(R.id.itemsImage3);
//					mAbImageDownloader.display(imageView3,news.pic.split(",")[2]);
//				}
//				
//			}else{
//				//列表中有视频
//	            convertView = mInflater.inflate(R.layout.listitem_xiapailist_noimage, null);
//	            frompeo = (TextView) convertView
//						.findViewById(R.id.frompeople);
//	            topeo = (TextView) convertView
//	            		.findViewById(R.id.topeople);
//	            abstr = (TextView) convertView
//						.findViewById(R.id.itemsAbstract);
//				timeView = (TextView) convertView
//						.findViewById(R.id.time);
//			}
//			frompeo.setText("下发人："+news.frompeo);
//			topeo.setText(news.topeo);
//			abstr.setText(news.c);
//			timeView.setText(news.pt.toString());
        	
        	
	        convertView = mInflater.inflate(R.layout.listitem_xiapailist_noimage, null);
	        frompeo = (TextView) convertView
					.findViewById(R.id.push_user_id);
	        topeo = (TextView) convertView
	        		.findViewById(R.id.receive_user_ids);
	        abstr = (TextView) convertView
					.findViewById(R.id.content);
			timeView = (TextView) convertView
					.findViewById(R.id.create_time);
//			frompeo.setText("下派人："+news.push_user_name);
//			topeo.setText(news.receive_user_name);
			frompeo.setText(news.title);
			topeo.setText(" "+news.push_user_name);
			abstr.setText(news.content);
			timeView.setText(news.create_time.toString());
	        return convertView;  
        }

    }  
}
