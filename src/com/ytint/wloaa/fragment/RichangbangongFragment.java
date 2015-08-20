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

public class RichangbangongFragment extends Fragment {

	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;

	public RichangbangongFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_richangbangong,
				container, false);
		findView(rootView);
		return rootView;
	}

	private void findView(View rootView) {
		// 发布公告
		button1 = (Button) rootView.findViewById(R.id.bangonggonggaosend);
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						AddXiaoxiSendActivity.class);
				intent.putExtra("from", 2);
				startActivity(intent);
			}
		});

		button2 = (Button) rootView.findViewById(R.id.bangonggonggaolist);
		button2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						XiaoxiListActivity.class);
				intent.putExtra("from", 0);
				startActivity(intent);
			}
		});

		button3 = (Button) rootView.findViewById(R.id.bangongxiaoxisend);
		button3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						AddXiaoxiSendActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});

		button4 = (Button) rootView.findViewById(R.id.bangongxiaoxilist);
		button4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),
						XiaoxiListActivity.class);
				intent.putExtra("from", 1);
				startActivity(intent);
			}
		});

	}

}
