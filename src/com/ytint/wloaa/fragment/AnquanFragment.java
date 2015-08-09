package com.ytint.wloaa.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ytint.wloaa.R;

public class AnquanFragment extends Fragment {
	
	public AnquanFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_anquan, container, false);
         
        return rootView;
    }
}
