package com.ytint.wloaa.popup;

import java.util.ArrayList;

import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.AlbumActivity;
import com.ytint.wloaa.adapter.FolderAdapter;
import com.ytint.wloaa.utils.BitmapCache;
import com.ytint.wloaa.utils.ImageItem;
import com.ytint.wloaa.utils.BitmapCache.ImageCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class SelectPicPopupWindow extends PopupWindow {

	// private Button btn_take_photo, btn_pick_photo, btn_cancel;
	private View mMenuView;
	private FolderAdapter folderAdapter;

	public SelectPicPopupWindow(Activity context, OnClickListener itemsOnClick) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.alert_picdialog, null);
		GridView gridView = (GridView) mMenuView
				.findViewById(R.id.fileGridView);
		folderAdapter = new FolderAdapter(context);
		gridView.setAdapter(folderAdapter);

		// btn_take_photo = (Button)
		// mMenuView.findViewById(R.id.btn_take_photo);
		// btn_pick_photo = (Button)
		// mMenuView.findViewById(R.id.btn_pick_photo);
		// btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		// 取消按钮
		// btn_cancel.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // 销毁弹出框
		// dismiss();
		// }
		// });
		// //设置按钮监听
		// btn_pick_photo.setOnClickListener(itemsOnClick);
		// btn_take_photo.setOnClickListener(itemsOnClick);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.FILL_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.AnimBottom);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

	public class FolderAdapter extends BaseAdapter {

		private Activity mContext;
		private Intent mIntent;
		private DisplayMetrics dm;
		BitmapCache cache;
		final String TAG = getClass().getSimpleName();

		public FolderAdapter(Activity c) {
			cache = new BitmapCache();
			init(c);
		}

		// 初始化
		public void init(Activity c) {
			mContext = c;
			mIntent = ((Activity) mContext).getIntent();
			dm = new DisplayMetrics();
			((Activity) mContext).getWindowManager().getDefaultDisplay()
					.getMetrics(dm);
		}

		@Override
		public int getCount() {
			return AlbumActivity.contentList.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
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

		private class ViewHolder {
			//
			public ImageView backImage;
			// 封面
			public ImageView imageView;
			public ImageView choose_back;
			// 文件夹名称
			public TextView folderName;
			// 文件夹里面的图片数量
			public TextView fileNum;
		}

		ViewHolder holder = null;

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.plugin_camera_select_folder, null);
				holder = new ViewHolder();
				holder.backImage = (ImageView) convertView
						.findViewById(R.id.file_back);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.file_image);
				holder.choose_back = (ImageView) convertView
						.findViewById(R.id.choose_back);
				holder.folderName = (TextView) convertView
						.findViewById(R.id.name);
				holder.fileNum = (TextView) convertView
						.findViewById(R.id.filenum);
				holder.imageView.setAdjustViewBounds(true);
				// LinearLayout.LayoutParams lp = new
				// LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,dipToPx(65));
				// lp.setMargins(50, 0, 50,0);
				// holder.imageView.setLayoutParams(lp);
				holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			String path;
			if (AlbumActivity.contentList.get(position).imageList != null) {

				// path = photoAbsolutePathList.get(position);
				// 封面图片路径
				path = AlbumActivity.contentList.get(position).imageList.get(0).imagePath;
				// 给folderName设置值为文件夹名称
				// holder.folderName.setText(fileNameList.get(position));
				holder.folderName.setText(AlbumActivity.contentList
						.get(position).bucketName);

				// 给fileNum设置文件夹内图片数量
				// holder.fileNum.setText("" + fileNum.get(position));
				holder.fileNum.setText(""
						+ AlbumActivity.contentList.get(position).count);

			} else
				path = "android_hybrid_camera_default";
			if (path.contains("android_hybrid_camera_default"))
				holder.imageView
						.setImageResource(R.drawable.plugin_camera_no_pictures);
			else {
				// holder.imageView.setImageBitmap(
				// AlbumActivity.contentList.get(position).imageList.get(0).getBitmap());
				final ImageItem item = AlbumActivity.contentList.get(position).imageList
						.get(0);
				holder.imageView.setTag(item.imagePath);
				cache.displayBmp(holder.imageView, item.thumbnailPath,
						item.imagePath, callback);
			}
			// 为封面添加监听
			holder.imageView.setOnClickListener(new ImageViewClickListener(
					position, mIntent, holder.choose_back));

			return convertView;
		}

		// 为每一个文件夹构建的监听器
		private class ImageViewClickListener implements OnClickListener {
			private int position;
			private Intent intent;
			private ImageView choose_back;

			public ImageViewClickListener(int position, Intent intent,
					ImageView choose_back) {
				this.position = position;
				this.intent = intent;
				this.choose_back = choose_back;
			}

			public void onClick(View v) {
				dismiss();
				AlbumActivity.dataList = (ArrayList<ImageItem>) AlbumActivity.contentList
						.get(position).imageList;
				AlbumActivity.showNewImage();
				// Intent intent = new Intent();
				// String folderName =
				// AlbumActivity.contentList.get(position).bucketName;
				// intent.putExtra("folderName", folderName);
				// intent.setClass(mContext, ShowAllPhoto.class);
				// mContext.startActivity(intent);
				// // ImageFile activity = (ImageFile) mContext;
				// // activity.finish();
				// choose_back.setVisibility(v.VISIBLE);
			}
		}

		public int dipToPx(int dip) {
			return (int) (dip * dm.density + 0.5f);
		}

	}

}
