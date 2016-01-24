package com.ytint.wloaa.activity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.bitmap.AbImageDownloader;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.FileHelper;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.Project;
import com.ytint.wloaa.bean.ProjectList;
import com.ytint.wloaa.bean.Shenpi;
import com.ytint.wloaa.bean.ShenpiInfo;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.utils.BitmapCache;
import com.ytint.wloaa.utils.BitmapCache.ImageCallback;
import com.ytint.wloaa.widget.TitleBar;

/**
 * 自定义任务
 * 
 * @author wlj
 * @date 2016-1-12下午2:41:14
 */
public class SendTaskActivity extends AbActivity {
	String TAG = "AddShenpiActivity";
	private MyApplication application;
	private ArrayAdapter<String> adapter;
	String[] people_names = new String[0];
	String[] project_names = new String[0];
	private long people = 0;
	private long project = 0;
	private List<People> peoples;
	private List<Project> projects;
	private int from;

	Context context = null;
	private String loginKey;
	private String department_id;
	private String userName;
	private String phone;
	@AbIocView(id = R.id.task_people)
	EditText task_people;
	@AbIocView(id = R.id.select_project)
	Spinner projectSpinner;
	@AbIocView(id = R.id.addshenpi_full)
	LinearLayout addxiapai_full;
	@AbIocView(id = R.id.showProgect)
	LinearLayout showProgect;
	@AbIocView(id = R.id.gridView_image_report)
	GridView gridView_image_report;
	/** 添加文件 */
	@AbIocView(id = R.id.commitShenpi)
	Button commitFile;
	/** 提交上报 */
	@AbIocView(id = R.id.reportcancel)
	Button reportcancel;
	@AbIocView(id = R.id.task_name_report)
	EditText task_name;
	@AbIocView(id = R.id.task_tell)
	EditText task_tell;
	@AbIocView(id = R.id.task_remark)
	EditText task_remark;
	/** 选择本地图片上传 */
	@AbIocView(id = R.id.add_photo)
	Button add_photo;
	/** 拍照 */
	@AbIocView(id = R.id.add_camera)
	Button add_camera;
	/** 录像 */
	@AbIocView(id = R.id.add_video)
	Button add_video;
	/** 添加录音 */
//	@AbIocView(id = R.id.addVoicereport)
//	Button addVoicereport;
	@AbIocView(id=R.id.toggleButton1)
	ToggleButton  voiceToggleButton ;
	@AbIocView(id=R.id.chronometer1)
	Chronometer  chronometer1 ;
	/** 显示录音 */
	@AbIocView(id = R.id.addvoicegridviewreport)
	GridView addvoicegridviewreport;
	@AbIocView(id = R.id.horizontalScrollView_addvoicereport)
	HorizontalScrollView horizontalScrollView_addvoicereport;
	@AbIocView(id = R.id.horizontalScrollView_report)
	HorizontalScrollView horizontalScrollView_report;
	/** 语音列表适配器 */
	private MyGridAdapter mAdapter;
	/** 语音列表 */
	private ArrayList<String> mVoicesList;
	/** 语音名称列表 */
	private List<String> mVoicesListname;
	/** 录音存储路径 */
	private static final String PATH = "/sdcard/wloaa/Record/";
	/** 用于语音播放 */
	private MediaPlayer mPlayer = null;
	/** 用于完成录音 */
	private MediaRecorder mRecorder = null;
	/** 语音文件保存路径 */
	private String mFileName = null;
	/** 语音文件显示名称 */
	private String mFileNameShow = null;
	/** 列宽 */
	private int cWidth = 500;
	/** 水平间距 */
	private int hSpacing = 10;

	private ArrayList<String> imagelist = new ArrayList<String>();
	public static ArrayList<String> attachment = new ArrayList<String>();
	public static ArrayList<String> media = new ArrayList<String>();
	public static ArrayList<String> video = new ArrayList<String>();
	private AbImageDownloader mAbImageDownloader = null;

	private String peopleId = "0";
	private String projectId = "0";
	private String companyId = "0";
	private static String srcPath;
	private static String videoPath;

