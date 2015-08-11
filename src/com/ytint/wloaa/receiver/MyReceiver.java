//package com.ytint.wloaa.receiver;
//
//import com.ytint.wloaa.activity.PushResultActivity;
//
//import android.app.ActivityManager;
//import android.app.ActivityManager.RunningServiceInfo;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import cn.jpush.android.api.JPushInterface;
//import cn.jpush.android.service.PushService;
//
///**
// * �Զ��������
// * 
// * �������� Receiver���� 1) Ĭ���û���������� 2) ���ղ����Զ�����Ϣ
// */
//public class MyReceiver extends BroadcastReceiver {
//	private static final String TAG = "JPush";
//
//	@Override
//	public void onReceive(Context context, Intent intent) {
//		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
//			// ���Service״̬
//			ActivityManager manager = (ActivityManager) context
//					.getSystemService(Context.ACTIVITY_SERVICE);
//			boolean isServiceRunning = false;
//			for (RunningServiceInfo service : manager
//					.getRunningServices(Integer.MAX_VALUE)) {
//				if ("cn.jpush.android.service.PushService"
//						.equals(service.service.getClassName())) {
//					isServiceRunning = true;
//				}
//			}
//			if (!isServiceRunning) {
//				Intent i = new Intent(context, PushService.class);
//				context.startService(i);
//			}
//		}
//
//		Bundle bundle = intent.getExtras();
//		Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction()
//				+ ", extras: " + printBundle(bundle));
//
//		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
//			String regId = bundle
//					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//			Log.d(TAG, "[MyReceiver] ����Registration Id : " + regId);
//
//		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
//				.getAction())) {
//			Log.d(TAG,
//					"[MyReceiver] ���յ������������Զ�����Ϣ: "
//							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
//			processCustomMessage(context, bundle);
//
//		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
//				.getAction())) {
//			Log.d(TAG, "[MyReceiver] ���յ�����������֪ͨ");
//			int notifactionId = bundle
//					.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//			Log.d(TAG, "[MyReceiver] ���յ�����������֪ͨ��ID: " + notifactionId);
//
//		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
//				.getAction())) {
//			Log.d(TAG, "[MyReceiver] �û��������֪ͨ");
//
//			JPushInterface.reportNotificationOpened(context,
//					bundle.getString(JPushInterface.EXTRA_MSG_ID));
//			// ���Զ����Activity
//			Intent i = new Intent(context, PushResultActivity.class);
//			i.putExtras(bundle);
//			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startActivity(i);
//
//		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
//				.getAction())) {
//			Log.d(TAG,
//					"[MyReceiver] �û��յ���RICH PUSH CALLBACK: "
//							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
//			// �������� JPushInterface.EXTRA_EXTRA �����ݴ�����룬������µ�Activity��
//			// ��һ����ҳ��..
//
//		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
//				.getAction())) {
//			boolean connected = intent.getBooleanExtra(
//					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//			Log.e(TAG, "[MyReceiver]" + intent.getAction()
//					+ " connected state change to " + connected);
//		} else {
//			Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
//		}
//	}
//
//	// ��ӡ���е� intent extra ���
//	private static String printBundle(Bundle bundle) {
//		StringBuilder sb = new StringBuilder();
//		for (String key : bundle.keySet()) {
//			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
//				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
//			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
//				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
//			} else {
//				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
//			}
//		}
//		return sb.toString();
//	}
//
//	// send msg to MainActivity
//	private void processCustomMessage(Context context, Bundle bundle) {
//		// if (MainActivity.isForeground) {
//		// String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//		// String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
//		// Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
//		// msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
//		// if (!ExampleUtil.isEmpty(extras)) {
//		// try {
//		// JSONObject extraJson = new JSONObject(extras);
//		// if (null != extraJson && extraJson.length() > 0) {
//		// msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
//		// }
//		// } catch (JSONException e) {
//		//
//		// }
//		//
//		// }
//		// context.sendBroadcast(msgIntent);
//		// }
//	}
//}
//
