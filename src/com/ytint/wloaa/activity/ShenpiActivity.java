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
import com.ytint.wloaa.bean.Shenpi;
import com.ytint.wloaa.bean.ShenpiInfoList;
import com.ytint.wloaa.bean.URLs;

/**
 * 审批
 * 列表显示所有的该手机号或用户参与的审批事件
 * 点击可以查看详细 可以操作后续步骤 或者操作过后只能查看
 * 经过自己的审批事项 审批后报给上级
 * @author wlj
 * @date 2015-6-13上午11:14:05
 */
public class ShenpiActivity extends BaseActivity{

	Context context = null;
	private MyApplication application;
	private List<Shenpi> shenpiList = new ArrayList<Shenpi>();
	private AbImageDownloader mAbImageDownloader = null;
	private ShenpiListAdapter listItemAdapter;
	
	String TAG = "ShenpiActivity";
	private ProgressDialog mProgressDialog;
	private String loginKey;
	
	@AbIocView(id = R.id.shenpi_list)
	ListView shenpiListView;
	
	@AbIocView(id = R.id.addShenpi)
	RelativeLayout addShenpi;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_shenpi);

		context = ShenpiActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		initUI();
		initData();
		getGroupData();
	}

	private void reCreate() {
		setContentView(R.layout.layout_shenpi);
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
	
	//初始化绑定数据 获取该用户参与的所有审批列表
	private void getGroupData() {
		
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		Log.d(TAG, String.format("%s?verify_step=3&user_id=0&p=1&ps=20", URLs.SHENPILIST));
		mAbHttpUtil.get(String.format("%s?verify_step=3&user_id=0&p=1&ps=20", URLs.SHENPILIST),
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							ShenpiInfoList gList = ShenpiInfoList
									.parseJson(content);
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
					};
				});

	}
	
	/**
	 * 初始化要用到的元素
	 */
	private void initUI() {
		
		addShenpi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShenpiActivity.this,
						AddShenpiActivity.class);
				startActivity(intent);
			}
		});

	}
	// 初始化绑定数据
    private void initData() {
    	application=(MyApplication) this.getApplication();
        if (shenpiListView == null)
            return;
        listItemAdapter = new ShenpiListAdapter(this);
        // 第三步：给listview设置适配器（view）
        shenpiListView.setAdapter(listItemAdapter);
        shenpiListView.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				//点击进入 审批事项 详情页
				   Integer shenpi_id = shenpiList.get(index).id;
		           Intent intent = new Intent(ShenpiActivity.this, ShenpiDetailActivity.class);  
		           intent.putExtra("shenpi_id", shenpi_id);
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
    public class ShenpiListAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;// 动态布局映射  
  
        public ShenpiListAdapter(Context context) {  
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
        	TextView abstr = null;
        	TextView timeView = null;
        	TextView topeo = null;
//			//列表中有图片
//			if (news.pic!=null&&news.pic!=""&&news.pic.split(",").length > 0) {
//	            convertView = mInflater.inflate(R.layout.listitem_shenpilist, null);//根据布局文件实例化view  
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
//	            convertView = mInflater.inflate(R.layout.listitem_shenpilist_noimage, null);
//	            frompeo = (TextView) convertView
//						.findViewById(R.id.frompeople);
//	            topeo = (TextView) convertView
//	            		.findViewById(R.id.topeople);
//	            abstr = (TextView) convertView
//						.findViewById(R.id.itemsAbstract);
//				timeView = (TextView) convertView
//						.findViewById(R.id.time);
//			}
//			frompeo.setText("申请人："+news.frompeo);
//			topeo.setText(news.topeo);
//			abstr.setText(news.c);
//			timeView.setText(news.pt.toString());
//            return convertView;  
            //列表中有视频
            convertView = mInflater.inflate(R.layout.listitem_shenpilist_noimage, null);
            frompeo = (TextView) convertView
            		.findViewById(R.id.apply_user_name);
            topeo = (TextView) convertView
            		.findViewById(R.id.first_verify_user_name);
            abstr = (TextView) convertView
            		.findViewById(R.id.shenpi_contnet);
            timeView = (TextView) convertView
            		.findViewById(R.id.shenpi_create_time);
//			}
            frompeo.setText("申请人："+news.apply_user_name);
            topeo.setText(news.first_verify_user_name);
            abstr.setText(news.content);
            timeView.setText("申请时间："+news.create_time.toString());
            return convertView;  
        }

    }  

}
