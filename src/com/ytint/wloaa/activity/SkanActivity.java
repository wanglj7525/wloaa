package com.ytint.wloaa.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.MyImageView.OnMeasureListener;
import com.ytint.wloaa.app.FileHelper;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.ImageLoader;
import com.ytint.wloaa.bean.ImageLoader.OnCallBackListener;

public class SkanActivity extends AbActivity {
	private MyApplication application;
	private GridView mGridView;
	private Button finishButton;
	private ArrayList<String> lists;
	private LayoutInflater inflate;
	private int viewWidth=0,viewHeight=0;
	private MyAdapter adapter;
	private String loginKey;
	private ArrayList<String> strs=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image_activity);
		lists=(ArrayList<String>) getIntent().getExtras().get("data");
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		initView();
	}
	private void initView() {
		mGridView=(GridView) findViewById(R.id.child_grid);
		finishButton=(Button)findViewById(R.id.finishButton);
		inflate=LayoutInflater.from(SkanActivity.this);
		adapter=new MyAdapter();
		mGridView.setAdapter(adapter);
		finishButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//返回上报内容页面，上传图片
				new FileHelper().submitUploadFile(strs, loginKey,"1");
				Intent intent=new Intent();
				intent.putExtra("data", strs);
				setResult(200,intent);
				finish();
			}
		});
	}
	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		intent.putExtra("data", strs);
		setResult(200,intent);
		finish();
		super.onBackPressed();
	}
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return lists.size();
		}
		@Override
		public Object getItem(int position) {
			return lists.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final String path=lists.get(position);
			ViewHolder viewHolder=null;
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=inflate.inflate(R.layout.grid_child_item, null);
				viewHolder.image=(MyImageView) convertView.findViewById(R.id.child_image);
				viewHolder.check=(CheckBox) convertView.findViewById(R.id.child_checkbox);
				convertView.setTag(viewHolder);
				viewHolder.image.setOnMeasureListener(new OnMeasureListener() {			
					@Override
					public void onMeasureSize(int width, int height) {
						viewWidth=width;
						viewHeight=height;
					}
				});
			}else{
				viewHolder=(ViewHolder) convertView.getTag();
				viewHolder.image.setImageResource(R.drawable.friends_sends_pictures_no);
			}
			viewHolder.image.setTag(path);
			Bitmap bitmap=ImageLoader.getInstance().loadImage(path, viewWidth, viewHeight, new OnCallBackListener() {
				@Override
				public void setOnCallBackListener(Bitmap bitmap, String url) {
					ImageView image=(ImageView) mGridView.findViewWithTag(url);
					if(image!=null&&bitmap!=null){
						image.setImageBitmap(bitmap);
					}
				}
			});
			if(bitmap!=null){
				viewHolder.image.setImageBitmap(bitmap);
			}else{				
				viewHolder.image.setImageResource(R.drawable.friends_sends_pictures_no);
			}
			viewHolder.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {	
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked){
						strs.add(path);
					}else{
						if(strs.contains(path))
							strs.remove(path);
					}
				}
			});
			
			return convertView;
			
		}		
	}
	private static class ViewHolder{
		public MyImageView image;
		public CheckBox check;
	}
	
	
}
