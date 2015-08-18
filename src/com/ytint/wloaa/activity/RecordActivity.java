package com.ytint.wloaa.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ytint.wloaa.app.FileHelper;
import com.ytint.wloaa.app.MyApplication;

public class RecordActivity extends AbActivity implements SurfaceHolder.Callback {

	private static final String TAG = "MainActivity";
	private SurfaceView mSurfaceview;
	private Button mBtnStartStop;
	private Button saveVideo;
	private Button cancelVideo;
	private boolean mStartedFlg = false;
	private MediaRecorder mRecorder;
	private SurfaceHolder mSurfaceHolder; 
	private String videoPath; 
	private String loginKey;
	private MyApplication application;
	private Camera camera;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (MyApplication) this.getApplication();
        loginKey = application.getProperty("loginKey");
        
//        requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏 
        setContentView(R.layout.layout_video_record1);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏 
//
//        // 设置横屏显示 
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); 
//
//        // 选择支持半透明模式,在有surfaceview的activity中使用。 
        getWindow().setFormat(PixelFormat.TRANSLUCENT); 

        mSurfaceview  = (SurfaceView)findViewById(R.id.surfaceview);
        mBtnStartStop = (Button)findViewById(R.id.btnStartStop);
        
        camera = Camera.open();
        saveVideo = (Button)findViewById(R.id.saveVideo);
        cancelVideo = (Button)findViewById(R.id.cancelVideo);
        cancelVideo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
        saveVideo.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View arg0) {
        		//上传图片
                ArrayList<String> strVideo=new ArrayList<String>();
                strVideo.add(videoPath);
                new FileHelper().submitUploadFile(strVideo, loginKey,"2");
        		finish();
        	}
        });
        mBtnStartStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!mStartedFlg) {
					// Start
					if (mRecorder == null) {
						mRecorder = new MediaRecorder(); // Create MediaRecorder
					}
					try {

						mRecorder.reset();
						mRecorder.setPreviewDisplay(mSurfaceHolder
	                            .getSurface());
						mRecorder
	                            .setVideoSource(MediaRecorder.VideoSource.CAMERA);
						mRecorder
	                            .setAudioSource(MediaRecorder.AudioSource.MIC);
						mRecorder
	                            .setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
						mRecorder
	                            .setVideoEncoder(MediaRecorder.VideoEncoder.H264);
						mRecorder
	                            .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//						mRecorder.setVideoSize(320, 240);
//						mRecorder.setVideoFrameRate(15);
				        
				        // Set output file path 
				        String path = getSDPath();
				        if (path != null) {
				        	
				        	File dir = new File(path + "/wloaa/video");
						    if (!dir.exists()) {
							    dir.mkdir();
						    }
						    videoPath = dir + "/" + getDate() + ".3gp";
					        mRecorder.setOutputFile(videoPath);
					        mRecorder.prepare();
					        mRecorder.start();   // Recording is now started
					        mStartedFlg = true;
					        mBtnStartStop.setText("停止");
					        Log.d(TAG, "Start recording ...");
					        Log.e(TAG, videoPath);
				        }
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					// stop
					if (mStartedFlg) {
						try {
							Log.d(TAG, "Stop recording ...");
							Log.d(TAG, "bf mRecorder.stop(");
							mRecorder.stop();
							Log.d(TAG, "af mRecorder.stop(");
					        mRecorder.reset();   // You can reuse the object by going back to setAudioSource() step
					        mBtnStartStop.setText("开始");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					mStartedFlg = false; // Set button status flag
				}
			}
        	
        });
        
        SurfaceHolder holder = mSurfaceview.getHolder();// 取得holder 

        holder.addCallback(this); // holder加入回调接口 

        // setType必须设置，要不出错. 
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 

    }
	private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
	    Camera.Size result=null;

	    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
	        if (size.width<=width && size.height<=height) {
	            if (result==null) {
	                result=size;
	            } else {
	                int resultArea=result.width*result.height;
	                int newArea=size.width*size.height;

	                if (newArea>resultArea) {
	                    result=size;
	                }
	            }
	        }
	    }
	    return(result);
	}  
	/**  
     * 获取系统时间  
     * @return  
     */  
 	public static String getDate(){
 		Calendar ca = Calendar.getInstance();   
 		int year = ca.get(Calendar.YEAR);			// 获取年份   
 		int month = ca.get(Calendar.MONTH);			// 获取月份    
 		int day = ca.get(Calendar.DATE);			// 获取日   
 		int minute = ca.get(Calendar.MINUTE);		// 分    
 		int hour = ca.get(Calendar.HOUR);			// 小时    
 		int second = ca.get(Calendar.SECOND);		// 秒   
     
 		String date = "" + year + (month + 1 )+ day + hour + minute + second;
 		Log.d(TAG, "date:" + date);
 		
        return date;         
    }

 	/**  
     * 获取SD path  
     * @return  
     */
 	public String getSDPath(){ 
 		File sdDir = null; 
 		boolean sdCardExist = Environment.getExternalStorageState() 
 				.equals(android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在 
 		if (sdCardExist) 
 		{ 
 			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录 
 			return sdDir.toString(); 
 		}
 		
 		return null;
 	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder 
        mSurfaceHolder = holder; 
        Log.d(TAG, "surfaceChanged 1");
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// 将holder，这个holder为开始在onCreate里面取得的holder，将它赋给mSurfaceHolder 
        mSurfaceHolder = holder;
        Log.d(TAG, "surfaceChanged 2");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		// surfaceDestroyed的时候同时对象设置为null 
        mSurfaceview = null; 
        mSurfaceHolder = null; 
        if (mRecorder != null) {
    		mRecorder.release(); // Now the object cannot be reused
    		mRecorder = null;
    		Log.d(TAG, "surfaceDestroyed release mRecorder");
    	}
	}
}