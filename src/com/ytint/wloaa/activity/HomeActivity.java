package com.ytint.wloaa.activity;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.HotNews;
import com.ytint.wloaa.bean.HotNewsList;
import com.ytint.wloaa.bean.URLs;

/**
 * �ֳ�ִ�� һ���ֻ����ջ�¼����Ƶ�����ϴ�������һ���ֻ��ܿ��� 
 * �б���ʾ���и��ֻ��յ�����Ƭ����Ƶ
 * ���һ����ť��ѡ�����ջ���Ƶ����ɺ�ѡ����ϵ�ˣ�����
 * @author wlj
 * @date 2015-6-12����9:32:13
 */
public class HomeActivity  extends BaseActivity{
	ListView hotDocListView;
	 private List<HotNews> hotNewsList = new ArrayList<HotNews>();
	 private AbImageDownloader mAbImageDownloader = null;
	private HotNewsListAdapter listItemAdapter;
	final int MaxHotNewsListNum = 20;
	int size = 1;
	String TAG = "HomeActivity";
	MediaController mMediaController;
	// ��ȡHttp������
	private MyApplication application;
	
	@AbIocView(id = R.id.photo_button)
	Button photo_button;
	@AbIocView(id = R.id.video_button)
	Button video_button;
	
    // ��ʼ�����
    private void initWidget() {
    	hotDocListView = (ListView) findViewById(R.id.hot_doc_list);
    	
    	//����
    	photo_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, PhotoActivity.class);  
		           startActivity(intent);
			}
		});
    }
    // ��ʼ��������
    private void initData() {
    	application=(MyApplication) this.getApplication();
        if (hotDocListView == null)
            return;
        listItemAdapter = new HotNewsListAdapter(this);
        // ����������listview������������view��
        hotDocListView.setAdapter(listItemAdapter);
        hotDocListView.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				//�����Ƭ����Ƶ �Ŵ�򲥷�
				//TODO
//				   String url = (String) hotNewsList.get(index).u;
//				   Long doc_id = hotNewsList.get(index)._id;
//		           Intent intent = new Intent(HomeActivity.this, DocWebDetailActivity.class);  
//		           intent.putExtra("docid", doc_id);
//		           intent.putExtra("doc_url", url);
//		           startActivity(intent);

	        }
        });
      //  appendData();
        // �ڶ�����newһ����������controller��
        // ����1��Context
        // ����2��listview��item����
        // ����3�����������item�����µ��Ǹ��ؼ�id
        // ����4����������
    }
    /**
     * ˢ������
     */
    public void refresh(){
    	hotNewsList.clear();
    	loadHotTopNews();
    }
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadHotTopNews();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.layout_home);
        initWidget();
        initData();
        //Create media controller
        mMediaController = new MediaController(this);
        loadHotTopNews();
		

    }
    public void loadHotTopNews() {    	
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		String loginKey = application.getProperty("loginKey");
		Log.d(TAG, loginKey);
		//��ȡ�ֳ������� ���ֻ���������Ƭ����Ƶ�б�
		//TODO ͨ����̨��ȡ�ֳ������� ���ֻ���������Ƭ����Ƶ�б�
		String urlString = String.format("%s?user_id=0",URLs.PHOTOLIST);
		Log.d(TAG,urlString);
		mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {
			// ��ȡ���ݳɹ����������
			@Override
			public void onSuccess(int statusCode, String content) {
				Log.d(TAG, content);
				try {
					HotNewsList gList = HotNewsList
							.parseJson(content);
					if (gList.code == 200) {
						hotNewsList = gList.getInfo();
						listItemAdapter.notifyDataSetChanged();
					} else {
						showToast(gList.msg);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					showToast("���ݽ���ʧ��");
				}
			}

			// ʧ�ܣ�����
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {

				Log.d(TAG, "onFailure");
				showToast("��������ʧ�ܣ�");
			}

			// ��ʼִ��ǰ
			@Override
			public void onStart() {
				Log.d(TAG, "onStart");
				// ��ʾ���ȿ�
				showProgressDialog();
				
			}

			// ��ɺ���ã�ʧ�ܣ��ɹ�
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				// �Ƴ����ȿ�
				removeProgressDialog();

			};

		});

	}
    
    /** �Զ��������� */  
    public class HotNewsListAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;// ��̬����ӳ��  
  
        public HotNewsListAdapter(Context context) {  
            this.mInflater = LayoutInflater.from(context);  
    		// ͼƬ������
    		mAbImageDownloader = new AbImageDownloader(context);
    		mAbImageDownloader.setWidth(200);
    		mAbImageDownloader.setHeight(150);
    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
    		mAbImageDownloader.setNoImage(R.drawable.image_no);
        }  
  
        // ����ListView�м��пɼ�  
        @Override  
        public int getCount() {  
            return hotNewsList.size();// ListView����Ŀ��  
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
        	HotNews news = hotNewsList.get(position);
        	
        	TextView title = null;
        	TextView abstr = null;
        	TextView timeView = null;
			//�б�����ͼƬ
			if (news.type==1) {
	            convertView = mInflater.inflate(R.layout.listitem_hotdoclist, null);//���ݲ����ļ�ʵ����view  
	            title = (TextView) convertView
						.findViewById(R.id.itemsTitle);
	            
	            abstr = (TextView) convertView
						.findViewById(R.id.itemsAbstract);
	            timeView = (TextView) convertView
						.findViewById(R.id.time);
				ImageView imageView = (ImageView) convertView
						.findViewById(R.id.itemsImage);
				mAbImageDownloader.display(imageView,URLs.URL_API_HOST+news.url);
				
//			}else{
//				//�б�������Ƶ
//	            convertView = mInflater.inflate(R.layout.listitem_hotdoclist_noimage, null);
//	            VideoView mVideoView = (VideoView)convertView.findViewById(R.id.videoView1);
//	            Uri uri = Uri.parse(news.mov);  
//	            mVideoView.setVideoURI(uri);  
////	            mVideoView.setMediaController(mMediaController);  
//	            /** 
//	             * ��Ƶ������Ƶ����βʱ�����ķ��� 
//	             */  
//	            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {  
//	                @Override  
//	                public void onCompletion(MediaPlayer mp) {  
//	                    Log.i("֪ͨ", "���");  
//	                }  
//	            });  
//	              
//	            mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {  
//	                  
//	                @Override  
//	                public boolean onError(MediaPlayer mp, int what, int extra) {  
//	                    Log.i("֪ͨ", "�����г��ִ���");  
//	                    return false;  
//	                }  
//	            });  
//	            title = (TextView) convertView
//						.findViewById(R.id.itemsTitle);
//	            abstr = (TextView) convertView
//						.findViewById(R.id.itemsAbstract);
//				timeView = (TextView) convertView
//						.findViewById(R.id.time);
			}
			title.setText(news.user_name);
			abstr.setText(news.content);
			if (news.content==""||news.content==null) {
				abstr.setVisibility(View.GONE);
			}
			timeView.setText(news.create_time.toString());
            return convertView;  
        }

    }  
}
