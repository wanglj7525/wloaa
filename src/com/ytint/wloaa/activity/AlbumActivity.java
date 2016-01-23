package com.ytint.wloaa.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.adapter.AlbumGridViewAdapter;
import com.ytint.wloaa.popup.SelectPicPopupWindow;
import com.ytint.wloaa.utils.AlbumHelper;
import com.ytint.wloaa.utils.Bimp;
import com.ytint.wloaa.utils.ImageBucket;
import com.ytint.wloaa.utils.ImageItem;
import com.ytint.wloaa.utils.PublicWay;

/**
 * 这个是进入相册显示所有图片的界面
 * 
 * @author king
 * @QQ:595163260
 * @version 2014年10月18日 下午11:47:15
 */
public class AlbumActivity extends AbActivity {
	// 显示手机里的所有图片的列表控件
	public static GridView gridView;
	// 当手机里没有图片时，提示用户没有图片的控件
	private TextView tv;
	// gridView的adapter
	public static AlbumGridViewAdapter gridImageAdapter;
	// 完成按钮
	private static Button okButton;
	// 相册按钮
	// private Button back;
	private Button back;
	// 取消按钮
	private Button cancel;
	private Intent intent;
	// 预览按钮
	// private Button preview;
	public static Context mContext;
	public static ArrayList<ImageItem> dataList = new ArrayList<ImageItem>();;
	private AlbumHelper helper;
	public static List<ImageBucket> contentList;
	public static Bitmap bitmap;

	// 自定义的弹出框类
	SelectPicPopupWindow menuWindow;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plugin_camera_album);
		PublicWay.activityList.add(this);
		mContext = this;
		AbTitleBar mAbTitleBar = this.getTitleBar();
		// 注册一个广播，这个广播主要是用于在GalleryActivity进行预览时，防止当所有图片都删除完后，再回到该页面时被取消选中的图片仍处于选中状态
		IntentFilter filter = new IntentFilter("data.broadcast.action");
		registerReceiver(broadcastReceiver, filter);

//		if (Build.VERSION.SDK_INT >= 19) {// Build.VERSION_CODES.KITKAT) { //
//			folderScan();
//		} else {
//			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
//					Uri.parse("file://"
//							+ Environment.getExternalStorageDirectory())));
//		}

		bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.plugin_camera_no_pictures);
		init();
		initListener();
		// 这个函数主要用来控制预览和完成按钮的状态
		isShowOkBt();
	}

