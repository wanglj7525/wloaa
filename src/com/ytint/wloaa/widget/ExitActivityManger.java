package com.ytint.wloaa.widget;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class ExitActivityManger extends Application {

private List<Activity> activityList = new LinkedList<Activity>(); 
private static ExitActivityManger instance;
private String TAG="ExitActivityManger";

	private ExitActivityManger()
	{
	}
	 //����ģʽ�л�ȡΨһ��MyApplicationʵ�� 
	 public static ExitActivityManger getInstance()
	 {
		 if(null == instance)
		{
			 instance = new ExitActivityManger();
		}
	     return instance;             
	 }
	 //���Activity��������
	 public void addActivity(Activity activity)
	 {
	    activityList.add(activity);
	 }
	 //��������Activity��finish
	 public void exit()
	 {
		 for(Activity activity:activityList)
	     {
	    	  
	       activity.finish();
	     }
	     System.exit(0);
	}
	 //ֻ��finish activity���ǲ��˳�����
	 public void finish()
	 {
		 for(Activity activity:activityList)
	     {
	    	  
	       activity.finish();
	     }

	}
	 //��������Activity��finish
	 public boolean queryActivityIsStart(String classname)
	 {
		 for(Activity activity:activityList)
	     {
			 Log.e(TAG, activity.getLocalClassName());
			 if(activity.getLocalClassName().equalsIgnoreCase(classname))
			 {
				 Log.e(TAG,"true");
				 return true;
			 }
	    	  
	     }
	    return false;
	}

}
