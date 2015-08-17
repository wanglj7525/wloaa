package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.activity.XiaoxiShowActivity;

public class GonggaoFragment extends Fragment {
	
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
	
	public GonggaoFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_xiaoxi, container, false);
        findView(rootView);
        return rootView;
    }
	private void findView(View rootView) {
		//下派质检任务
		button1=(Button)rootView.findViewById(R.id.xiaoxi1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),XiaoxiShowActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.xiaoxi2);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),XiaoxiShowActivity.class);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});
		button3=(Button)rootView.findViewById(R.id.xiaoxi3);
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),XiaoxiShowActivity.class);
				intent.putExtra("from", 3);
				startActivity(intent);
			}
		});
		button4=(Button)rootView.findViewById(R.id.xiaoxi4);
		button4.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),XiaoxiShowActivity.class);
				intent.putExtra("from", 4);
				startActivity(intent);
			}
		});
		button5=(Button)rootView.findViewById(R.id.xiaoxi5);
		button5.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),XiaoxiShowActivity.class);
				intent.putExtra("from", 5);
				startActivity(intent);
			}
		});
	}
	
}