//	public void folderScan() {
//		File file = new File(Environment
//						.getExternalStoragePublicDirectory(
//								Environment.DIRECTORY_DCIM).getPath()
//						+ "/Camera/");
//		if (file.isDirectory()) {
//			File[] array = file.listFiles();
//			for (int i = 0; i < array.length; i++) {
//				File f = array[i];
//				if (f.isFile()) {// FILE TYPE
//					String name = f.getName();
//					if (name.contains(".jpg")) {
//						Log.e("TAG", "file:" + f.getAbsolutePath());
//						MediaScannerConnection
//						.scanFile(this, new String[] { f.getAbsolutePath() }, null, null);
//					}
//				}
//			}
//		}
//	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// mContext.unregisterReceiver(this);
			// TODO Auto-generated method stub
			gridImageAdapter.notifyDataSetChanged();
		}
	};

	public static void showNewImage() {
		gridImageAdapter = new AlbumGridViewAdapter(mContext, dataList,
				Bimp.tempSelectBitmap);
		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked, Button chooseBt) {
						if (isChecked) {
							chooseBt.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText("完成" + "("
									+ Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
						} else {
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							chooseBt.setVisibility(View.GONE);
							okButton.setText("完成" + "("
									+ Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
						}
						isShowOkBt();
					}
				});

		gridView.setAdapter(gridImageAdapter);
		// gridImageAdapter.notifyDataSetChanged();
	}

	// // 预览按钮的监听
	// private class PreviewListener implements OnClickListener {
	// public void onClick(View v) {
	// if (Bimp.tempSelectBitmap.size() > 0) {
	// intent.putExtra("position", "1");
	// intent.setClass(AlbumActivity.this, GalleryActivity.class);
	// startActivity(intent);
	// }
	// }
	//
	// }

	// 完成按钮的监听
	private class AlbumSendListener implements OnClickListener {
		public void onClick(View v) {
			// overridePendingTransition(R.anim.activity_translate_in,
			// R.anim.activity_translate_out);
			// intent.setClass(mContext, AddZhiliangReportActivity.class);
			// startActivity(intent);
			// finish();
			// 数据是使用Intent返回
			Intent intent = new Intent();
			ArrayList<String> datas = new ArrayList<String>();
			for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
				System.out.println(Bimp.tempSelectBitmap.get(i).imagePath);
				datas.add(Bimp.tempSelectBitmap.get(i).imagePath);
			}
			// 把返回数据存入Intent
			intent.putExtra("result", datas);
			// 设置返回数据
			AlbumActivity.this.setResult(RESULT_OK, intent);
			Bimp.tempSelectBitmap.clear();
			// 关闭Activity
			AlbumActivity.this.finish();
		}

	}

	// 返回按钮监听
	// 为弹出窗口实现监听类
	private OnClickListener ImageViewClickListener = new OnClickListener() {
		public void onClick(View v) {
			menuWindow.dismiss();
			// switch (v.getId()) {
			// case R.id.btn_take_photo:
			// break;
			// case R.id.btn_pick_photo:
			// break;
			// default:
			// break;
			// }
		}
	};

	// 取消按钮的监听
	private class CancelListener implements OnClickListener {
		public void onClick(View v) {
			Bimp.tempSelectBitmap.clear();
			AlbumActivity.this.finish();
		}
	}

	// 初始化，给一些对象赋值
	private void init() {
		helper = AlbumHelper.getHelper();
		helper.init(getApplicationContext());

		contentList = helper.getImagesBucketList(false);
		dataList = new ArrayList<ImageItem>();
		for (int i = 0; i < contentList.size(); i++) {
			dataList.addAll(contentList.get(i).imageList);
		}

		cancel = (Button) findViewById(R.id.cancel);
		cancel.setOnClickListener(new CancelListener());
		back = (Button) findViewById(R.id.select);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// 实例化SelectPicPopupWindow
				menuWindow = new SelectPicPopupWindow(AlbumActivity.this,
						ImageViewClickListener);
				// 显示窗口
				menuWindow.showAtLocation(
						AlbumActivity.this.findViewById(R.id.albummain),
						Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			}
		});
		// preview = (Button) findViewById(R.id.preview);
		// preview.setOnClickListener(new PreviewListener());
		intent = getIntent();
		Bundle bundle = intent.getExtras();
		gridView = (GridView) findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList,
				Bimp.tempSelectBitmap);
		gridView.setAdapter(gridImageAdapter);
		tv = (TextView) findViewById(R.id.myText);
		gridView.setEmptyView(tv);
		okButton = (Button) findViewById(R.id.ok_button);
		okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
	}

	private void initListener() {

		gridImageAdapter
				.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {

					@Override
					public void onItemClick(final ToggleButton toggleButton,
							int position, boolean isChecked, Button chooseBt) {
						// if (Bimp.tempSelectBitmap.size() >= PublicWay.num) {
						// toggleButton.setChecked(false);
						// chooseBt.setVisibility(View.GONE);
						// if (!removeOneData(dataList.get(position))) {
						// Toast.makeText(AlbumActivity.this,
						// R.string.only_choose_num,
						// 200).show();
						// }
						// return;
						// }
						if (isChecked) {
							chooseBt.setVisibility(View.VISIBLE);
							Bimp.tempSelectBitmap.add(dataList.get(position));
							okButton.setText("完成" + "("
									+ Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
						} else {
							Bimp.tempSelectBitmap.remove(dataList.get(position));
							chooseBt.setVisibility(View.GONE);
							okButton.setText("完成" + "("
									+ Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
						}
						isShowOkBt();
					}
				});

		okButton.setOnClickListener(new AlbumSendListener());

	}

	private boolean removeOneData(ImageItem imageItem) {
		if (Bimp.tempSelectBitmap.contains(imageItem)) {
			Bimp.tempSelectBitmap.remove(imageItem);
			okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
			return true;
		}
		return false;
	}

	public static void isShowOkBt() {
		if (Bimp.tempSelectBitmap.size() > 0) {
			okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
			// preview.setPressed(true);
			okButton.setPressed(true);
			// preview.setClickable(true);
			okButton.setClickable(true);
			okButton.setTextColor(Color.WHITE);
			// preview.setTextColor(Color.WHITE);
		} else {
			okButton.setText("完成" + "(" + Bimp.tempSelectBitmap.size() + ")");// "/"+PublicWay.num+
			// preview.setPressed(false);
			// preview.setClickable(false);
			okButton.setPressed(false);
			okButton.setClickable(false);
			okButton.setTextColor(Color.parseColor("#E1E0DE"));
			// preview.setTextColor(Color.parseColor("#E1E0DE"));
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// intent.setClass(AlbumActivity.this, ImageFile.class);
			// startActivity(intent);
			Bimp.tempSelectBitmap.clear();
			AlbumActivity.this.finish();
		}
		return false;

	}

	@Override
	protected void onRestart() {
		isShowOkBt();
		super.onRestart();
	}
}
