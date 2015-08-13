//package com.ytint.wloaa.utils;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URI;
//import java.net.URL;
//
//import org.json.JSONException;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.net.Uri;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.yixuan.ProgressDialogHandle;
//import com.ytint.wloaa.R;
//
//public class DialogImage extends Dialog {
//	// 定义回调事件，用于dialog的点击事件
//	public interface OnCustomDialogListener {
//		public void back(String name);
//	}
//	private String url;
//	private ImageView zoomView;
//
//	public DialogImage(Context context, String url ) {
//		super(context);
//		this.url = url;
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.image_dialog);
//		// 设置标题
//		zoomView=(ImageView) findViewById(R.id.zoom_view);
//		zoomView.setOnClickListener(clickListener);
//		//图片资源
//		
//		/* 缩略图存储在本地的地址 */
//		final String smallPath = getIntent().getStringExtra("smallPath");
//		final int identify = getIntent().getIntExtra("indentify", -1);
//		DisplayMetrics metrics = new DisplayMetrics();
//		getWindowManager().getDefaultDisplay().getMetrics(metrics);
//		final int widthPixels = metrics.widthPixels;
//		final int heightPixels = metrics.heightPixels;
//		File bigPicFile = new File(getLocalPath(url));
//		if (bigPicFile.exists()) {/* 如果已经下载过了,直接从本地文件中读取 */
//			zoomView.setImageBitmap(zoomBitmap(
//					BitmapFactory.decodeFile(getLocalPath(url)), widthPixels,
//					heightPixels));
//		} else if (!TextUtils.isEmpty(url)) {
//			ProgressDialogHandle handle = new ProgressDialogHandle(this) {
//				Bitmap bitmap = null;
//
//				@Override
//				public void handleData() throws JSONException, IOException,
//						Exception {
//					bitmap = getBitMapFromUrl(url);
//					if (bitmap != null) {
//						savePhotoToSDCard(
//								zoomBitmap(bitmap, widthPixels, heightPixels),
//								getLocalPath(url));
//					}
//				}
//
//				@Override
//				public String initialContent() {
//					return null;
//				}
//
//				@Override
//				public void updateUI() {
//					if (bitmap != null) {
//						// recycle();
//
//						zoomView.setImageBitmap(zoomBitmap(bitmap, widthPixels,
//								heightPixels));
//					} else {
//						Toast.makeText(ctx, R.string.download_failed,
//								Toast.LENGTH_LONG).show();
//					}
//				}
//
//			};
//			if (TextUtils.isEmpty(smallPath) && identify != -1) {
//				handle.setBackground(BitmapFactory.decodeResource(
//						getResources(), identify));
//			} else {
//				handle.setBackground(BitmapFactory.decodeFile(smallPath));
//			}
//			handle.show();
//		}
//		gestureDetector = new GestureDetector(this,
//				new GestureDetector.SimpleOnGestureListener() {
//					@Override
//					public boolean onFling(MotionEvent e1, MotionEvent e2,
//							float velocityX, float velocityY) {
//						float x = e2.getX() - e1.getX();
//						if (x > 0) {
//							prePicture();
//						} else if (x < 0) {
//
//							nextPicture();
//						}
//						return true;
//					}
//				});
//	}
//	private View.OnClickListener clickListener = new View.OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			DialogImage.this.dismiss();
//		}
//	};
//}
