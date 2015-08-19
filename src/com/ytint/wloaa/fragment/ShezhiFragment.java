package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.activity.AddQiyeActivity;
import com.ytint.wloaa.activity.DeleteQiyeActivity;
import com.ytint.wloaa.activity.LoginActivity;
import com.ytint.wloaa.activity.PassWordUpdateActivity;
import com.ytint.wloaa.activity.QiyeListActivity;
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.activity.ZhiliangListActivity;

public class ShezhiFragment extends Fragment {
	private Button button1;
	private Button button2;
	public ShezhiFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_shezhi, container, false);
        findView(rootView);
        return rootView;
    }
	
	private void findView(View rootView) {
		button1=(Button)rootView.findViewById(R.id.updatepas);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),PassWordUpdateActivity.class);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.changeuser);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),LoginActivity.class);
				startActivity(intent);
			}
		});
	}
	
}