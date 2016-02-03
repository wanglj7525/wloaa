package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.SendTaskActivity;
import com.ytint.wloaa.activity.AddZhiliangSendActivity;
import com.ytint.wloaa.activity.ProjectListActivity;
import com.ytint.wloaa.activity.TaskListActivity;
import com.ytint.wloaa.app.MyApplication;

public class TaskFragment extends Fragment {
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private MyApplication application;
	private String userType;
	private String departmentId;
	private int if_task_power;
	public TaskFragment(){}
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_zhifa, container, false);
        application = (MyApplication) getActivity().getApplication();
        userType = application.getProperty("userType");
        departmentId = application.getProperty("department_id");
        if_task_power =Integer.parseInt(application.getProperty("if_task_power"));
        findView(rootView);
        return rootView;
    }
	
	private void findView(View rootView) {
		//TODO
		button1=(Button)rootView.findViewById(R.id.zhifa1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
//				Intent intent = new Intent(getActivity(),AddZhiliangSendActivity.class);
				Intent intent = new Intent(getActivity(),SendTaskActivity.class);
				intent.putExtra("from", 1);
				intent.putExtra("reply_task_id", 0);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.zhifa2);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),SendTaskActivity.class);
				intent.putExtra("reply_task_id", 0);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});
//		if (userType.equals("3")) {
//			button1.setEnabled(true);
//		}else if (userType.equals("4")) {
//			button2.setEnabled(true);
//		}
		if (userType.equals("0")||userType.equals("1")||userType.equals("2")) {
			button1.setVisibility(View.GONE);
			button2.setVisibility(View.GONE);
			departmentId="0";
		}
		//任务列表
		button3=(Button)rootView.findViewById(R.id.zhifa3);
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),TaskListActivity.class);
				intent.putExtra("from", departmentId);
				startActivity(intent);
			}
		});
		//工程列表
		button4=(Button)rootView.findViewById(R.id.zhifa4);
		button4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),ProjectListActivity.class);
				intent.putExtra("from", departmentId);
				startActivity(intent);
			}
		});
		if (if_task_power==0) {
			//只有质检站、安监站、监察大队、局领导的成员，才能点击这两个按钮，其他人员不能操作工程任务和工程列表。
			button1.setVisibility(View.GONE);
			button4.setVisibility(View.GONE);
		}
	}
	
}