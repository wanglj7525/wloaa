package com.ytint.wloaa.activity;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.MyImageView.OnMeasureListener;
import com.ytint.wloaa.app.FileHelper;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.ImageBean;
import com.ytint.wloaa.bean.ImageLoader;
import com.ytint.wloaa.bean.ImageLoader.OnCallBackListener;


public class AddSelectPhotoActivity extends AbActivity{
	private ProgressDialog mProgressDialog;
	private GridView mGridView;
	private ArrayList<ImageBean> list;
	private Button paizhao;
	private Button finishBack;
	private HashMap<String, ArrayList<String>> mGruopMap = new HashMap<String, ArrayList<String>>(); 
	private static String srcPath;
	private ArrayList<String> imagepath=new ArrayList<String>();
	private MyApplication application;
	private String loginKey;
	private Handler handle=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			mProgressDialog.dismiss();
			MyGridAdapter adapter=new MyGridAdapter(getApplicationContext(),list=getImageBeans(), mGridView);
			mGridView.setAdapter(adapter);
			super.handleMessage(msg);
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_local_photo);
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		initView();
		
	}
	private void initView() {
		mGridView=(GridView) findViewById(R.id.main_grid);
		paizhao=(Button)findViewById(R.id.paizhao);
		finishBack=(Button)findViewById(R.id.finishBack);
		mProgressDialog= ProgressDialog.show(this, null, "正在加载..."); 
		mProgressDialog.show();
		getImages();
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String path=list.get(position).getFolderName();
				ArrayList<String> data=mGruopMap.get(path);
				Intent intent=new Intent(AddSelectPhotoActivity.this,SkanActivity.class);
				intent.putExtra("data",data);
				startActivityForResult(intent,2);
//				startActivity(intent);
			}
		});
		paizhao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	            startActivityForResult(it, 1);
			}
		});
		
		finishBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//完成 返回详情页
				Intent intent=new Intent();
				intent.putExtra("data", imagepath);
				setResult(200,intent);
				finish();
			}
		});
	}

	private void getImages(){
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		new Thread(){
			@Override
			public void run() {
				Uri uri=MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				Cursor cursor=getContentResolver().query(uri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "  
		                + MediaStore.Images.Media.MIME_TYPE + "=?",  
		        new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				if(cursor==null){
					return;
				} while (cursor.moveToNext()) {  
                    //获取图片的路径  
                    String path = cursor.getString(cursor  
                            .getColumnIndex(MediaStore.Images.Media.DATA));  
                      
                    //获取该图片的父路径名  
                    String parentName = new File(path).getParentFile().getName();                        
                    //根据父路径名将图片放入到mGruopMap中  
                    if(mGruopMap.containsKey(parentName)){
                    	mGruopMap.get(parentName).add(path);
                    }else{
                    	ArrayList<String> list=new ArrayList<String>();
                    	list.add(path);
                    	mGruopMap.put(parentName, list);
                    }
                }  
                //通知Handler扫描图片完成  
				cursor.close();  
                handle.sendEmptyMessage(0);  
            }  
        }.start();  
	};
	private ArrayList<ImageBean> getImageBeans(){
		Iterator<Map.Entry<String, ArrayList<String>>> it = mGruopMap.entrySet().iterator();
		ArrayList<ImageBean> list = new ArrayList<ImageBean>();
		while (it.hasNext()) {
			Map.Entry<String, ArrayList<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			ArrayList<String> value = entry.getValue();
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
			list.add(mImageBean);
		}
		return list;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		System.out.println(resultCode);
		if(resultCode==200){
			System.out.println(requestCode);
			switch(requestCode) {
            case 1:
                Bundle extras = data.getExtras();
                Bitmap b = (Bitmap) extras.get("data");
//                img.setImageBitmap(b);
                String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                String fileNmae = Environment.getExternalStorageDirectory().toString()+File.separator+"dong/image/"+name+".jpg";
                srcPath = fileNmae;
                System.out.println(srcPath+"----------保存路径1");
                File myCaptureFile =new File(fileNmae);
                try {
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        if(!myCaptureFile.getParentFile().exists()){
                            myCaptureFile.getParentFile().mkdirs();
                        }
                        BufferedOutputStream bos;
                        bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
                        b.compress(Bitmap.CompressFormat.JPEG, 80, bos);
                        bos.flush();
                        bos.close();
                        //上传图片
                        ArrayList<String> strPhoto=new ArrayList<String>();
                        strPhoto.add(srcPath);
                        new FileHelper().submitUploadFile(strPhoto, loginKey,"1");
                        imagepath.add(srcPath);
                		
                    }else{
                        Toast toast= Toast.makeText(AddSelectPhotoActivity.this, "保存失败，SD卡无效", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case 2:
				ArrayList<String> strs=(ArrayList<String>) data.getExtras().get("data");
				for (String string : strs) {
					if (!imagepath.contains(string)) {
						imagepath.add(string);
					}
				}
				System.out.println(imagepath);
			    break;
            default:
                break;
				}
			}
		super.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onBackPressed() {
		Intent intent=new Intent();
		intent.putExtra("data", imagepath);
		setResult(200,intent);
		finish();
		super.onBackPressed();
	}
	 class ViewHolder{
		public MyImageView image;
		public TextView tv1,tv2;
	}
	class MyGridAdapter extends BaseAdapter {
		private List<ImageBean> beans;
		private GridView mGridView;
		int width=0,height=0;
		LayoutInflater inflater;
		public MyGridAdapter(Context context,List<ImageBean> beans,GridView gridView) {
			this.beans = beans;
			this.mGridView=gridView;
			inflater=LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return beans.size();
		}

		@Override
		public Object getItem(int position) {
			return beans.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageBean bean=beans.get(position);
			String path=bean.getTopImagePath();
			final ViewHolder viewHolder;
			if(convertView==null){
				viewHolder=new ViewHolder();
				convertView=inflater.inflate(R.layout.item_gridview, null);
				viewHolder.image=(MyImageView) convertView.findViewById(R.id.group_image);
				viewHolder.tv1=(TextView) convertView.findViewById(R.id.group_count);
				viewHolder.tv2=(TextView) convertView.findViewById(R.id.group_title);
				viewHolder.image.setOnMeasureListener(new OnMeasureListener() {           
					@Override  
	                public void onMeasureSize(int width, int height) {  
						MyGridAdapter.this.width=width; 
						MyGridAdapter.this.height=height;
	                }  
	            });  
				convertView.setTag(viewHolder);
			}else{
				viewHolder=(ViewHolder) convertView.getTag();
				viewHolder.image.setImageResource(R.drawable.friends_sends_pictures_no);
			}	
			
			viewHolder.image.setTag(path);

			Bitmap bitmap=ImageLoader.getInstance().loadImage(path, width,height, new OnCallBackListener() {

				@Override
				public void setOnCallBackListener(Bitmap bitmap, String path) {
					ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
					if(bitmap != null && mImageView != null){
						mImageView.setImageBitmap(bitmap);
					}
				}
			});
			if(bitmap!=null){
				viewHolder.image.setImageBitmap(bitmap);
			}else{
				viewHolder.image.setImageResource(R.drawable.friends_sends_pictures_no);
			}
			viewHolder.tv1.setText(bean.getImageCounts()+"");
			viewHolder.tv2.setText(bean.getFolderName());
			return convertView;
		}
	}
	
	
}

