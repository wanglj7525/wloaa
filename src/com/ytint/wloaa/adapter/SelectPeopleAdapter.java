package com.ytint.wloaa.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.ytint.wloaa.R;

public class SelectPeopleAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<Map<String, Object>> mData;
	public static Map<Integer, Boolean> isSelected;

	public SelectPeopleAdapter(Context context, List<Map<String, Object>> mDatas,Map<Integer, Boolean> isSelecteds) {
		mInflater = LayoutInflater.from(context);
		init(mDatas,isSelecteds);
	}

	// 初始化
	private void init(List<Map<String, Object>> mDatas,Map<Integer, Boolean> isSelecteds) {
//		mData = new ArrayList<Map<String, Object>>();
		// this.peoplelist=peoplelist;
		// this.deptartments=deptartments;
//		for (Department department : deptartments) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("name", department.name);
//			map.put("isdept", "1");
//			map.put("dept_id", department.id);
//			map.put("peopleid", 0);
//			mData.add(map);
//			for (People people : peoplelist) {
//				if (people.department_id.trim().equals(department.id.trim())) {
//					map = new HashMap<String, Object>();
//					map.put("name", people.name);
//					map.put("isdept", "2");
//					map.put("dept_id",people.department_id);
//					map.put("peopleid",people.id);
//					mData.add(map);
//				}
//			}
//		}
		mData=mDatas;
		isSelected=isSelecteds;
//		// 这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
//		isSelected = new HashMap<Integer, Boolean>();
//		for (int i = 0; i < mData.size(); i++) {
//			isSelected.put(i, false);
//		}
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position).get("name");
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		// convertView为null的时候初始化convertView。
//		if (convertView == null) {
			holder = new ViewHolder();
			if (mData.get(position).get("isdept").toString().equals("1")) {
				convertView = mInflater.inflate(
						R.layout.addpeople_list_item_tag, null);
				holder.title = (TextView) convertView
						.findViewById(R.id.addpeople_list_item_text);
				holder.cBox = (CheckBox) convertView
						.findViewById(R.id.addpeople_list_item_check);
			} else {
				convertView = mInflater.inflate(R.layout.addpeople_list_item,
						null);
				holder.title = (TextView) convertView
						.findViewById(R.id.people_one);
				holder.cBox = (CheckBox) convertView
						.findViewById(R.id.people_check);
			}
			convertView.setTag(holder);
//		} else {
//			holder = (ViewHolder) convertView.getTag();
//		}
		holder.title.setText(mData.get(position).get("name").toString());
		holder.cBox.setChecked(isSelected.get(position));
		holder.dept_id=mData.get(position).get("dept_id").toString();
		holder.isdept=mData.get(position).get("isdept").toString();
		holder.peopleid=mData.get(position).get("peopleid").toString();
		holder.user_num=Integer.parseInt(mData.get(position).get("user_num").toString());
		return convertView;
	}

	public final class ViewHolder {
		public String dept_id=new String();
		public String isdept=new String();
		public String peopleid=new String();
		public int user_num;
		public TextView title;
		public CheckBox cBox;
	}
}
