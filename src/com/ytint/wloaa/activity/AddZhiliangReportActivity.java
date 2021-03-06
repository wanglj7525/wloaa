﻿package com.ytint.wloaa.activity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.activity.ShenpiDetailActivity.ViewHolder;
import com.ytint.wloaa.app.FileHelper;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.Company;
import com.ytint.wloaa.bean.CompanyList;
import com.ytint.wloaa.bean.ImageLoader;
import com.ytint.wloaa.bean.ImageLoader.OnCallBackListener;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.PeopleList;
import com.ytint.wloaa.bean.ShenpiInfo;
import com.ytint.wloaa.bean.URLs;

public class AddZhiliangReportActivity extends AbActivity {
	String TAG = "AddShenpiActivity";
	private MyApplication application;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	String[] company_names = new String[0];
	private long people = 0;
	private long company = 0;
	private List<People> peoples;
	private List<Company> companys;
	private int from;
	
	Context context = null;
	private String loginKey;
	private String userName;
	@AbIocView(id = R.id.task_people)
	EditText task_people;
	@AbIocView(id = R.id.select_people_report)
	Spinner peopleSpinner;
	@AbIocView(id = R.id.select_company_report)
	Spinner companySpinner;
	@AbIocView(id = R.id.addshenpi_full)
	ScrollView addxiapai_full;
	@AbIocView(id=R.id.gridView_image_report)
	GridView gridView_image_report;
//	@AbIocView(id = R.id.select_shenpi_people)
//	Spinner peopleSpinner;
	/**提交上报**/
	@AbIocView(id = R.id.commitShenpi)
	Button add;
	@AbIocView(id = R.id.task_name_report)
	EditText task_name;
	@AbIocView(id = R.id.task_tell)
	EditText task_tell;
	@AbIocView(id = R.id.task_method)
	EditText task_method;
	@AbIocView(id=R.id.is_reply)
	CheckBox is_reply;
	@AbIocView(id=R.id.is_review)
	CheckBox is_review;
	/**	确定位置*/
	@AbIocView(id = R.id.findlocal)
	Button findlocal;
	/**显示当前位置*/
	@AbIocView(id = R.id.showlocal)
	TextView showlocal;
	/**拍照，选择本地图片上传*/
	@AbIocView(id = R.id.add_photo)
	Button add_photo;
	/**添加录音*/
	@AbIocView(id = R.id.addVoicereport)
	Button addVoicereport;
	/**显示录音*/
	@AbIocView(id = R.id.addvoicegridviewreport)
	GridView addvoicegridviewreport;
	@AbIocView(id=R.id.horizontalScrollView_addvoicereport)
	HorizontalScrollView horizontalScrollView_addvoicereport;
	@AbIocView(id=R.id.horizontalScrollView_report)
	HorizontalScrollView horizontalScrollView_report;
	/** 语音列表适配器 */
	private MyGridAdapter mAdapter;
	/** 语音列表 */
	private ArrayList<String> mVoicesList;
	/** 语音名称列表 */
	private List<String> mVoicesListname;
	/** 录音存储路径 */
	private static final String PATH = "/sdcard/MyVoiceForder/Record/";
	/** 用于语音播放 */
	private MediaPlayer mPlayer = null;
	/** 用于完成录音 */
	private MediaRecorder mRecorder = null;
	/** 语音文件保存路径 */
	private String mFileName = null;
	/** 语音文件显示名称 */
	private String mFileNameShow = null;
	/**列宽*/
	private int cWidth = 500;
	/**水平间距*/
	private int hSpacing = 10;

	private ArrayList<String> imagelist=new ArrayList<String>();
	private AbImageDownloader mAbImageDownloader = null;
	
