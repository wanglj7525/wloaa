package com.ytint.wloaa.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.ab.activity.AbActivity;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.ytint.wloaa.R;
import com.ytint.wloaa.app.MyApplication;

public class AddQiyeActivity extends AbActivity {
	String TAG = "AddXiapaiActivity";
	private MyApplication application;
	Context context = null;
	private String loginKey;
	
	private TextView textViewAddr;

	// 固定信息
	private static final LatLng GEO_ZOUPING = new LatLng(36.869669, 117.749697);
	private static final Integer ZOOM_ZOUPING = 16;

	// 地图组件
	private MapView mMapView; // MapView 是地图主控件
	private BaiduMap mBaiduMap;
	private GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private LatLng currentPt; // 当前地点击点

	// 位置信息
	private boolean hasPositionInfo = false;
	private double positionLat;
	private double positionLng;
	private String positionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("企业信息-添加企业");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		// 设置文字边距，常用来控制高度：
		mAbTitleBar.setTitleTextMargin(10, 0, 0, 0);
		// 设置标题栏背景：
		mAbTitleBar.setTitleBarBackground(R.drawable.abg_top);
		// 左边图片右边的线：
		mAbTitleBar.setLogoLine(R.drawable.aline);
		// 左边图片的点击事件：
		mAbTitleBar.getLogoView().setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		setAbContentView(R.layout.layout_addqiye);
		context = AddQiyeActivity.this;
		application = (MyApplication) this.getApplication();
		loginKey = application.getProperty("loginKey");
		
		textViewAddr = (TextView) findViewById(R.id.qiye_addr);

		addBaiduMap();// 添加百度地图 

		initUi();
		
