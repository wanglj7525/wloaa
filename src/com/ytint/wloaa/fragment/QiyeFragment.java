package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.AddQiyeActivity;
import com.ytint.wloaa.activity.AddZhiliangReportActivity;
import com.ytint.wloaa.activity.AddZhiliangSendActivity;
import com.ytint.wloaa.activity.DeleteQiyeActivity;
import com.ytint.wloaa.activity.QiyeListActivity;
import com.ytint.wloaa.activity.ZhiliangListActivity;

public class QiyeFragment extends Fragment {
	private Button button1;
	private Button button2;
	private Button button3;
	public QiyeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        findView(rootView);
        return rootView;
    }
	
	private void findView(View rootView) {
		button1=(Button)rootView.findViewById(R.id.qiye1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddQiyeActivity.class);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.qiye2);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),DeleteQiyeActivity.class);
				startActivity(intent);
			}
		});
		button3=(Button)rootView.findViewById(R.id.qiye3);
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),QiyeListActivity.class);
				startActivity(intent);
			}
		});
	}
	
}