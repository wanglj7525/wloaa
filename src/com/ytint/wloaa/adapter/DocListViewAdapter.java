package com.ytint.wloaa.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ab.bitmap.AbImageDownloader;
import com.ab.global.AbConstant;
import com.ytint.wloaa.activity.R;
import com.ytint.wloaa.bean.DocInfo;

public class DocListViewAdapter extends BaseAdapter {
	private List<DocInfo> mArrayList;
	private LayoutInflater mLayoutInflater;
	private Context mContext;
	// ͼƬ������
	private AbImageDownloader mAbImageDownloader = null;

	public DocListViewAdapter(Context context, List<DocInfo> data) {
		this.mArrayList = data;
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		// ͼƬ������
		mAbImageDownloader = new AbImageDownloader(context);
		mAbImageDownloader.setWidth(80);
		mAbImageDownloader.setHeight(60);
		mAbImageDownloader.setType(AbConstant.SCALEIMG);
		mAbImageDownloader.setLoadingImage(R.drawable.image_loading);
		mAbImageDownloader.setErrorImage(R.drawable.image_error);
		mAbImageDownloader.setNoImage(R.drawable.image_no);
	}

	@Override
	public int getCount() {
		return mArrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return mArrayList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = null;
		View weiboItemView = null;
		View commentItemView = null;
		CommentViewHolder commentViewHolder = null;
		WeiboViewHolder weiboViewHolder = null;
		ViewHolder viewHolder = null;
		DocInfo doc = mArrayList.get(position);
		// ��ͨ���µ����
			itemView = convertView;
			if (itemView == null) {
				itemView = mLayoutInflater.inflate(R.layout.layout_doclistitem,
						null);
				viewHolder = new ViewHolder();
				viewHolder.title = (TextView) itemView
						.findViewById(R.id.itemsTitle);
				viewHolder.abstr = (TextView) itemView
						.findViewById(R.id.itemsAbstract);
				viewHolder.imageView = (ImageView) itemView
						.findViewById(R.id.itemsImage);
				viewHolder.timeView = (TextView) itemView
						.findViewById(R.id.time);
				itemView.setTag(viewHolder);
			} else {
				if (itemView.getTag() instanceof ViewHolder) {
					viewHolder = (ViewHolder) itemView.getTag();
				} else {
					viewHolder = new ViewHolder();
					itemView = mLayoutInflater.inflate(
							R.layout.layout_doclistitem, null);
					viewHolder.title = (TextView) itemView
							.findViewById(R.id.itemsTitle);
					viewHolder.abstr = (TextView) itemView
							.findViewById(R.id.itemsAbstract);
					viewHolder.imageView = (ImageView) itemView
							.findViewById(R.id.itemsImage);
					viewHolder.timeView = (TextView) itemView
							.findViewById(R.id.time);
					itemView.setTag(viewHolder);
				}
			}
			viewHolder.title.setText(Html.fromHtml(doc.t));
//			viewHolder.title.setText(doc.t);
			viewHolder.title.setTag(doc);
			if (doc.pic.length > 0 && doc.pic[0].length() > 5) {
				if (doc.pic[0].substring(doc.pic[0].length() - 4,
						doc.pic[0].length()).equals(".gif")) {
					viewHolder.imageView.setVisibility(View.GONE);
				} else {
					viewHolder.imageView.setVisibility(View.VISIBLE);
					mAbImageDownloader
							.display(viewHolder.imageView, doc.pic[0]);
				}
			} else {
				viewHolder.imageView.setVisibility(View.GONE);
			}
			viewHolder.abstr.setText(Html.fromHtml(doc.a));
			if(doc.a.length()>0){
				viewHolder.abstr.setVisibility(View.VISIBLE);
			}else{
				viewHolder.abstr.setVisibility(View.GONE);
			}
			viewHolder.timeView.setText(doc.pt.toString() + "   " + doc.sn);
			convertView = itemView;
		return convertView;
	}

	class CommentViewHolder {
		TextView author;
		TextView timeView;
		TextView agree;
		TextView replay;
		TextView comment;
	}

	class ViewHolder {
		ImageView imageView;
		TextView title;
		TextView abstr;
		TextView timeView;
	}

	class WeiboViewHolder {
		ImageView imageView;
		TextView title;
		TextView abstr;
		TextView timeView;
	}
}
