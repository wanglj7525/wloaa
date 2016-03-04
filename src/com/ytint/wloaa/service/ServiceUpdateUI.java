package com.ytint.wloaa.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ytint.wloaa.activity.MainActivity;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.URLs;

public class ServiceUpdateUI extends Service {
	private Timer timer;
	private TimerTask task;
	private MyApplication application;
	private String host;
	private String loginKey;
	int news = 0;
	int news0 = 0;
	int newstask = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = (MyApplication) this.getApplication();
		

		final Intent intent = new Intent();
		intent.setAction(MainActivity.ACTION_UPDATEUI);

		timer = new Timer();
		task = new TimerTask() {

			@Override
			public void run() {
				updatetitle();
				intent.putExtra("news", news);
				intent.putExtra("news0", news0);
				intent.putExtra("newstask", newstask);
				sendBroadcast(intent);
			}
		};
		timer.schedule(task, 1, 10000);
	}

	public void updatetitle() {
		host = URLs.HTTP + application.getProperty("HOST") + ":"
				+ application.getProperty("PORT");
		System.out.println("host==="+host);
		loginKey = application.getProperty("loginKey");
		
		System.out.println("查询新消息数量");
		String url = String.format("%s?user_id=%s&notice_type=0", host
				+ URLs.NEWNUM, loginKey);
		BufferedReader in = null;

		String content = null;
		try {
			// 定义HttpClient
			HttpClient client = new DefaultHttpClient();
			// 实例化HTTP方法
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);

			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			in.close();
			content = sb.toString();

			JSONObject myJsonObject = new JSONObject(content);
			JSONObject infoJsonObject = myJsonObject.getJSONObject("info");
			// 公告
			news0 = infoJsonObject.getInt("gg");
			news = infoJsonObject.getInt("xx");
			newstask = infoJsonObject.getInt("rw");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("异常"+e);
		} finally {
			if (in != null) {
				try {
					in.close();// 最后要关闭BufferedReader
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		// client.get(url, new HttpResponseHandler() {
		// @Override
		// public void onSuccess(String response) {
		// System.out.println("services---"+response);
		// try {
		// JSONObject myJsonObject = new JSONObject(response);
		// JSONObject infoJsonObject=myJsonObject.getJSONObject("info");
		// //公告
		// news0=infoJsonObject.getInt("gg");
		// news=infoJsonObject.getInt("xx");
		// newstask=infoJsonObject.getInt("rw");
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
	}

	@Override
	public void onDestroy() {
		System.out.println("停止service");
		super.onDestroy();
		timer.cancel();
	}

}
