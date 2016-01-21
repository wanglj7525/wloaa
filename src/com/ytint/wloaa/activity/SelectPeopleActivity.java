package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.adapter.SelectPeopleAdapter;
import com.ytint.wloaa.adapter.SelectPeopleAdapter.ViewHolder;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.Department;
import com.ytint.wloaa.bean.DepartmentList;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.PeopleList;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.widget.TitleBar;

/**
 * 选择联系人
 * 
 * @author wlj
 * 
 */
public class SelectPeopleActivity extends AbActivity {

	String TAG = "SelectPeopleActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	String host;
	@AbIocView(id = R.id.listView_people_list)
	ListView listView_people_list;
//	private List<String> peopleidlist = new ArrayList<String>();
	private HashSet<String> peopleidlist=new HashSet<String>(); 
	public static Map<Integer, Boolean> isSelected;
	private List<People> peoples;
	private List<Department> deptartments;
	private SelectPeopleAdapter adapter;
	private List<Map<String, Object>> mData;
	private List<String> userlist=new ArrayList<String>();
	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application = (MyApplication) this.getApplication();
		host = URLs.HTTP + application.getProperty("HOST") + ":"
				+ application.getProperty("PORT");
		Intent intent = getIntent();
		userlist=intent.getStringArrayListExtra("userlist");
		setAbContentView(R.layout.layout_select_people);
		context = SelectPeopleActivity.this;
		loginKey = application.getProperty("loginKey");
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		final TitleBar titleBar = (TitleBar) findViewById(R.id.title_bar);
		titleBar.setLeftImageResource(R.drawable.back_green);
		titleBar.setLeftText("返回");
		titleBar.setLeftTextColor(Color.WHITE);
		titleBar.setLeftClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		        finish();
		    }
		});
		titleBar.setTitle("选择联系人");
		titleBar.setTitleColor(Color.WHITE);
		titleBar.setDividerColor(Color.GRAY);
		titleBar.setActionTextColor(Color.WHITE);
		titleBar.addAction(new TitleBar.TextAction("完成") {
		    @Override
		    public void performAction(View view) {
		    	//数据是使用Intent返回
	            Intent intent = new Intent();
	            ArrayList<Map<String, Object>> datas=new ArrayList<Map<String, Object>>();
	            //获得选择的人员列表
	            for (int i = 0; i < isSelected.size(); i++) {
	            	if (isSelected.get(i)) {
	            		if (!mData.get(i).get("peopleid").toString().equals("0")) {
	            			datas.add(mData.get(i));
	            		}
	            	}
	            }
//	            for (int i = 0; i < peopleidlist.size(); i++) {
//	            	if (!mData.get(i).get("peopleid").toString().equals("0")) {
//	            		datas.add(mData.get(i));
//					}
//				}
	            //把返回数据存入Intent
	            intent.putExtra("result", datas);
	            //设置返回数据
	            SelectPeopleActivity.this.setResult(RESULT_OK, intent);
	            //关闭Activity
	            SelectPeopleActivity.this.finish();
		    }
		});
		loadDept();

	}
	
	@SuppressLint("NewApi")
	private void loadDept() {
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		String host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		String url=host+URLs.DEPLIST+"?user_id="+loginKey;
		Log.e(TAG, url);
		mAbHttpUtil.get(url,
			 new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					DepartmentList cList = DepartmentList.parseJson(content);
					if (cList.code == 200) {
						deptartments = cList.getInfo();
						loadPeoples();
					} else {
						showToast(cList.msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("数据解析失败");
				}
			};

			// 开始执行前
			@Override
			public void onStart() {
				// 显示进度框
				showProgressDialog();
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				showToast("网络连接失败！");
			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				// 移除进度框
				removeProgressDialog();
			};

		});
	}
	@SuppressLint("NewApi")
	private void loadPeoples() {
		
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		String host=URLs.HTTP+application.getProperty("HOST")+":"+application.getProperty("PORT");
		mAbHttpUtil.get(host+URLs.USERLIST+"?user_id=0" ,
				new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					PeopleList cList = PeopleList.parseJson(content);
					if (cList.code == 200) {
						peoples = cList.getInfo();
						for (int i = 0; i < peoples.size(); i++) {
							if (loginKey.equals(peoples.get(i).id+"")) {
								peoples.remove(i);
							}
						}
						mData = new ArrayList<Map<String, Object>>();
						for (Department department : deptartments) {
							Map<String, Object> map = new HashMap<String, Object>();
							map.put("name", department.name);
							map.put("isdept", "1");
							map.put("dept_id", department.id);
							map.put("peopleid", 0);
							map.put("user_num", department.user_num);
							mData.add(map);
							for (People people : peoples) {
								if (people.department_id.trim().equals(department.id.trim())) {
									map = new HashMap<String, Object>();
									map.put("name", people.name);
									map.put("isdept", "2");
									map.put("dept_id",people.department_id);
									map.put("peopleid",people.id);
									map.put("user_num",0);
									mData.add(map);
								}
							}
						}
						// 这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
						isSelected = new HashMap<Integer, Boolean>();
						for (int i = 0; i < mData.size(); i++) {
							if (userlist.contains(mData.get(i).get("peopleid").toString())) {
								isSelected.put(i, true);
							}else{
								isSelected.put(i, false);
							}
						}
						adapter=new SelectPeopleAdapter(context,mData,isSelected);  
						listView_people_list.setAdapter(adapter);  
						listView_people_list.setItemsCanFocus(false);  
						listView_people_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);  
						
						listView_people_list.setOnItemClickListener(new OnItemClickListener(){  
							@Override  
							public void onItemClick(AdapterView<?> parent, View view,  
									int position, long id) {  
								ViewHolder vHollder = (ViewHolder) view.getTag();  
								//在每次获取点击的item时将对于的checkbox状态改变，同时修改map的值。  
								vHollder.cBox.toggle();  
								SelectPeopleAdapter.isSelected.put(position, vHollder.cBox.isChecked());  
//								if (vHollder.cBox.isChecked()) {
//									peopleidlist.add(position+"");
//								}else {
//									peopleidlist.remove(position+"");
//								}
								//判断是否是部门，选择部门下的用户
								if (vHollder.isdept.equals("1")) {
									for (int i = 1; i <= vHollder.user_num; i++) {
										SelectPeopleAdapter.isSelected.put(position+i, vHollder.cBox.isChecked());
//										if (vHollder.cBox.isChecked()) {
//											peopleidlist.add(position+i+"");
//										}else {
//											peopleidlist.remove(position+i+"");
//										}
									}
									// 通知listView刷新  
									adapter.notifyDataSetChanged();  
								}
							}  
						});
					} else {
						showToast(cList.msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					showToast("数据解析失败");
				}
			};
			
			// 开始执行前
			@Override
			public void onStart() {
				// 显示进度框
				showProgressDialog();
			}
			
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				showToast("网络连接失败！");
			}
			
			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				// 移除进度框
				removeProgressDialog();
			};
			
		});
	}
