package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout.LayoutParams;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbStrUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.activity.ShenpiDetailActivity.MAdapter;
import com.ytint.wloaa.activity.ShenpiDetailActivity.ViewHolder;
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
public class ZhiliangListActivity extends AbActivity{

	Context context = null;
	private MyApplication application;
	private List<Shenpi> shenpiList = new ArrayList<Shenpi>();
	private AbImageDownloader mAbImageDownloader = null;
	private ShenpiListAdapter listItemAdapter;
	private int from;
	private int whichOne;
	
	String TAG = "ShenpiActivity";
	private ProgressDialog mProgressDialog;
	private String loginKey;
	
	@AbIocView(id = R.id.shenpi_list)
	ListView shenpiListView;
	
	@AbIocView(id = R.id.showtitle)
	LinearLayout showtitle;
	
	@AbIocView(id = R.id.titlebar)
	TextView titlebar;
	
	@AbIocView(id = R.id.addShenpi)
	RelativeLayout addShenpi;
	@AbIocView(id = R.id.report_list)
	RadioButton report_list;
	@AbIocView(id = R.id.send_list)
	RadioButton send_list;
//	@AbIocView(id = R.id.radiogroup1)
//	RadioGroup selectList;
	
	private int select_show=1;
	private ArrayList<String> imageList=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_zhilianglist);
		context = ZhiliangListActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		
		initUI();
		initData();
		getGroupData();
	}

//	private void reCreate() {
//		setContentView(R.layout.layout_zhilianglist);
//		initUI();
//		initData();
//		getGroupData();
//	}

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
		String url;
		if (whichOne!=3) {
			url=String.format("%s?user_id=%s&p=1&ps=100&department_id=%d&task_type=%d&status=%d", URLs.TASKLIST,loginKey,from,select_show,whichOne);
		}else{
			url=String.format("%s?user_id=%s&p=1&ps=100&department_id=%d&task_type=%d", URLs.TASKLIST,loginKey,from,select_show);
		}
		Log.d(TAG, url);
		mAbHttpUtil.get(url,
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
		Intent intent = getIntent();
		whichOne = Integer.parseInt(intent.getExtras().get("whichOne").toString());
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		if (from==1) {
			if (whichOne==1) {
				titlebar.setText("质量检查-已完成任务");
			}else if(whichOne==2){
				titlebar.setText("质量检查-未完成任务");
			}else{
				titlebar.setText("质量检查-所有任务列表");
			}
		}else if(from==2){
			if (whichOne==1) {
				titlebar.setText("安全检查-已完成任务");
			}else if(whichOne==2){
				titlebar.setText("安全检查-未完成任务");
			}else{
				titlebar.setText("安全检查-所有任务列表");
			}
		}else{
			if (whichOne==1) {
				titlebar.setText("执法管理-已完成任务");
			}else if(whichOne==2){
				titlebar.setText("执法管理-未完成任务");
			}else{
				titlebar.setText("执法管理-所有任务列表");
			}
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
		report_list.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				select_show=1;
				getGroupData();
			}
		});
		send_list.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				select_show=2;
				getGroupData();
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
		           Intent intent = new Intent(ZhiliangListActivity.this, ShenpiDetailActivity.class);  
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
//    		// 图片下载器
//    		mAbImageDownloader = new AbImageDownloader(context);
//    		mAbImageDownloader.setWidth(80);
//    		mAbImageDownloader.setHeight(60);
//    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
//    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
//    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
//    		mAbImageDownloader.setNoImage(R.drawable.image_no);
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
//        	TextView abstr = null;
        	TextView timeView = null;
        	TextView topeo = null;
        	GridView gridView_image_list=null;
        	
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
            gridView_image_list=(GridView)convertView
            		.findViewById(R.id.gridView_image_list);
            if (news.attachment==""&&news.media=="") {
            	gridView_image_list.setVisibility(View.GONE);
			}
//            abstr = (TextView) convertView
//            		.findViewById(R.id.shenpi_contnet);
            timeView = (TextView) convertView
            		.findViewById(R.id.shenpi_create_time);
//			}
            String html="【";
            if (from==1) {
				html+="质量检查】  &nbsp;&nbsp;";
			}else if (from==2) {
				html+="安全检查】 &nbsp;&nbsp; ";
			}else{
				html+="执法管理】  &nbsp;&nbsp;";
			}
             html+="<font color='#A00000'>&nbsp;"+news.create_user_name+"&nbsp;&nbsp;&nbsp;</font>"+news.name+"&nbsp;&nbsp;&nbsp;<font color='#505050'>"+news.company_name+"</font>"; 
            frompeo.setText(Html.fromHtml(html));  
            topeo.setText("接收人："+news.receive_user_name);
//            abstr.setText(news.content);
            timeView.setText("申请时间："+news.create_time.toString());
            imageList=new ArrayList<String>();
            if (news.attachment!="") {
				for (int i = 0; i < news.attachment.split(",").length; i++) {
					imageList.add(URLs.URL_API_HOST+news.attachment.split(",")[i]);
				}
			}
            MAdapter mAdapter = new MAdapter(context);
            gridView_image_list.setAdapter(mAdapter);
            LayoutParams params = new LayoutParams(mAdapter.getCount() * (120 + 10),
                    LayoutParams.WRAP_CONTENT);
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
    		mAbImageDownloader = new AbImageDownloader(mContext);
    		mAbImageDownloader.setWidth(120);
    		mAbImageDownloader.setHeight(100);
    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
    		mAbImageDownloader.setNoImage(R.drawable.image_no);
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
            ViewHolder holder;
            if (contentView == null) {
                holder = new ViewHolder();
                contentView = mInflater.inflate(R.layout.gridview_item, null);
                holder.mImg = (ImageView) contentView.findViewById(R.id.mImage);
                mAbImageDownloader.display(holder.mImg,image);
            } else {
                holder = (ViewHolder) contentView.getTag();
            }
            contentView.setTag(holder);
            return contentView;
        }

    }
}