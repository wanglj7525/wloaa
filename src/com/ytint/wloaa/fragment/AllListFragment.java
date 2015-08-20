package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.ZhiliangListActivity;

public class AllListFragment extends Fragment {
	private Button button1;
	private Button button2;
	private Button button3;
	public AllListFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_community, container, false);
        findView(rootView);
        return rootView;
    }
	
	private void findView(View rootView) {
		button1=(Button)rootView.findViewById(R.id.zhilianglist);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),ZhiliangListActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.anquanlist);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),ZhiliangListActivity.class);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});
		button3=(Button)rootView.findViewById(R.id.zhifalist);
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),ZhiliangListActivity.class);
				intent.putExtra("from", 3);
				startActivity(intent);
			}
		});
	}
	
}