package com.ytint.wloaa.activity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.view.ioc.AbIocView;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.URLs;

/**
 * �����ϴ��͵���ϵͳ����
 * @author Administrator
 *
 */
public class PhotoActivity  extends AbActivity implements OnClickListener{
	private MyApplication application;
	String TAG = "PhotoActivity";
    private ImageView img;
    private EditText img_content;
    private Button nati;
    private Button pai;
    private Button submit;
    
    @AbIocView(id = R.id.photo_full)
	LinearLayout photo_full;
    
    private static String srcPath;
    private static final int TIME_OUT = 10*1000;   //��ʱʱ��
    private static final String CHARSET = "utf-8"; //���ñ���
    private String loginKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aa);
        application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
        initView();
    }

    private void initView() {
        img = (ImageView) findViewById(R.id.img);
        nati = (Button) findViewById(R.id.natives);
        pai = (Button) findViewById(R.id.pai);
        submit = (Button) findViewById(R.id.submit);
        img_content=(EditText)findViewById(R.id.img_content);
        nati.setOnClickListener(this);
        pai.setOnClickListener(this);
        submit.setOnClickListener(this);
        
    	OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) PhotoActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		photo_full.setClickable(true);
		photo_full.setOnClickListener(keyboard_hide);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.natives:
                Intent local = new Intent();
                local.setType("image/*");
                local.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(local, 2);
                break;
            case R.id.pai:
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, 1);
                break;
            case R.id.submit:
            	if (srcPath == null || srcPath=="") {
            		showToast("�ļ�������");
            	}else{
            		submitUploadFile();
            	}
            	break;
        }
    }

    /**
     * �����ϴ�
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch(requestCode) {
                case 1:
                    Bundle extras = data.getExtras();
                    Bitmap b = (Bitmap) extras.get("data");
                    img.setImageBitmap(b);
                    String name = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
                    String fileNmae = Environment.getExternalStorageDirectory().toString()+File.separator+"dong/image/"+name+".jpg";
                    srcPath = fileNmae;
                    System.out.println(srcPath+"----------����·��1");
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
                        }else{

                            Toast toast= Toast.makeText(PhotoActivity.this, "����ʧ�ܣ�SD����Ч", Toast.LENGTH_SHORT);
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
                    Uri uri = data.getData();
                    img.setImageURI(uri);
                    ContentResolver cr = this.getContentResolver();
                    Cursor c = cr.query(uri, null, null, null, null);
                    c.moveToFirst();
                    //���ǻ�ȡ��ͼƬ������sdcard�е�λ��
                    srcPath = c.getString(c.getColumnIndex("_data"));
                    System.out.println(srcPath+"----------����·��2");
                    break;
                default:
                    break;
            };
        }
//    	n =1;
    }


    private void submitUploadFile(){
    	final File file=new File(srcPath);
    	final String RequestURL=URLs.UPLOADPHOTO;
    	if (file == null || (!file.exists())) {
    		return;
    	}

		Log.i(TAG, "�����URL=" + RequestURL);
		Log.i(TAG, "�����fileName=" + file.getName());
		final Map<String, String> params = new HashMap<String, String>();  
        params.put("user_id", loginKey);  
        params.put("file_type", "1");  
        params.put("content", img_content.getText().toString());  
        showProgressDialog();
    	new Thread(new Runnable() { //�����߳��ϴ��ļ�
    		@Override
    		public void run() {
    			uploadFile(file, RequestURL,params);
    		}
    	}).start();
    }

    /**
     * android�ϴ��ļ���������
     * @param file  ��Ҫ�ϴ����ļ�
     * @param RequestURL  �����rul
     * @return  ������Ӧ������
     */
    private String uploadFile(File file,String RequestURL,Map<String, String> param){
        String result = null;
        String  BOUNDARY =  UUID.randomUUID().toString();  //�߽��ʶ   �������
        String PREFIX = "--" , LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data";   //��������
        // ��ʾ���ȿ�
//		showProgressDialog();
        try {
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true);  //����������
            conn.setDoOutput(true); //���������
            conn.setUseCaches(false);  //������ʹ�û���
            conn.setRequestMethod("POST");  //����ʽ
            conn.setRequestProperty("Charset", CHARSET);  //���ñ���
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            if(file!=null){
                /**
                 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
                 */
                DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
                StringBuffer sb = new StringBuffer();
                
                String params = "";  
                if (param != null && param.size() > 0) {  
                    Iterator<String> it = param.keySet().iterator();  
                    while (it.hasNext()) {  
                        sb = null;  
                        sb = new StringBuffer();  
                        String key = it.next();  
                        String value = param.get(key);  
                        sb.append(PREFIX).append(BOUNDARY).append(LINE_END);  
                        sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);  
                        sb.append(value).append(LINE_END);  
                        params = sb.toString();  
                        Log.i(TAG, key+"="+params+"##");  
                        dos.write(params.getBytes());  
//                      dos.flush();  
                    }  
                }  
                sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * �����ص�ע�⣺
                 * name�����ֵΪ����������Ҫkey   ֻ�����key �ſ��Եõ���Ӧ���ļ�
                 * filename���ļ������֣�������׺����   ����:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"upfile\";filename=\""+file.getName()+"\""+LINE_END);
                sb.append("Content-Type: image/pjpeg; charset="+CHARSET+LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(file);
                byte[] bytes = new byte[1024];
                int len = 0;
                while((len=is.read(bytes))!=-1){
                    dos.write(bytes, 0, len);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();
                dos.write(end_data);
                
                dos.flush();
                /**
                 * ��ȡ��Ӧ��  200=�ɹ�
                 * ����Ӧ�ɹ�����ȡ��Ӧ����
                 */

                int res = conn.getResponseCode();
                System.out.println("res========="+res);
                if(res==200){
                    InputStream input =  conn.getInputStream();
                    StringBuffer sb1= new StringBuffer();
                    int ss ;
                    while((ss=input.read())!=-1){
                        sb1.append((char)ss);
                    }
                    result = sb1.toString();
//                 // �Ƴ����ȿ�
//    				removeProgressDialog();
                    finish();
                }
                else{
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


}