	private String commitId;
	String host;
	private String path = Environment.getExternalStorageDirectory()
			+ "/wloaa/BigImage/";
	private String fileName;

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
		application = (MyApplication) this.getApplication();
		host = URLs.HTTP + application.getProperty("HOST") + ":"
				+ application.getProperty("PORT");
		Intent intent = getIntent();
		from = Integer.parseInt(intent.getExtras().get("from").toString());

		setAbContentView(R.layout.layout_addtask);
		context = SendTaskActivity.this;
		loginKey = application.getProperty("loginKey");
		department_id = application.getProperty("department_id");
		userName = application.getProperty("userName");
		phone = application.getProperty("phone");

		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		final TitleBar titleBar = (TitleBar) findViewById(R.id.title_bart);
		titleBar.setLeftImageResource(R.drawable.back_green);
		titleBar.setLeftText("返回");
		titleBar.setLeftTextColor(Color.WHITE);
		titleBar.setLeftClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		if (from == 1) {
			titleBar.setTitle("工程任务");
		} else if (from == 2) {
			titleBar.setTitle("自定义任务");
		}
		titleBar.setTitleColor(Color.WHITE);
		titleBar.setDividerColor(Color.GRAY);
		initData();
		initUi();
		// 加载联系人下拉框
		// loadPeoples();
		if (from == 1) {
			loadProject();
		}
		// loadComapny();

		commitId = "0";

		// 扫描相册 防止有新的图片，缩略图不全 目测不能立即生效
		if (Build.VERSION.SDK_INT >= 19) {// Build.VERSION_CODES.KITKAT) { //
			folderScan();
		} else {
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory())));
		}
	}

	public void folderScan() {
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_DCIM).getPath()
				+ "/Camera/");
		if (file.isDirectory()) {
			File[] array = file.listFiles();
			for (int i = 0; i < array.length; i++) {
				File f = array[i];
				if (f.isFile()) {// FILE TYPE
					String name = f.getName();
					if (name.contains(".jpg")) {
						Log.e("TAG", "file:" + f.getAbsolutePath());
						MediaScannerConnection.scanFile(this,
								new String[] { f.getAbsolutePath() }, null,
								null);
					}
				}
			}
		}
	}

	/** 初始化数据 */
	private void initData() {
		mVoicesList = new ArrayList<String>();
		mVoicesListname = new ArrayList<String>();
		mPlayer = new MediaPlayer();
		chronometer1.setBase(SystemClock.elapsedRealtime());
	}

	private void initGridView() {
		MyGridAdapter mAdapter = new MyGridAdapter(context);
		addvoicegridviewreport.setAdapter(mAdapter);
		LayoutParams params = new LayoutParams(mAdapter.getCount()
				* (cWidth + hSpacing), LayoutParams.WRAP_CONTENT);
		addvoicegridviewreport.setLayoutParams(params);
		addvoicegridviewreport.setColumnWidth(cWidth);
		addvoicegridviewreport.setHorizontalSpacing(hSpacing);
		addvoicegridviewreport.setStretchMode(GridView.NO_STRETCH);
		addvoicegridviewreport.setNumColumns(mAdapter.getCount());

		addvoicegridviewreport
				.setOnItemClickListener(new OnItemClickListener() {
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
		addvoicegridviewreport
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						mVoicesList.remove(arg2);
						mVoicesListname.remove(arg2);
						initGridView();
						return false;
					}
				});
	}

	private void initProject() {
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(SendTaskActivity.this,
				R.layout.spinner_item, project_names);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// 将adapter 添加到spinner中
		projectSpinner.setAdapter(adapter);
		// 设置默认选中
		projectSpinner.setSelection(0);
		// 设置默认值
		// channelSpinner.setVisibility(View.VISIBLE);
		projectSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
						project = projects.get(arg2).id;
						projectId = projects.get(arg2).id + "";
						TextView tv = (TextView) view;
						tv.setTextColor(getResources().getColor(R.color.black)); // 设置颜色
						tv.setGravity(android.view.Gravity.CENTER); // 设置居中

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						arg0.setVisibility(View.VISIBLE);
					}

				});

	}

	@SuppressLint("NewApi")
	private void loadProject() {

		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(host + URLs.PROJECTLIST + "?p=1&ps",
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						try {
							ProjectList cList = ProjectList.parseJson(content);
							if (cList.code == 200) {
								projects = cList.getInfo();
								application.saveObject((Serializable) projects,
										"projects");
								project_names = new String[projects.size()];
								int i = 0;
								for (Project cn : projects) {
									project_names[i] = cn.name;
									i++;
								}

								initProject();
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
		// 相册
		add_photo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SendTaskActivity.this,
						AlbumActivity.class);
				startActivityForResult(intent, 20);
			}
		});
		// 拍照
		add_camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				/**
				 * 在启动拍照之前最好先判断一下sdcard是否可用
				 */
				String state = Environment.getExternalStorageState(); // 拿到sdcard是否可用的状态码
				if (state.equals(Environment.MEDIA_MOUNTED)) { // 如果可用
					// Intent intent = new
					// Intent("android.media.action.IMAGE_CAPTURE");
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
					System.out.println(path);
					File file = new File(path);
					if (!file.exists()) {
						file.mkdirs();
					}
					fileName = String.valueOf(System.currentTimeMillis())
							+ ".png";
					Uri imageUri = Uri.fromFile(new File(path, fileName));
					intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
					startActivityForResult(intent, 12);
				} else {
					Toast.makeText(SendTaskActivity.this, "sdcard不可用",
							Toast.LENGTH_SHORT).show();
				}
				// Intent intent = new
				// Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
				// intent.putExtra("output", Uri.fromFile(tempFile));
			}
		});
		// 录像
		add_video.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
				startActivityForResult(intent, 10);
			}
		});
		// 提交上报信息 添加文件
		commitFile.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) SendTaskActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				submitShenpi();
			}
		});
		reportcancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		voiceToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isCheck) {
				if (isCheck) {
					//开始录音
					System.out.println("开始录音");
					if (mVoicesList.size() >= 1) {
						Toast.makeText(getApplicationContext(), "只能上传一个录音", 0)
								.show();
						voiceToggleButton.setChecked(false);
					} else {
						//开始计时
						chronometer1.setBase(SystemClock.elapsedRealtime());
						chronometer1.start();
						startVoice();
					}
				}else{
					//结束录音
					System.out.println("结束录音");
					chronometer1.stop();
					stopVoice();
				}
			}
		});
