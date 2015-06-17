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
 * ����
 * �б���ʾ���еĸ��ֻ��Ż��û�����������¼�
 * ������Բ鿴��ϸ ���Բ����������� ���߲�������ֻ�ܲ鿴
 * �����Լ����������� �����󱨸��ϼ�
 * @author wlj
 * @date 2015-6-13����11:14:05
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
	
	//��ʼ�������� ��ȡ���û���������������б�
	private void getGroupData() {
		
		// ��ȡHttp������
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "������������");
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
							UIHelper.ToastMessage(context, "���ݽ���ʧ��");
						}
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						UIHelper.ToastMessage(context, "��������ʧ�ܣ�");
					}

					@Override
					public void onStart() {
						showProgressDialog(null);
					}

					// ��ɺ����
					@Override
					public void onFinish() {
						mProgressDialog.dismiss();
					};
				});

	}
	
	/**
	 * ��ʼ��Ҫ�õ���Ԫ��
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
	// ��ʼ��������
    private void initData() {
    	application=(MyApplication) this.getApplication();
        if (shenpiListView == null)
            return;
        listItemAdapter = new ShenpiListAdapter(this);
        // ����������listview������������view��
        shenpiListView.setAdapter(listItemAdapter);
        shenpiListView.setOnItemClickListener(new ListView.OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				//������� �������� ����ҳ
				   Integer shenpi_id = shenpiList.get(index).id;
		           Intent intent = new Intent(ShenpiActivity.this, ShenpiDetailActivity.class);  
		           intent.putExtra("shenpi_id", shenpi_id);
		           System.out.println(intent.getIntExtra("shenpi_id", 0));
		           startActivity(intent);

	        }
        });
    }

	public void showProgressDialog(String message) {
		// ����һ����ʾ���ȵ�Dialog
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
	
    /** �Զ��������� */  
    public class ShenpiListAdapter extends BaseAdapter {  
        private LayoutInflater mInflater;// ��̬����ӳ��  
  
        public ShenpiListAdapter(Context context) {  
            this.mInflater = LayoutInflater.from(context);  
    		// ͼƬ������
    		mAbImageDownloader = new AbImageDownloader(context);
    		mAbImageDownloader.setWidth(80);
    		mAbImageDownloader.setHeight(60);
    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
    		mAbImageDownloader.setNoImage(R.drawable.image_no);
        }  
  
        // ����ListView�м��пɼ�  
        @Override  
        public int getCount() {  
            return shenpiList.size();// ListView����Ŀ��  
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
//			//�б�����ͼƬ
//			if (news.pic!=null&&news.pic!=""&&news.pic.split(",").length > 0) {
//	            convertView = mInflater.inflate(R.layout.listitem_shenpilist, null);//���ݲ����ļ�ʵ����view  
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
//				//�б�������Ƶ
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
//			frompeo.setText("�����ˣ�"+news.frompeo);
//			topeo.setText(news.topeo);
//			abstr.setText(news.c);
//			timeView.setText(news.pt.toString());
//            return convertView;  
            //�б�������Ƶ
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
            frompeo.setText("�����ˣ�"+news.apply_user_name);
            topeo.setText(news.first_verify_user_name);
            abstr.setText(news.content);
            timeView.setText("����ʱ�䣺"+news.create_time.toString());
            return convertView;  
        }

    }  

}