//	public void initData() {
//		peoplelist = new ArrayList<String>();  
//        
//        groupkey.add("A组");  
//        groupkey.add("B组");  
//          
//        for(int i=0; i<10; i++){  
//            aList.add("A组"+i);  
//        }  
//        peoplelist.add("A组");  
//        peoplelist.addAll(aList);  
//          
//        for(int i=0; i<18; i++){  
//            bList.add("B组"+i);  
//        }  
//        peoplelist.add("B组");  
//        peoplelist.addAll(bList);  
//        
//      //这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。  
//        isSelected = new HashMap<Integer, Boolean>();  
//        for (int i = 0; i < peoplelist.size(); i++) {  
//            isSelected.put(i, false);  
//        }  
//	}
//
//	private class MyAdapter extends BaseAdapter {
//		 private LayoutInflater mInflater;  
//		    private List<Map<String, Object>> mData;  
//		                                                      
//		    public void MyAdapter(Context context) {  
//		        mInflater = LayoutInflater.from(context);  
//		    }  
//		                                                      
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return peoplelist.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			// TODO Auto-generated method stub
//			return peoplelist.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return position;
//		}
//
//		@Override
//		public boolean isEnabled(int position) {
//			// TODO Auto-generated method stub
//			if (groupkey.contains(getItem(position))) {
//				return false;
//			}
//			return super.isEnabled(position);
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			
//			
//			 ViewHolder holder = null;  
//		        //convertView为null的时候初始化convertView。  
//		        if (convertView == null) {  
//		            holder = new ViewHolder();  
//		            if (groupkey.contains(getItem(position))) {
//		            	convertView = LayoutInflater.from(getApplicationContext()).inflate(
//								R.layout.addpeople_list_item_tag, null);
//		            	holder.title=(TextView)convertView.findViewById(R.id.addpeople_list_item_text);
//					} else {
//						convertView = LayoutInflater.from(getApplicationContext()).inflate(
//								R.layout.addpeople_list_item, null);
//						holder.title=(TextView)convertView.findViewById(R.id.people_one);
//					} 
//		            holder.cBox = (CheckBox) convertView.findViewById(R.id.people_check);  
//		            convertView.setTag(holder);  
//		        } else {  
//		            holder = (ViewHolder) convertView.getTag();  
//		        }  
//		        holder.title.setText((CharSequence) getItem(position));  
//		        holder.cBox.setChecked(isSelected.get(position));  
//			
////			// TODO Auto-generated method stub
////			View view = convertView;
////			TextView text;
////			if (groupkey.contains(getItem(position))) {
////				view = LayoutInflater.from(getApplicationContext()).inflate(
////						R.layout.addpeople_list_item_tag, null);
////				text=(TextView)view.findViewById(R.id.addpeople_list_item_text);
////			} else {
////				view = LayoutInflater.from(getApplicationContext()).inflate(
////						R.layout.addpeople_list_item, null);
////				text=(TextView)view.findViewById(R.id.people_one);
////			}
////			text.setText((CharSequence) getItem(position));
//////			TextView text = (TextView) view
//////					.findViewById(R.id.addpeople_list_item_tagtext);
//////			text.setText((CharSequence) getItem(position));
//			return convertView;
//		}
//
//	}
//	public final class ViewHolder {  
//        public TextView title;  
//        public CheckBox cBox;  
//    }  
}
