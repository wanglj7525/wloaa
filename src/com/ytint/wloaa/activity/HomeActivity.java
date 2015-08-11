//package com.ytint.wloaa.activity;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.MediaController;
//import android.widget.TextView;
//import android.widget.VideoView;
//
//import com.ab.bitmap.AbImageDownloader;
//import com.ab.global.AbConstant;
//import com.ab.http.AbHttpUtil;
//import com.ab.http.AbStringHttpResponseListener;
//import com.ab.view.ioc.AbIocView;
//import com.ytint.wloaa.R;
//import com.ytint.wloaa.app.Constants;
//import com.ytint.wloaa.app.MyApplication;
//import com.ytint.wloaa.bean.HotNews;
//import com.ytint.wloaa.bean.HotNewsList;
//import com.ytint.wloaa.bean.URLs;
//
///**
// * 现场执法 一个手机拍照或录制视频后能上传，另外一个手机能看到 
// * 列表显示所有该手机收到的照片和视频
// * 点击一个按钮，选择拍照或视频，完成后选择联系人，发送
// * @author wlj
// * @date 2015-6-12上午9:32:13
// */
//public class HomeActivity  extends BaseActivity{
//	ListView hotDocListView;
//	 private List<HotNews> hotNewsList = new ArrayList<HotNews>();
//	 private AbImageDownloader mAbImageDownloader = null;
//	private HotNewsListAdapter listItemAdapter;
//	final int MaxHotNewsListNum = 20;
//	int size = 1;
//	String TAG = "HomeActivity";
//	MediaController mMediaController;
//	// 获取Http工具类
//	private MyApplication application;
//	
//	@AbIocView(id = R.id.photo_button)
//	Button photo_button;
//	@AbIocView(id = R.id.video_button)
//	Button video_button;
//	
//    // 初始化组件
//    private void initWidget() {
//    	hotDocListView = (ListView) findViewById(R.id.hot_doc_list);
//    	
//    	//拍照
//    	photo_button.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(HomeActivity.this, PhotoActivity.class);  
//		           startActivity(intent);
//			}
//		});
//    }
//    // 初始化绑定数据
//    private void initData() {
//    	application=(MyApplication) this.getApplication();
//        if (hotDocListView == null)
//            return;
//        listItemAdapter = new HotNewsListAdapter(this);
//        // 第三步：给listview设置适配器（view）
//        hotDocListView.setAdapter(listItemAdapter);
//        hotDocListView.setOnItemClickListener(new ListView.OnItemClickListener(){
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
//					long arg3) {
//				//点击照片或视频 放大或播放
//				//TODO
////				   String url = (String) hotNewsList.get(index).u;
////				   Long doc_id = hotNewsList.get(index)._id;
////		           Intent intent = new Intent(HomeActivity.this, DocWebDetailActivity.class);  
////		           intent.putExtra("docid", doc_id);
////		           intent.putExtra("doc_url", url);
////		           startActivity(intent);
//
//	        }
//        });
//      //  appendData();
//        // 第二步：new一个适配器（controller）
//        // 参数1：Context
//        // 参数2：listview的item布局
//        // 参数3：数据填充在item布局下的那个控件id
//        // 参数4：填充的数据
//    }
//    /**
//     * 刷新数据
//     */
//    public void refresh(){
//    	hotNewsList.clear();
//    	loadHotTopNews();
//    }
//	public void refresh_show_page() {
//		loadHotTopNews();
////		if (topicInfoList_show.size() <= 0) {
////			reCreate();
////		} else {
////			CommonDocsListActivity cl = (CommonDocsListActivity) getLocalActivityManager()
////					.getActivity("topic_activity" + selectedId);
////			cl.showRefreshHead();
////			cl.loadDocs(false, true);
////		}
//	}
//	@Override
//	protected void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		loadHotTopNews();
//	}
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//    	super.onCreate(savedInstanceState);
//    	setContentView(R.layout.layout_home);
//        initWidget();
//        initData();
//        //Create media controller
//        mMediaController = new MediaController(this);
//        loadHotTopNews();
//		
//
//    }
//    public void loadHotTopNews() {    	
//		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
//		mAbHttpUtil.setDebug(true);
//		String loginKey = application.getProperty("loginKey");
//		Log.d(TAG, loginKey);
//		//获取现场发布的 该手机的所有照片和视频列表
//		//TODO 通过后台获取现场发布的 该手机的所有照片和视频列表
//		String urlString = String.format("%s?user_id=0",URLs.PHOTOLIST);
//		Log.d(TAG,urlString);
//		mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {
//			// 获取数据成功会调用这里
//			@Override
//			public void onSuccess(int statusCode, String content) {
//				Log.d(TAG, content);
//				try {
//					HotNewsList gList = HotNewsList
//							.parseJson(content);
//					if (gList.code == 200) {
//						hotNewsList = gList.getInfo();
//						listItemAdapter.notifyDataSetChanged();
//					} else {
//						showToast(gList.msg);
//					}
//					
//				} catch (Exception e) {
//					e.printStackTrace();
//					showToast("数据解析失败");
//				}
//			}
//
//			// 失败，调用
//			@Override
//			public void onFailure(int statusCode, String content,
//					Throwable error) {
//
//				Log.d(TAG, "onFailure");
//				showToast("网络连接失败！");
//			}
//
//			// 开始执行前
//			@Override
//			public void onStart() {
//				Log.d(TAG, "onStart");
//				// 显示进度框
//				showProgressDialog();
//				
//			}
//
//			// 完成后调用，失败，成功
//			@Override
//			public void onFinish() {
//				Log.d(TAG, "onFinish");
//				// 移除进度框
//				removeProgressDialog();
//
//			};
//
//		});
//
//	}
//    
//    /** 自定义适配器 */  
//    public class HotNewsListAdapter extends BaseAdapter {  
//        private LayoutInflater mInflater;// 动态布局映射  
//  
//        public HotNewsListAdapter(Context context) {  
//            this.mInflater = LayoutInflater.from(context);  
//    		// 图片下载器
//    		mAbImageDownloader = new AbImageDownloader(context);
//    		mAbImageDownloader.setWidth(200);
//    		mAbImageDownloader.setHeight(150);
//    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
//    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
//    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
//    		mAbImageDownloader.setNoImage(R.drawable.image_no);
//        }  
//  
//        // 决定ListView有几行可见  
//        @Override  
//        public int getCount() {  
//            return hotNewsList.size();// ListView的条目数  
//        }  
//  
//        @Override  
//        public Object getItem(int arg0) {  
//            return null;  
//        }  
//  
//        @Override  
//        public long getItemId(int arg0) {  
//            return 0;  
//        }  
//  
//        @Override  
//        public View getView(int position, View convertView, ViewGroup parent) {  
//        	HotNews news = hotNewsList.get(position);
//        	
//        	TextView title = null;
//        	TextView abstr = null;
//        	TextView timeView = null;
//			//列表中有图片
//			if (news.type==1) {
//	            convertView = mInflater.inflate(R.layout.listitem_hotdoclist, null);//根据布局文件实例化view  
//	            title = (TextView) convertView
//						.findViewById(R.id.itemsTitle);
//	            
//	            abstr = (TextView) convertView
//						.findViewById(R.id.itemsAbstract);
//	            timeView = (TextView) convertView
//						.findViewById(R.id.time);
//				ImageView imageView = (ImageView) convertView
//						.findViewById(R.id.itemsImage);
//				mAbImageDownloader.display(imageView,URLs.URL_API_HOST+news.url);
//				
////			}else{
////				//列表中有视频
////	            convertView = mInflater.inflate(R.layout.listitem_hotdoclist_noimage, null);
////	            VideoView mVideoView = (VideoView)convertView.findViewById(R.id.videoView1);
////	            Uri uri = Uri.parse(news.mov);  
////	            mVideoView.setVideoURI(uri);  
//////	            mVideoView.setMediaController(mMediaController);  
////	            /** 
////	             * 视频或者音频到结尾时触发的方法 
////	             */  
////	            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
////	                @Override  
////	                public void onCompletion(MediaPlayer mp) {  
////	                    Log.i("通知", "完成");  
////	                }  
////	            });  
////	              
////	            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
////	                  
////	                @Override  
////	                public boolean onError(MediaPlayer mp, int what, int extra) {  
////	                    Log.i("通知", "播放中出现错误");  
////	                    return false;  
////	                }  
////	            });  
////	            title = (TextView) convertView
////						.findViewById(R.id.itemsTitle);
////	            abstr = (TextView) convertView
////						.findViewById(R.id.itemsAbstract);
////				timeView = (TextView) convertView
////						.findViewById(R.id.time);
//			}
//			title.setText(news.user_name);
//			abstr.setText(news.content);
//			if (news.content==""||news.content==null) {
//				abstr.setVisibility(View.GONE);
//			}
//			timeView.setText(news.create_time.toString());
//            return convertView;  
//        }
//
//    }  
//}