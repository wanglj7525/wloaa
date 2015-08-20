package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.AddXiaoxiSendActivity;
import com.ytint.wloaa.activity.XiaoxiListActivity;

public class GonggaoFragment extends Fragment {
	
	private Button button1;
	private Button button2;
	
	public GonggaoFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_gonggao, container, false);
        findView(rootView);
        return rootView;
    }
	private void findView(View rootView) {
		// 发布公告
		button1=(Button)rootView.findViewById(R.id.gonggaosend);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddXiaoxiSendActivity.class);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.gonggaolist);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),XiaoxiListActivity.class);
				intent.putExtra("from", 0);
				startActivity(intent);
			}
		});
		
	}
	
}
