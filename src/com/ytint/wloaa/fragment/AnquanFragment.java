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

public class AnquanFragment extends Fragment {
	private Button button1;
	private Button button2;
	private Button button3;
	public AnquanFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_anquan, container, false);
        findView(rootView);
        return rootView;
    }

	private void findView(View rootView) {
		//下派质检任务
		button1=(Button)rootView.findViewById(R.id.anquan1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddZhiliangSendActivity.class);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.anquan2);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddZhiliangReportActivity.class);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});
		button3=(Button)rootView.findViewById(R.id.anquan3);
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),ZhiliangListActivity.class);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});
	}
	
}