		onAddQiyeButtonClick();
	}

	private void addBaiduMap() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mSearch = GeoCoder.newInstance();

		MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(GEO_ZOUPING);
		mBaiduMap.setMapStatus(u1);
		MapStatusUpdate u2 = MapStatusUpdateFactory.zoomTo(ZOOM_ZOUPING);
		mBaiduMap.animateMapStatus(u2);

		// 地图触屏
		mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {
			public void onTouch(MotionEvent event) {
				updateMapState();
			}
		});

		// 单击地图
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				currentPt = point;
				updateMapState();
				hasPositionInfo = false;
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(currentPt));
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});

		// 长按地图
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				currentPt = point;
				updateMapState();
			}
		});

		// 双击地图
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			public void onMapDoubleClick(LatLng point) {
				currentPt = point;
				updateMapState();
			}
		});

		// 地图状态改变
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			public void onMapStatusChangeStart(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChange(MapStatus status) {
				updateMapState();
			}
		});

		// 地理位置、地名互转
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			// 地名 -> 地理位置
			public void onGetGeoCodeResult(GeoCodeResult result) {
			}

			// 地理位置 -> 地名
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null
						|| (SearchResult.ERRORNO.NO_ERROR != result.error)) {
					Toast.makeText(AddQiyeActivity.this, "抱歉，未能找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}

				hasPositionInfo = true;
				positionLat = result.getLocation().latitude;
				positionLng = result.getLocation().longitude;
				positionName = result.getAddress();
				
				// 添加圆
				mBaiduMap.clear();
				LatLng llDot = result.getLocation();
				OverlayOptions ooDot = new DotOptions().center(llDot)
						.radius(10).color(0xFFFF0000);
				mBaiduMap.addOverlay(ooDot);
				
				if(null != textViewAddr) {
					textViewAddr.setText(positionName);
				}

				String toastStr = positionName + "\n" + "纬度:" + positionLat
						+ ", 经度:" + positionLng;
				Toast.makeText(AddQiyeActivity.this, toastStr,
						Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	private Button qiye_add;
	public void onAddQiyeButtonClick(){
		qiye_add = (Button) findViewById(R.id.qiye_add);
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.equals(qiye_add)) {
					Toast.makeText(AddQiyeActivity.this, "已添加",
							Toast.LENGTH_SHORT).show();
				} 
			}

		};
		qiye_add.setOnClickListener(onClickListener);
//		Toast.makeText(AddQiyeActivity.this, "已添加",
//				Toast.LENGTH_SHORT).show();
	}
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
	
	/**
	 * 更新地图状态显示面板
	 */
	private void updateMapState() {
		; // nothing to do
	}

	private void initUi() {
		//
		// //添加
		// add.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// // 关闭键盘
		// InputMethodManager imm = (InputMethodManager) AddQiyeActivity.this
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		//
		// submitXiaoxi();
		// }
		// });
		// // search_close.setOnClickListener(new View.OnClickListener() {
		// //
		// // @Override
		// // public void onClick(View v) {
		// // finish();
		// // }
		// // });
		//
		//
		// OnClickListener keyboard_hide = new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// InputMethodManager imm = (InputMethodManager) AddQiyeActivity.this
		// .getSystemService(Context.INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		// }
		//
		// };
		// // addxiapai_full.setClickable(true);
		// // addxiapai_full.setOnClickListener(keyboard_hide);
	}

	private void initSpinner() {
		// // 将可选内容与ArrayAdapter连接起来
		// adapter = new ArrayAdapter<String>(AddQiyeActivity.this,
		// R.layout.spinner_item, people_names);
		// // 设置下拉列表的风格
		// adapter.setDropDownViewResource(R.layout.drop_down_item);
		// // 将adapter 添加到spinner中
		// peopleSpinner.setAdapter(adapter);
		// // 设置默认选中
		// // channelSpinner.setSelection(0);
		// // 设置默认值
		// // channelSpinner.setVisibility(View.VISIBLE);
		// peopleSpinner
		// .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
		// @Override
		// public void onItemSelected(AdapterView<?> arg0, View view,
		// int arg2, long arg3) {
		// people = peoples.get(arg2).id;
		// TextView tv = (TextView) view;
		// tv.setTextColor(getResources().getColor(R.color.white)); // 设置颜色
		// tv.setGravity(android.view.Gravity.CENTER); // 设置居中
		//
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> arg0) {
		// arg0.setVisibility(View.VISIBLE);
		// }
		//
		// });

	}

	/**
	 * 下发任务
	 * 
	 * @author wlj
	 * @date 2015-6-16下午7:21:20
	 */
	private void submitXiaoxi() {
		// // 获取Http工具类
		// final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		// mAbHttpUtil.setDebug(true);
		// if (!application.isNetworkConnected()) {
		// UIHelper.ToastMessage(context, "请检查网络连接");
		// return;
		// }
		//
		// AbRequestParams params = new AbRequestParams();
		// params.put("androidNoticeInfo.title",
		// task_tell.getText().toString());
		// params.put("androidNoticeInfo.content",
		// task_info.getText().toString());
		// params.put("androidNoticeInfo.push_user_id", loginKey);
		// params.put("androidNoticeInfo.notice_type", "1");
		// params.put("androidNoticeInfo.receive_user_ids", people+"");
		// Log.d(TAG, String.format("%s?", URLs.ADDMSG,
		// params));
		// mAbHttpUtil.post(URLs.ADDMSG ,params,
		// new AbStringHttpResponseListener() {
		// @Override
		// public void onSuccess(int statusCode, String content) {
		// Log.d(TAG, content);
		// try {
		// QunfaInfo gList = QunfaInfo
		// .parseJson(content);
		// if (gList.code == 200) {
		// showToast("任务下发成功！");
		// finish();
		// } else {
		// UIHelper.ToastMessage(context, gList.msg);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// UIHelper.ToastMessage(context, "数据解析失败");
		// }
		// }
		//
		// @Override
		// public void onFailure(int statusCode, String content,
		// Throwable error) {
		// UIHelper.ToastMessage(context, "网络连接失败！");
		// }
		//
		// @Override
		// public void onStart() {
		// showProgressDialog(null);
		// }
		//
		// // 完成后调用
		// @Override
		// public void onFinish() {
		// finish();
		// };
		// });
	}

	@SuppressLint("NewApi")
	private void loadPeoples() {
		//
		// final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(this);
		// if (!application.isNetworkConnected()) {
		// showToast("请检查网络连接");
		// return;
		// }
		// mAbHttpUtil.get(URLs.USERLIST ,
		// new AbStringHttpResponseListener() {
		// // 获取数据成功会调用这里
		// @Override
		// public void onSuccess(int statusCode, String content) {
		// try {
		// PeopleList cList = PeopleList.parseJson(content);
		// if (cList.code == 200) {
		// peoples = cList.getInfo();
		// peoples.remove(Integer.parseInt(loginKey)-1);
		// application.saveObject((Serializable) peoples,"peoples");
		// people_names = new String[peoples.size()];
		// int i = 0;
		// for (People cn : peoples) {
		// people_names[i] = cn.name;
		// i++;
		// }
		// initSpinner();
		// } else {
		// showToast(cList.msg);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// showToast("数据解析失败");
		// }
		// };
		//
		// // 开始执行前
		// @Override
		// public void onStart() {
		// // 显示进度框
		// showProgressDialog();
		// }
		//
		// @Override
		// public void onFailure(int statusCode, String content,
		// Throwable error) {
		// showToast("网络连接失败！");
		// }
		//
		// // 完成后调用，失败，成功
		// @Override
		// public void onFinish() {
		// // 移除进度框
		// removeProgressDialog();
		// };
		//
		// });
	}

}