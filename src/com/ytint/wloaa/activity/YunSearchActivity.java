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
 * ������������ҳ��
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
		// ����ѡ������ArrayAdapter��������
		adapter = new ArrayAdapter<String>(YunSearchActivity.this,
				R.layout.spinner_item, channel_names);
		// ���������б�ķ��
		adapter.setDropDownViewResource(R.layout.drop_down_item);
		// ��adapter ��ӵ�spinner��
		channelSpinner.setAdapter(adapter);
		// ����Ĭ��ѡ��
//		channelSpinner.setSelection(0);
		// ����Ĭ��ֵ
//		channelSpinner.setVisibility(View.VISIBLE);
		channelSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> arg0, View view,
							int arg2, long arg3) {
//						channel = channels.get(arg2).cid;
						TextView tv = (TextView) view;
						tv.setTextColor(getResources().getColor(R.color.white)); // ������ɫ
						tv.setGravity(android.view.Gravity.CENTER); // ���þ���

						// ѡ��ͨ���������������������������������
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
		// ͨ�����سɹ�����������
		Intent intent = getIntent();
		String keyword = intent.getStringExtra("SearchWord");
		if (keyword != null && !keyword.equals("")) {
			keyword_input.setText(keyword);
//			list.setLoading();
			//list.onLoadMoreComplete(true);
			loadDocs(false, true);
		} else {
			list.onLoadMoreComplete(true);
			list.changeLoadMoreViewText("��������");
		}
		// ͨ�����سɹ���������������
	}

	private void initUi() {
		channelSpinner = (Spinner) findViewById(R.id.channel_spinner);
		yun_search = (ImageView) findViewById(R.id.yun_search);
		keyword_input = (EditText) findViewById(R.id.keyword_input);
		search_close = (TextView) findViewById(R.id.search_close);
		yunsearch_full = (LinearLayout) findViewById(R.id.yunsearch_full);
		config_head = (TextView) findViewById(R.id.config_head);
		list = (DragListView) findViewById(R.id.draglistview); // ��Layout�����ListView

		config_head.setFocusable(true);
		config_head.setFocusableInTouchMode(true);
		config_head.requestFocus(); // ��ʼ����EditText�ý���
		config_head.requestFocusFromTouch();
		yun_search.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// �رռ���
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
		// ������������Item�Ͷ�̬�����Ӧ��Ԫ��
		listItemAdapter = new DocListViewAdapter(this, listItems);

		// ��Ӳ�����ʾ
		list.setAdapter(listItemAdapter);
		list.setOnRefreshListener(this);
		list.setLastFreshTime(new Date().getTime());

		// ��ӵ��
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int arg2,
					long arg3) {
				DocInfo info = null;
				// �ж��Ƿ���TextView
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
			showToast("������������");
			return;
		}
		mAbHttpUtil.get(URLs.CHANNELS + "?access_token=" + loginKey,
				new AbStringHttpResponseListener() {
					// ��ȡ���ݳɹ����������
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
							showToast("���ݽ���ʧ��");
						}
					};

					// ��ʼִ��ǰ
					@Override
					public void onStart() {
						// ��ʾ���ȿ�
						showProgressDialog();
					}

					@Override
					public void onFailure(int statusCode, String content,
							Throwable error) {
						showToast("��������ʧ�ܣ�");
					}

					// ��ɺ���ã�ʧ�ܣ��ɹ�
					@Override
					public void onFinish() {
						// �Ƴ����ȿ�
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
			showToast("������ؼ���");
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
			// ��ȡ���ݳɹ����������
			@Override
			public void onSuccess(int statusCode, String content) {
				try {
					DocInfoList docList = DocInfoList.parseJson(content);
					if (docList.code == 200) {
						//���ݵ�ǰҳ�Ƿ������ж��Ƿ�����һҳ
						if(docList.getInfo().size()<Constants.PAGE_SIZE){
							hasNextPage = false;
						}else{
							hasNextPage = true;
						}
						// �ж��Ƿ��ǲ鿴���࣬�ǵĻ�׷��
						if (isRefresh) {
							listItems.clear();
							listItems.addAll(docList.getInfo());
						} else {
							listItems.addAll(docList.getInfo());
						}
						//listItems.addAll(docList.getInfo());
						// �������һҳ��ʾ���ظ��࣬û����ʾ�������
						if (hasNextPage) {
							list.onLoadMoreComplete(false);
						} else {
							list.onLoadMoreComplete(true);
							if (listItems.size() <= 0) {
								list.changeLoadMoreViewText("��������");
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
					showToast("���ݽ���ʧ��");
				}
			}

			// ʧ�ܣ�����
			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				if (isRefresh) {
					listItems.clear();
					listItemAdapter.notifyDataSetChanged();
				}
				list.onLoadMoreComplete(false);
				showToast("��������ʧ�ܣ�");
			}

			// ��ʼִ��ǰ
			@Override
			public void onStart() {

			}

			// ��ɺ���ã�ʧ�ܣ��ɹ�
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
