package com.ytint.wloaa.activity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.view.titlebar.AbTitleBar;
import com.ytint.wloaa.R;
import com.ytint.wloaa.adapter.DocListViewAdapter;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.bean.People;
import com.ytint.wloaa.bean.ChannelList;
import com.ytint.wloaa.bean.DocInfo;
import com.ytint.wloaa.bean.DocInfoList;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.widget.DragListView;

/**
 * 云搜索弹出层页面
 * 
 * @author zhangyg
 * 
 */
public class YunSearchActivity extends AbActivity implements
		DragListView.OnRefreshLoadingMoreListener {
	private ArrayAdapter<String> adapter;
	private MyApplication application;
	private Spinner channelSpinner;
	private long channel = 0;
	private List<People> channels;
	String[] channel_names = new String[0];
	private ImageView yun_search;
	private EditText keyword_input;
	private TextView search_close;
	private TextView config_head;

	private ArrayList<DocInfo> listItems = new ArrayList<DocInfo>();
	private DragListView list;
	private DocListViewAdapter listItemAdapter;
	private String TAG = "CommonDocsListActivity";
	final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
	private int page = 1;
	private boolean hasNextPage = true;
	private LinearLayout yunsearch_full;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setVisibility(View.GONE);
		setAbContentView(R.layout.layout_search);
		application = (MyApplication) abApplication;
		initUi();
		initListView();
		channels = (List<People>) application.readObject("channels");
		if(null==channels||channels.size()<=0){
			loadChannels();
		}else{
			channel_names = new String[channels.size()];
			int i = 0;
			for (People cn : channels) {
//				channel_names[i] = cn.cn;
				i++;
			}
			initSpinner();
			oneSearch();
		}
	}

	private void initSpinner() {
		// 将可选内容与ArrayAdapter连接起来
		adapter = new ArrayAdapter<String>(YunSearchActivity.this,
				R.layout.spinner_item, channel_names);
		// 设置下拉列表的风格
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// 将adapter 添加到spinner中
		channelSpinner.setAdapter(adapter);
		// 设置默认选中
//		channelSpinner.setSelection(0);
		// 设置默认值
//		channelSpinner.setVisibility(View.VISIBLE);
		channelSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
//						channel = channels.get(arg2).cid;
						TextView tv = (TextView) view;
						tv.setTextColor(getResources().getColor(R.color.white)); // 设置颜色
						tv.setGravity(android.view.Gravity.CENTER); // 设置居中

						// 选择通道后，如果搜索框有内容则立即进行搜索
						if (keyword_input.getText() != null
								&& !keyword_input.getText().toString().trim()
										.equals("")) {
//							list.showLoading();
							page = 1;
							loadDocs(false, true);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						arg0.setVisibility(View.VISIBLE);
					}

				});

	}

	private void oneSearch() {
		// 通道返回成功，进行搜索
		Intent intent = getIntent();
		String keyword = intent.getStringExtra("SearchWord");
		if (keyword != null && !keyword.equals("")) {
			keyword_input.setText(keyword);
//			list.setLoading();
			//list.onLoadMoreComplete(true);
			loadDocs(false, true);
		} else {
			list.onLoadMoreComplete(true);
			list.changeLoadMoreViewText("暂无数据");
		}
		// 通道返回成功，进行搜索结束
	}

	private void initUi() {
		channelSpinner = (Spinner) findViewById(R.id.channel_spinner);
		yun_search = (ImageView) findViewById(R.id.yun_search);
		keyword_input = (EditText) findViewById(R.id.keyword_input);
		search_close = (TextView) findViewById(R.id.search_close);
		yunsearch_full = (LinearLayout) findViewById(R.id.yunsearch_full);
		config_head = (TextView) findViewById(R.id.config_head);
		list = (DragListView) findViewById(R.id.draglistview); // 绑定Layout里面的ListView

		config_head.setFocusable(true);
		config_head.setFocusableInTouchMode(true);
		config_head.requestFocus(); // 初始不让EditText得焦点
		config_head.requestFocusFromTouch();
		yun_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 关闭键盘
				InputMethodManager imm = (InputMethodManager) YunSearchActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				//list.showLoading();
				loadDocs(false, true);
			}
		});
		search_close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		OnClickListener keyboard_hide = new OnClickListener() {

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) YunSearchActivity.this
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}

		};
		yunsearch_full.setClickable(true);
		yunsearch_full.setOnClickListener(keyboard_hide);
	}

	private void initListView() {
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new DocListViewAdapter(this, listItems);

		// 添加并且显示
		list.setAdapter(listItemAdapter);
		list.setOnRefreshListener(this);
		list.setLastFreshTime(new Date().getTime());

		// 添加点击
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				DocInfo info = null;
				// 判断是否是TextView
				if (view instanceof TextView) {
					info = (DocInfo) view.getTag();
				} else {
					TextView tv = (TextView) view.findViewById(R.id.itemsTitle);
					info = (DocInfo) tv.getTag();
				}
				if (info == null)
					return;
				String url = "";
				url = info.u;
//					Intent intent = new Intent(YunSearchActivity.this,
//							DocWebDetailActivity.class);
//					intent.putExtra("doc_url", url);
//					intent.putExtra("docid", info._id);
//					intent.putExtra("topicid", "");
//					intent.putExtra("ui", info.ui);
//					intent.putExtra("si", info.si);
//					intent.putExtra("ch", info.ch);
//					intent.putExtra(Constants.DATA_TYPE_FLAG,
//							Constants.YUN_SEARCH_TYPE);
//					startActivity(intent);
			}
		});
	}

	@SuppressLint("NewApi")
	private void loadChannels() {
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		String loginKey = application.getProperty("loginKey");
		if (!application.isNetworkConnected()) {
			showToast("请检查网络连接");
			return;
		}
		mAbHttpUtil.get(URLs.CHANNELS + "?access_token=" + loginKey,
				new AbStringHttpResponseListener() {
					// 获取数据成功会调用这里
					@Override
					public void onSuccess(int statusCode, String content) {
						try {
							ChannelList cList = ChannelList.parseJson(content);
							if (cList.code == 200) {
								channels = cList.getInfo();
								application.saveObject((Serializable) channels,"channels");
								channel_names = new String[channels.size()];
								int i = 0;
								for (People cn : channels) {
//									channel_names[i] = cn.cn;
									i++;
								}
								initSpinner();
								oneSearch();
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

	@Override
	public void onRefresh() {
		page = 1;
		loadDocs(true, true);
	}

	@Override
	public void onLoadMore() {
		if (hasNextPage) {
			page = page + 1;
			loadDocs(false, false);
		} else {
			list.onLoadMoreComplete(true);
		}
	}

	public void loadDocs(final boolean isHansRefresh, final boolean isRefresh) {
		String loginKey = application.getProperty("loginKey");
		String keyword = keyword_input.getText().toString();
		if(!isHansRefresh&&isRefresh)
		{
			listItems.clear();
			listItemAdapter.notifyDataSetChanged();
			list.setLoading();
		}
		if ("".equals(keyword)) {
			showToast("请输入关键词");
//			list.onLoadMoreComplete(true);
			list.onRefreshComplete();
			return;
		}
		String urlString = null;
		try {
			urlString = URLs.SEARCH + "?access_token=" + loginKey
					+ "&keywords=" + URLEncoder.encode(keyword, "UTF-8")
					+ "&ch=" + channel + "&p=" + page + "&ps="
					+ Constants.PAGE_SIZE;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}

		Log.d(TAG, urlString);
		mAbHttpUtil.get(urlString, new AbStringHttpResponseListener() {
			// 获取数据成功会调用这里
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					DocInfoList docList = DocInfoList.parseJson(content);
					if (docList.code == 200) {
						//根据当前页是否满，判断是否有下一页
						if(docList.getInfo().size()<Constants.PAGE_SIZE){
							hasNextPage = false;
						}else{
							hasNextPage = true;
						}
						// 判断是否是查看更多，是的话追加
						if (isRefresh) {
							listItems.clear();
							listItems.addAll(docList.getInfo());
						} else {
							listItems.addAll(docList.getInfo());
						}
						//listItems.addAll(docList.getInfo());
						// 如果有下一页显示加载更多，没有显示加载完毕
						if (hasNextPage) {
							list.onLoadMoreComplete(false);
						} else {
							list.onLoadMoreComplete(true);
							if (listItems.size() <= 0) {
								list.changeLoadMoreViewText("暂无数据");
							}
						}
						listItemAdapter.notifyDataSetChanged();
					} else {
						if (isRefresh) {
							listItems.clear();
							listItemAdapter.notifyDataSetChanged();
						}
						showToast(docList.msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					if (isRefresh) {
						listItems.clear();
						listItemAdapter.notifyDataSetChanged();
					}
					showToast("数据解析失败");
				}
			}

			// 失败，调用
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				if (isRefresh) {
					listItems.clear();
					listItemAdapter.notifyDataSetChanged();
				}
				list.onLoadMoreComplete(false);
				showToast("网络连接失败！");
			}

			// 开始执行前
			@Override
			public void onStart() {

			}

			// 完成后调用，失败，成功
			@Override
			public void onFinish() {
				Log.d(TAG, "onFinish");
				list.onRefreshComplete();
				if (isRefresh) {
					list.setLastFreshTime(new Date().getTime());
				}
			};

		});

	}
}
