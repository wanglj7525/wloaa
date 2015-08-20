package com.ytint.wloaa.fragment;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbStringHttpResponseListener;
import com.ytint.wloaa.R;
import com.ytint.wloaa.activity.LoginActivity;
import com.ytint.wloaa.activity.PassWordUpdateActivity;
import com.ytint.wloaa.app.Constants;
import com.ytint.wloaa.app.MyApplication;
import com.ytint.wloaa.app.UIHelper;
import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.bean.Version;
import com.ytint.wloaa.bean.VersionInfo;
import com.ytint.wloaa.service.AppUpgradeService;

public class ShezhiFragment extends Fragment {
	private Button button1;
	private Button button2;
	private Button button3;
	
	private int mVersionCode;
	private String mVersionName;
	private int mLatestVersionCode = 1;
	private String mLatestVersionUpdate = "mLatestVersionUpdate";
	private String mLatestVersionDownload = "";
	
	private MyApplication application;
	public ShezhiFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_shezhi, container, false);
        findView(rootView);
        application = (MyApplication) getActivity().getApplication();
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
		button3=(Button)rootView.findViewById(R.id.checkNew);
		button3.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				initLocalVersion();
				getNewVersion();
			}
		});
	}
	public void initLocalVersion() {
		PackageInfo pinfo;
		try {
			pinfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
			mVersionCode = pinfo.versionCode;
			mVersionName = pinfo.versionName;
			Log.e("mVersionCode:", String.valueOf(mVersionCode));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取版本信息
	 */
	public void getNewVersion() {
		final AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(getActivity());
		if (!application.isNetworkConnected()) {
			UIHelper.ToastMessage(getActivity(), "请检查网络连接");
			return;
		}

		mAbHttpUtil.get(URLs.GETVERSION + "?id="+mVersionCode , new AbStringHttpResponseListener() {
			@Override
			public void onSuccess(int statusCode, String content) {
				System.out.println(content);
				try {
					VersionInfo vi = VersionInfo.parseJson(content);
					if (vi.code == 200) {
						Version v = vi.getInfo();
						mLatestVersionCode = v.id;
						mLatestVersionUpdate = "";
						for (String str : v.introduce) {
							mLatestVersionUpdate += str + "<br>";
						}
						mLatestVersionDownload = v.download_address;
						checkNewVersion();
					} else {
						UIHelper.ToastMessage(getActivity(), vi.msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
					UIHelper.ToastMessage(getActivity(), "数据解析失败");
				}
			}

			@Override
			public void onFailure(int statusCode, String content,
					Throwable error) {
				UIHelper.ToastMessage(getActivity(), "网络连接失败！");
			}

			@Override
			public void onStart() {
			}

			// 完成后调用
			@Override
			public void onFinish() {
			};
		});
	}

	/**
	 * 检查版本
	 */
	public void checkNewVersion() {
		if (mVersionCode < mLatestVersionCode) {
			new AlertDialog.Builder(getActivity())
					.setTitle(R.string.check_new_version)
					.setMessage(Html.fromHtml(mLatestVersionUpdate))
					.setPositiveButton(R.string.app_upgrade_confirm,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											getActivity(),
											AppUpgradeService.class);
									intent.putExtra("downloadUrl",
											mLatestVersionDownload);
									getActivity().startService(intent);
								}
							})
					.setNegativeButton(R.string.app_upgrade_cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
								}
							}).create().show();
		}

		if (mVersionCode >= mLatestVersionCode) {
			Toast.makeText(getActivity(), R.string.check_new_version_latest,
					Toast.LENGTH_SHORT).show();
			File updateFile = new File(Environment
					.getExternalStorageDirectory().getPath()
					+ Constants.DOWNLOADPATH, getResources().getString(
					R.string.app_name)
					+ ".apk");
			if (updateFile.exists()) {
				// 当不需要的时候，清除之前的下载文件，避免浪费用户空间
				updateFile.delete();
			}
		}
	}
}