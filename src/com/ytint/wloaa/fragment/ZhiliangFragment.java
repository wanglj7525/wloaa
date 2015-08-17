package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.activity.AddZhiliangReportActivity;
import com.ytint.wloaa.activity.AddZhiliangSendActivity;
import com.ytint.wloaa.activity.ZhiliangListActivity;
import com.ytint.wloaa.app.MyApplication;

public class ZhiliangFragment extends Fragment {
	
	private Button button1;
	private Button button2;
	private Button button3;
	private MyApplication application;
	private String userType;
	public ZhiliangFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_zhiliang, container, false);
        application = (MyApplication) getActivity().getApplication();
        userType = application.getProperty("userType");
        findView(rootView);
        
        return rootView;
    }


	private void findView(View rootView) {
		//下派质检任务
		button1=(Button)rootView.findViewById(R.id.zhiliang1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddZhiliangSendActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.zhiliang2);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddZhiliangReportActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});
		
		if (userType.equals("3")) {
			button1.setEnabled(true);
		}else if (userType.equals("4")) {
			button2.setEnabled(true);
		}
		
		button3=(Button)rootView.findViewById(R.id.zhiliang3);
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),ZhiliangListActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});
	}
	
}