//		addVoicereport.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				// 点击开始录音 再点击结束录音
//			}
//		});
//		// 添加录音
//		addVoicereport.setOnTouchListener(new OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// if (commitId.equals("0")) {
//				// Toast.makeText(getApplicationContext(), "请先保存上面的信息", 0)
//				// .show();
//				// } else {
//				switch (event.getAction()) {
//				case MotionEvent.ACTION_DOWN:
//					if (mVoicesList.size() >= 1) {
//						Toast.makeText(getApplicationContext(), "只能上传一个录音", 0)
//								.show();
//					} else {
//						startVoice();
//					}
//					break;
//				case MotionEvent.ACTION_UP:
//					if (mVoicesList.size() >= 1) {
//					} else {
//						stopVoice();
//					}
//					break;
//				default:
//					break;
//				}
//				// }
//				return false;
//			}
//		});

		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) SendTaskActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		};
		addxiapai_full.setClickable(true);
		addxiapai_full.setOnClickListener(keyboard_hide);

		task_people.setText(userName);
		task_tell.setText(phone);
		if (from == 2) {
			showProgect.setVisibility(View.GONE);
			projectId = "-1";
		}
	}

	private void alert() {
		Dialog dialog = new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("您选择的不是有效的图片")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						srcPath = null;
					}
				}).create();
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 20:
				// 相册选择
				ArrayList<String> result = data.getExtras().getStringArrayList(
						"result");// 得到新Activity 关闭后返回的数据
				for (int i = 0; i < result.size(); i++) {
					File image = FileHelper.scal(result.get(i));
					if (!imagelist.contains(image.getPath())) {
						imagelist.add(image.getPath());
						Log.e(TAG, image.getPath());
					}
				}
				setImageGrideValue();
				break;
			case 10:
				// 录像
				Uri uriVideo = data.getData();
				Cursor cursors = this.getContentResolver().query(uriVideo,
						null, null, null, null);
				if (cursors.moveToNext()) {
					/* _data：文件的绝对路径 ，_display_name：文件名 */
					videoPath = cursors.getString(cursors
							.getColumnIndex("_data"));
					Toast.makeText(this, videoPath, Toast.LENGTH_SHORT).show();
					imagelist.add(videoPath);
					setImageGrideValue();
				}
				break;
			// case 11:
			// Uri uri = data.getData();
			// Log.e(TAG, "uri = " + uri);
			// try {
			// String[] pojo = { MediaStore.Images.Media.DATA };
			// Cursor cursor = managedQuery(uri, pojo, null, null, null);
			// if (cursor != null) {
			// ContentResolver cr = this.getContentResolver();
			// int colunm_index = cursor
			// .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			// cursor.moveToFirst();
			// String path = cursor.getString(colunm_index);
			// /***
			// * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，
			// * 你选择的文件就不一定是图片了， 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以
			// */
			// if (path.endsWith("jpg") || path.endsWith("png")) {
			// srcPath = path;
			// imagelist.add(srcPath);
			// Log.e(TAG, "srcPath = " + srcPath);
			// setImageGrideValue();
			// } else {
			// alert();
			// }
			// } else {
			// alert();
			// }
			//
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// break;
			case 12:
				// 相机拍照
				File image = FileHelper.scal(path + fileName);
				imagelist.add(image.getPath());
				setImageGrideValue();
				Log.e(TAG, "path + fileName = " + path + fileName);
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 提交申请
	 * 
	 * @author wlj
	 * @date 2015-6-16下午7:21:20
	 */
	private void submitShenpi() {
		// 获取Http工具类
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		mAbHttpUtil.setDebug(true);
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(context, "请检查网络连接");
			return;
		}
		if (task_name.getText().toString().trim().length() == 0) {
			UIHelper.ToastMessage(context, "请输入内容");
			return;
		}
		AbRequestParams params = new AbRequestParams();
		params.put("taskInfo.name", task_name.getText().toString());
		params.put("taskInfo.company_id", companyId);
		params.put("taskInfo.project_id", projectId);
		params.put("taskInfo.contact", task_tell.getText().toString());
		params.put("taskInfo.remark", task_remark.getText().toString());
		params.put("taskInfo.task_type", from + "");
		params.put("taskInfo.create_user_id", loginKey);
		params.put("taskInfo.department_id", department_id);
		params.put("taskInfo.status", "0");
		System.out.println(params.toString());
		Log.e(TAG, String.format("%s?%s", host + URLs.ADDSHENPI, params));
		mAbHttpUtil.post(host + URLs.ADDRS, params,
				new AbStringHttpResponseListener() {
					@Override
					public void onSuccess(int statusCode, String content) {
						Log.d(TAG, content);
						try {
							ShenpiInfo gList = ShenpiInfo.parseJson(content);
							if (gList.code == 200) {
								Shenpi shenpi = gList.getInfo();
								commitId = shenpi.id.toString();
								// 上传文件
								if (mVoicesList.size() == 0
										&& imagelist.size() == 0) {
									showToast("提交成功！");
									// Toast.makeText(getApplicationContext(),
									// "请添加文件", 0).show();
									finish();
								} else {
									// 录音
									new FileHelper().submitUploadFile(
											mVoicesList, loginKey, commitId,
											"2", host);
									// 图片 视频
									new FileHelper().submitUploadFile(
											imagelist, loginKey, commitId, "1",
											host);
									Toast.makeText(getApplicationContext(),
											"正在上传文件...", 0).show();
									finish();
								}

								// finish();
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
						// finish();
						removeProgressDialog();
					};
				});
	}

	/** 开始录音 */
	private void startVoice() {
		// 设置录音保存路径
		try {
			mFileNameShow = UUID.randomUUID().toString();
			mFileName = PATH + mFileNameShow + ".amr";
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
			mRecorder.prepare();
			mRecorder.start();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "录音失败，请重试！", 0).show();
			e.printStackTrace();
		}
	}

	/** 停止录音 */
	@SuppressLint("NewApi")
	private void stopVoice() {
		try {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
			mVoicesList.add(mFileName);
			mVoicesListname.add(mFileNameShow);
			mAdapter = new MyGridAdapter(SendTaskActivity.this);
			addvoicegridviewreport.setAdapter(mAdapter);
			initGridView();
			Toast.makeText(getApplicationContext(), "保存录音" + mFileName, 0)
					.show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "录音失败，请重试！", 0).show();
			e.printStackTrace();
		}
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
//			TextView tv = (TextView) contentView.findViewById(R.id.tv_armName);
//			tv.setText(mVoicesListname.get(position) + ".amr");
			return contentView;
		}

	}

	private void setImageGrideValue() {
		setImageListener();
		MAImagedapter mAdapter = new MAImagedapter(context);
		gridView_image_report.setAdapter(mAdapter);
		LayoutParams params = new LayoutParams(
				mAdapter.getCount() * (200 + 10), LayoutParams.WRAP_CONTENT);
		gridView_image_report.setLayoutParams(params);
		gridView_image_report.setColumnWidth(200);
		gridView_image_report.setHorizontalSpacing(10);
		gridView_image_report.setStretchMode(GridView.NO_STRETCH);
		gridView_image_report.setNumColumns(mAdapter.getCount());

	}

	private void setImageListener() {
//		gridView_image_report.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				// TODO Auto-generated method stub
//				Log.e(TAG, "position = " + position);
//				String url = imagelist.get(position);
//				// Intent intent = new Intent(AddZhiliangReportActivity.this,
//				// PicturePreviewActivity.class);
//				// intent.putExtra("url", url);
//				// startActivity(intent);
//			}
//
//		});

		gridView_image_report
				.setOnItemLongClickListener(new OnItemLongClickListener() {
					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {
						imagelist.remove(arg2);
						setImageGrideValue();
						return false;
					}
				});
	}

	class MAImagedapter extends BaseAdapter {
		BitmapCache cache;
		Context mContext;
		LayoutInflater mInflater;

		private class ViewHolder {
			public ImageView imageView;
		}

		public MAImagedapter(Context c) {
			mContext = c;
			mInflater = LayoutInflater.from(mContext);
			cache = new BitmapCache();
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

		ImageCallback callback = new ImageCallback() {
			@Override
			public void imageLoad(ImageView imageView, Bitmap bitmap,
					Object... params) {
				if (imageView != null && bitmap != null) {
					String url = (String) params[0];
					if (url != null && url.equals((String) imageView.getTag())) {
						((ImageView) imageView).setImageBitmap(bitmap);
					} else {
						Log.e(TAG, "callback, bmp not match");
					}
				} else {
					Log.e(TAG, "callback, bmp null");
				}
			}
		};

		@Override
		public View getView(int position, View contentView, ViewGroup arg2) {
			String image = imagelist.get(position);
			// if (image.contains("3gp") || image.contains("mp4")) {
			// image = host + "/public/images/video_play_btn.png";
			// }
			Log.e(TAG, image);
			ViewHolder holder;
			if (contentView == null) {
				holder = new ViewHolder();
				contentView = mInflater.inflate(R.layout.gridview_item, null);
				holder.imageView = (ImageView) contentView
						.findViewById(R.id.mImage);
				contentView.setTag(holder);
				// Bitmap bitmap = ImageLoader.getInstance().loadImage(image,
				// 200,
				// 100, new OnCallBackListener() {
				// @Override
				// public void setOnCallBackListener(Bitmap bitmap,
				// String url) {
				// ImageView image = (ImageView) gridView_image_report
				// .findViewWithTag(url);
				// if (image != null && bitmap != null) {
				// image.setImageBitmap(bitmap);
				// }
				// }
				// });
				// if (bitmap != null) {
				// holder.mImg.setImageBitmap(bitmap);
				// } else {
				// holder.mImg
				// .setImageResource(R.drawable.friends_sends_pictures_no);
				// }
				// mAbImageDownloader.display(holder.mImg,image);
			} else {
				holder = (ViewHolder) contentView.getTag();
			}
			if (image.contains("3gp") || image.contains("mp4")) {
				holder.imageView.setImageResource(R.drawable.video_play_btn);
				// }
				// if (path.contains("camera_default")) {
				// holder.imageView.setImageResource(R.drawable.friends_sends_pictures_no);
			} else {
				// ImageManager2.from(mContext).displayImage(viewHolder.imageView,
				// path, Res.getDrawableID("plugin_camera_camera_default"), 100,
				// 100);
				// final ImageItem item = dataList.get(position);
				holder.imageView.setTag(image);
				cache.displayBmp(holder.imageView, image, image, callback);
			}
			return contentView;
		}

	}
}
