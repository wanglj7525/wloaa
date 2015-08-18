package com.ytint.wloaa.app;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.ytint.wloaa.activity.AddZhiliangReportActivity;
import com.ytint.wloaa.activity.LoginActivity;
import com.ytint.wloaa.activity.MainActivity;
import com.ytint.wloaa.activity.ZhiliangListActivity;
import com.ytint.wloaa.bean.URLs;

public class FileHelper extends Activity{
	String TAG = "FileHelper";
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码
	String result;
	private MyApplication application = (MyApplication) this.getApplication();
	public void submitUploadFile(ArrayList<String> srcPath, String loginKey,final String type) {
		final ArrayList<File> files=new ArrayList<File>();
		for (String path : srcPath) {
			File file = new File(path);
			if (file == null || (!file.exists())) {
				return ;
			}
			files.add(file);
		}
		if (files==null||files.size()==0) {
			return ;
		}
//		final File file = new File(srcPath);
		final String RequestURL = URLs.UPLOADPHOTO;

		Log.i(TAG, "请求的URL=" + RequestURL);
		Log.i(TAG, "请求的fileName=" + files.get(0).getName());
		final Map<String, String> params = new HashMap<String, String>();
		params.put("user_id", loginKey);
		params.put("file_type", type);
		new Thread(new Runnable() { // 开启线程上传文件
					@Override
					public void run() {
						result= uploadFile(files, RequestURL, params,type);
					}
				}).start();
	}

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	private String uploadFile(ArrayList<File> files, String RequestURL,
			Map<String, String> param,String type) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型
		// 显示进度框
		// showProgressDialog();
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="
					+ BOUNDARY);
			if (files != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(
						conn.getOutputStream());
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
						sb.append("Content-Disposition: form-data; name=\"")
								.append(key).append("\"").append(LINE_END)
								.append(LINE_END);
						sb.append(value).append(LINE_END);
						params = sb.toString();
						Log.i(TAG, key + "=" + params + "##");
						dos.write(params.getBytes());
						// dos.flush();
					}
				}
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				int flag=0;
				for (File file : files) {
					sb = new StringBuffer();
					sb.append(PREFIX);
					sb.append(BOUNDARY);
					sb.append(LINE_END);
					sb.append("Content-Disposition: form-data; name=\"upfile["+flag+"]\";filename=\""
							+ file.getName() + "\"" + LINE_END);
					if (type.equals("3")) {
						sb.append("Content-Type: audio/mpeg; charset=" + CHARSET
								+ LINE_END);
					}else if (type.equals("2")) {
						sb.append("Content-Type: video/3gpp; charset=" + CHARSET
								+ LINE_END);
					}else{
						sb.append("Content-Type: image/pjpeg; charset=" + CHARSET
								+ LINE_END);
					}
					
					sb.append(LINE_END);
					dos.write(sb.toString().getBytes());
					Log.i(TAG,  "files=" + sb.toString() + "##");
					InputStream is = new FileInputStream(file);
					byte[] bytes = new byte[1024];
					int len = 0;
					while ((len = is.read(bytes)) != -1) {
						dos.write(bytes, 0, len);
					}
					is.close();
					dos.write(LINE_END.getBytes());
					flag++;
				}
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);

				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */

				int res = conn.getResponseCode();
				if (res == 200) {
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
					System.out.println("result=========" + result);
					
					JSONObject jsonObject = null;
					try {
						jsonObject = new JSONObject(result);
						int code = Integer.parseInt(jsonObject.getString(
								"code").toString());
						if (code == Constants.SUCCESS) {
							String ids=jsonObject.getString("info");
							System.out.println(ids+"====="+type);
							if (type.equals("1")) {
								AddZhiliangReportActivity.attachment.add(ids);
							}else if (type.equals("2")) {
								AddZhiliangReportActivity.video.add(ids);
							}else if (type.equals("3")) {
								AddZhiliangReportActivity.media.add(ids);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				} else {
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
