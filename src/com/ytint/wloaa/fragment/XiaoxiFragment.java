package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.activity.AddXiaoxiSendActivity;
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.activity.XiaoxiListActivity;
import com.ytint.wloaa.activity.XiaoxiShowActivity;

public class XiaoxiFragment extends Fragment {
	
	private Button button1;
	private Button button2;
	
	public XiaoxiFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_xiaoxi, container, false);
        findView(rootView);
        return rootView;
    }
	private void findView(View rootView) {
		// 发送消息
		button1=(Button)rootView.findViewById(R.id.xiaoxisend);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddXiaoxiSendActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.xiaoxilist);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),XiaoxiListActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});
	}
	
}
