package com.ytint.wloaa.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.AddXiapaiActivity;

public class HomeFragment extends Fragment {
	
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	private Button button5;
//	private ViewPager mViewPager;
//	private static final String[] titles = {"One","Two","Three","Four","Five"};
//	private List<ContentBean> list = new ArrayList<ContentBean>();
//	private ContentFragmentPagerAdapter mAdapter;
	
	public HomeFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        
        findView(rootView);
        
        return rootView;
    }


	private void findView(View rootView) {
		//下派质检任务
		button1=(Button)rootView.findViewById(R.id.zhiliang1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(),AddXiapaiActivity.class);
				startActivity(intent);
			}
		});
		
		button2=(Button)rootView.findViewById(R.id.zhiliang2);
		button3=(Button)rootView.findViewById(R.id.zhiliang3);
		button4=(Button)rootView.findViewById(R.id.zhiliang4);
		button5=(Button)rootView.findViewById(R.id.zhiliang5);
//		mViewPager = (ViewPager) rootView.findViewById(R.id.mViewPager);
		
//		PagerTabStrip mPagerTabStrip = (PagerTabStrip) rootView.findViewById(R.id.mPagerTabStrip);
//		mPagerTabStrip.setTabIndicatorColor(getResources().getColor(R.color.select_text_color)); 
		
//		mAdapter = new ContentFragmentPagerAdapter(getActivity().getSupportFragmentManager(),list);
//		mViewPager.setAdapter(mAdapter);
	}
	
	@Override
	public void onStart() {
		
//		if(mAdapter!=null){
//			mAdapter.notifyDataSetChanged();
//		}
//		
		super.onStart();
	}
}