	private String peopleId;
	private String companyId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());
		AbTitleBar mAbTitleBar = this.getTitleBar();
		if (from==1) {
			mAbTitleBar.setTitleText("质量检查-上报质检任务");
		}else if(from==2){
			mAbTitleBar.setTitleText("安全检查-上报安全隐患");
		}else{
			mAbTitleBar.setTitleText("执法管理-上报执法任务");
		}
		mAbTitleBar.setLogo(R.drawable.button_selector_back); 
		 mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		 mAbTitleBar.setTitleBarBackground(R.drawable.abg_top); 
		 mAbTitleBar.setLogoLine(R.drawable.aline);
		 mAbTitleBar.getLogoView().setOnClickListener(new View.OnClickListener() {
		     @Override
		     public void onClick(View v) {
		        finish();
		     }

		 }); 
		 
		setAbContentView(R.layout.layout_addzhiliangreport);
		context = AddZhiliangReportActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		userName = application.getProperty("userName");
		initData(); 
		initUi();
		//加载联系人下拉框
		loadPeoples();
		loadComapny();
	}
	private void initSpinner() {
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(AddZhiliangReportActivity.this,
				R.layout.spinner_item, people_names);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// 将adapter 添加到spinner中
		peopleSpinner.setAdapter(adapter);
		// 设置默认选中
		peopleSpinner.setSelection(0);
		// 设置默认值
		// channelSpinner.setVisibility(View.VISIBLE);
		peopleSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						people = peoples.get(arg2).id;
						peopleId=peoples.get(arg2).id+"";
						TextView tv = (TextView) view;
						tv.setTextColor(getResources().getColor(R.color.white)); // 设置颜色
						tv.setGravity(android.view.Gravity.CENTER); // 设置居中

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						arg0.setVisibility(View.VISIBLE);
					}

				});

	}
	private void initCompany() {
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(AddZhiliangReportActivity.this,
				R.layout.spinner_item, company_names);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// 将adapter 添加到spinner中
		companySpinner.setAdapter(adapter);
		// 设置默认选中
		companySpinner.setSelection(0);
		// 设置默认值
		// channelSpinner.setVisibility(View.VISIBLE);
		companySpinner
		.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View view,
					int arg2, long arg3) {
				company = companys.get(arg2).id;
				companyId=companys.get(arg2).id+"";
						TextView tv = (TextView) view;
						tv.setTextColor(getResources().getColor(R.color.white)); // 设置颜色
						tv.setGravity(android.view.Gravity.CENTER); // 设置居中

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						arg0.setVisibility(View.VISIBLE);
					}

				});

	}
	/** 初始化数据 */
	private void initData() {
		mVoicesList = new ArrayList<String>();
		mVoicesListname = new ArrayList<String>();
		mPlayer = new MediaPlayer();
	}
	private void initGridView(){
		MyGridAdapter mAdapter = new MyGridAdapter(context);
		addvoicegridviewreport.setAdapter(mAdapter);
		LayoutParams params = new LayoutParams(mAdapter.getCount()
				* (cWidth + hSpacing), LayoutParams.WRAP_CONTENT);
		addvoicegridviewreport.setLayoutParams(params);
		addvoicegridviewreport.setColumnWidth(cWidth);
		addvoicegridviewreport.setHorizontalSpacing(hSpacing);
		addvoicegridviewreport.setStretchMode(GridView.NO_STRETCH);
		addvoicegridviewreport.setNumColumns(mAdapter.getCount());

		addvoicegridviewreport.setOnItemClickListener(new OnItemClickListener() {
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
		addvoicegridviewreport.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mVoicesList.remove(arg2);
				mVoicesListname.remove(arg2);
				initGridView();
				return false;
			}
		});
	}
	@SuppressLint("NewApi")
	private void loadPeoples() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(URLs.USERLIST+"?user_id="+loginKey+"&department_id="+from+"&type=1" ,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						try {
							PeopleList cList = PeopleList.parseJson(content);
							if (cList.code == 200) {
								peoples = cList.getInfo();
								application.saveObject((Serializable) peoples,"peoples");
								people_names = new String[peoples.size()];
								int i = 0;
								for (People cn : peoples) {
									people_names[i] = cn.name;
									i++;
								}
								initSpinner();
							} else {
								showToast(cList.msg);
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
	private void loadComapny() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(URLs.COMPANYLIST+"?p=1&ps=2" ,
				new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					CompanyList cList = CompanyList.parseJson(content);
					if (cList.code == 200) {
						companys = cList.getInfo();
						application.saveObject((Serializable) companys,"companys");
						company_names = new String[companys.size()];
						int i = 0;
						for (Company cn : companys) {
							company_names[i] = cn.name;
							i++;
						}
						
						initCompany();
					} else {
						showToast(cList.msg);
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

	private void initUi() {
		horizontalScrollView_addvoicereport.setHorizontalScrollBarEnabled(true);
		horizontalScrollView_report.setHorizontalScrollBarEnabled(true);
		initGridView();
		setImageGrideValue();
		//拍照
    	add_photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AddZhiliangReportActivity.this, AddSelectPhotoActivity.class);  
		        startActivityForResult(intent, 11);
			}
		});
		//确定当前位置
		findlocal.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
			}
		});
		//添加上报
		add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) AddZhiliangReportActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				
				submitShenpi();
			}
		});
		//添加录音
		addVoicereport.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (mVoicesList.size()>=1) {
						Toast.makeText(getApplicationContext(), "只能上传一个录音", 0).show();
					}else{
						startVoice();
					}
					break;
				case MotionEvent.ACTION_UP:
					if (mVoicesList.size()>=1) {
					}else{
						stopVoice();
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) AddZhiliangReportActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		};
		addxiapai_full.setClickable(true);
		addxiapai_full.setOnClickListener(keyboard_hide);
		
		task_people.setText(userName);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode==200){	
			switch(requestCode) {
            case 11:
				ArrayList<String> strs=(ArrayList<String>) data.getExtras().get("data");
				imagelist=strs;
				setImageGrideValue();
			    break;
            default:
                break;
				}
			}
		super.onActivityResult(requestCode, resultCode, data);
	}
	/**
	 * 提交申请
	 * @author wlj
	 * @date 2015-6-16下午7:21:20
	 */
	private void submitShenpi(){
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		
		AbRequestParams params = new AbRequestParams();
		params.put("taskInfo.name", task_name.getText().toString());
		System.out.println(peopleSpinner.getSelectedItem().toString());
		params.put("taskInfo.receive_user_id",peopleId);
		params.put("taskInfo.company_id", companyId);
		params.put("taskInfo.contact", task_tell.getText().toString());
		params.put("taskInfo.handle_mode", task_method.getText().toString());
		params.put("taskInfo.remark", loginKey);
		System.out.println();
		String reply="2";
		if (is_reply.isChecked()) {
			reply="1";
		}
		String review="2";
		if (is_review.isChecked()) {
			review="1";
		}
		params.put("taskInfo.is_reply",reply);
		params.put("taskInfo.is_review",review);
		String attachment=FileHelper.addImageReport;
		params.put("taskInfo.attachment", attachment);
		String media=FileHelper.addVoiceReport;
		params.put("taskInfo.media", media);
		params.put("taskInfo.task_type", "1");
		params.put("taskInfo.create_user_id", loginKey);
		params.put("taskInfo.department_id", from+"");
		params.put("taskInfo.status", "0");
		System.out.println(params.toString());
		Log.e(TAG, String.format("%s?%s", URLs.ADDSHENPI,
				params));
		mAbHttpUtil.post(URLs.ADDRS ,params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							ShenpiInfo gList = ShenpiInfo
									.parseJson(content);
							if (gList.code == 200) {
								showToast("申请提交成功！");
								finish();
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
						finish();
					};
				});
	}
	/** 开始录音 */
	private void startVoice() {
		// 设置录音保存路径
		mFileNameShow=UUID.randomUUID().toString();
		mFileName = PATH +mFileNameShow + ".amr";
		String state = android.os.Environment.getExternalStorageState();
		if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
			Log.i(TAG, "SD Card is not mounted,It is  " + state + ".");
		}
		File directory = new File(mFileName).getParentFile();
		if (!directory.exists() && !directory.mkdirs()) {
			Log.i(TAG, "Path to file could not be created");
		}
		Toast.makeText(getApplicationContext(), "开始录音", 0).show();
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}
		mRecorder.start();
	}

	/** 停止录音 */
	@SuppressLint("NewApi")
	private void stopVoice() {
		mRecorder.stop();
		mRecorder.release();
		mRecorder = null;
		mVoicesList.add(mFileName);
		mVoicesListname.add(mFileNameShow);
		mAdapter = new MyGridAdapter(AddZhiliangReportActivity.this);
		addvoicegridviewreport.setAdapter(mAdapter);
		initGridView();
		new FileHelper().submitUploadFile(mVoicesList, loginKey,"3");
		Toast.makeText(getApplicationContext(), "保存录音" + mFileName, 0).show();
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
			TextView tv = (TextView) contentView.findViewById(R.id.tv_armName);
			tv.setText(mVoicesListname.get(position)+ ".amr");
			return contentView;
		}

	}
    private void setImageGrideValue() {
    	setImageListener();
        MAImagedapter mAdapter = new MAImagedapter(context);
        gridView_image_report.setAdapter(mAdapter);
        LayoutParams params = new LayoutParams(mAdapter.getCount() * (200 + 10),
                LayoutParams.WRAP_CONTENT);
        gridView_image_report.setLayoutParams(params);
        gridView_image_report.setColumnWidth(200);
        gridView_image_report.setHorizontalSpacing(10);
        gridView_image_report.setStretchMode(GridView.NO_STRETCH);
        gridView_image_report.setNumColumns(mAdapter.getCount());
        
    }

    private void setImageListener() {
    	gridView_image_report.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // TODO Auto-generated method stub
                Log.e(TAG, "position = " + position);
                String url=imagelist.get(position);
//                Intent intent = new Intent(AddZhiliangReportActivity.this, PicturePreviewActivity.class);  
//        		intent.putExtra("url", url);
//        		startActivity(intent);
            }
        });
    }

    class MAImagedapter extends BaseAdapter {
    	 Context mContext;
         LayoutInflater mInflater;

         public MAImagedapter(Context c) {
             mContext = c;
             mInflater = LayoutInflater.from(mContext);
//    		// 图片下载器
//    		mAbImageDownloader = new AbImageDownloader(mContext);
//    		mAbImageDownloader.setWidth(120);
//    		mAbImageDownloader.setHeight(100);
//    		mAbImageDownloader.setType(AbConstant.SCALEIMG);
//    		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
//    		mAbImageDownloader.setErrorImage(R.drawable.image_error);
//    		mAbImageDownloader.setNoImage(R.drawable.image_no);
        }


		@Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imagelist.size();
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
        	String image=imagelist.get(position);
            ViewHolder holder;
            if (contentView == null) {
                holder = new ViewHolder();
                contentView = mInflater.inflate(R.layout.gridview_item, null);
                holder.mImg = (ImageView) contentView.findViewById(R.id.mImage);
                Bitmap bitmap=ImageLoader.getInstance().loadImage(image, 200, 100, new OnCallBackListener() {
    				@Override
    				public void setOnCallBackListener(Bitmap bitmap, String url) {
    					ImageView image=(ImageView) gridView_image_report.findViewWithTag(url);
    					if(image!=null&&bitmap!=null){
    						image.setImageBitmap(bitmap);
    					}
    				}
    			});
    			if(bitmap!=null){
    				holder.mImg.setImageBitmap(bitmap);
    			}else{				
    				holder.mImg.setImageResource(R.drawable.friends_sends_pictures_no);
    			}
//                mAbImageDownloader.display(holder.mImg,image);
            } else {
                holder = (ViewHolder) contentView.getTag();
            }
            contentView.setTag(holder);
            return contentView;
        }

    }
}