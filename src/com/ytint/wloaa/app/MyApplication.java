package com.ytint.wloaa.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.util.LruCache;
import cn.jpush.android.api.JPushInterface;
import cn.trinea.android.common.entity.CacheObject;
import cn.trinea.android.common.service.impl.ImageCache;
import cn.trinea.android.common.service.impl.ImageCache.CompressListener;
import cn.trinea.android.common.service.impl.PreloadDataCache.OnGetDataListener;
import cn.trinea.android.common.util.CacheManager;
import cn.trinea.android.common.util.FileUtils;

import com.ytint.wloaa.bean.URLs;
import com.ytint.wloaa.utils.AsynImageLoader;
import com.ytint.wloaa.utils.MethodsCompat;
import com.ytint.wloaa.utils.StringUtils;

/**
 * ȫ��Ӧ�ó����ࣺ���ڱ���͵���ȫ��Ӧ�����ü�������������
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MyApplication extends Application {
	public static final ImageCache IMAGE_CACHE = CacheManager.getImageCache();
	private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
	private static LruCache<String, Bitmap> mMemoryCache;
	private static LruCache<String, Map<String, String>> realNameCache;

	private AsynImageLoader asynImageLoader = new AsynImageLoader();

	public AsynImageLoader getImageLoader() {
		return asynImageLoader;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		if (null==getProperty("HOST")) {
			setProperty("HOST", "112.33.6.14");
//			setProperty("HOST", "windoer.com");
			setProperty("PORT", "9002");
//			URLs.HOST= getProperty("HOST");
		}else{
			System.out.println("*****"+getProperty("HOST")+":"+getProperty("PORT"));
		}
		
		IMAGE_CACHE.initData(this,"wloaa");
		IMAGE_CACHE.setOpenWaitingQueue(false);
		IMAGE_CACHE.setContext(this);
		IMAGE_CACHE.setCacheFolder(Environment.getExternalStorageDirectory().getPath()+"/wloaa/Image");
		IMAGE_CACHE.setCompressListener(new CompressListener() {
			@Override
			public int getCompressSize(String imagePath) {
				if (FileUtils.isFileExist(imagePath)) {
					long fileSize = FileUtils.getFileSize(imagePath) / 1000;
					/**
					 * if image bigger than 100k, compress to 1/(n + 1) width and 1/(n + 1) height, n is fileSize / 100k
					 **/
					if (fileSize > 100) {
						return (int)(fileSize / 100) + 1;
					}
				}
				return 1;
			}
		});
		JPushInterface.setDebugMode(false);
	    JPushInterface.init(this);
	    
	}

	/**
	 * ��������Ƿ����
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * ��ȡ��ǰ��������
	 * 
	 * @return 0��û������ 1��WIFI���� 2��WAP���� 3��NET����
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = Constants.NETTYPE_CMNET;
				} else {
					netType = Constants.NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = Constants.NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * �жϵ�ǰ�汾�Ƿ����Ŀ��汾�ķ���
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * �жϻ��������Ƿ�ɶ�
	 * 
	 * @param cachefile
	 * @return
	 */
	boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * �жϻ����Ƿ����
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * �жϻ����Ƿ�ʧЧ
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > Constants.CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * ���app����
	 */
	public void clearAppCache() {
		// ������ݻ���
		clearCacheFolder(getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(getCacheDir(), System.currentTimeMillis());
		// 2.2�汾���н�Ӧ�û���ת�Ƶ�sd���Ĺ���
		if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
			clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
					System.currentTimeMillis());
		}
	}

	/**
	 * �������Ŀ¼
	 * 
	 * @param dir
	 *            Ŀ¼
	 * @param numDays
	 *            ��ǰϵͳʱ��
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * �������浽�ڴ滺����
	 * 
	 * @param key
	 * @param value
	 */
	public void setMemCache(String key, Object value) {
		memCacheRegion.put(key, value);
	}

	/**
	 * ���ڴ滺���л�ȡ����
	 * 
	 * @param key
	 * @return
	 */
	public Object getMemCache(String key) {
		return memCacheRegion.get(key);
	}

	/**
	 * ������̻���
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ��ȡ���̻�������
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * �������
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = openFileOutput(file, MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ��ȡ����
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// �����л�ʧ�� - ɾ�������ļ�
			if (e instanceof InvalidClassException) {
				File data = getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		Properties props = getProperties();
		props.putAll(ps);
		setProps(props);
	}

	public Properties getProperties() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// ��ȡapp_configĿ¼�µ�config
			File dirConf = this.getDir(Constants.APP_CONFIG,
					Context.MODE_PRIVATE);
			fis = new FileInputStream(dirConf.getPath() + File.separator
					+ Constants.APP_CONFIG);
			props.load(fis);
		} catch (Exception e) {
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return props;
	}

	public void setProperty(String key, String value) {
		Properties props = getProperties();
		props.setProperty(key, value);
		setProps(props);
	}

	public String getProperty(String key) {
		Properties props = getProperties();
		return (props != null) ? props.getProperty(key) : null;
	}

	public void removeProperty(String... key) {
		Properties props = getProperties();
		for (String k : key)
			props.remove(k);
		setProps(props);
	}

	private void setProps(Properties p) {
		FileOutputStream fos = null;
		try {
			// ��config����(�Զ���)app_config��Ŀ¼��
			File dirConf = this.getDir(Constants.APP_CONFIG,
					Context.MODE_PRIVATE);
			File conf = new File(dirConf, Constants.APP_CONFIG);
			fos = new FileOutputStream(conf);
			p.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

/*	public HospitalList getHospitals(int page, boolean isRefresh) {
		HospitalList ul = null;
		String key = "doctors_" + page;
		// ������� �� ���治�ɶ���ˢ��
		if (isNetworkConnected() && (!isReadDataCache(key) || isRefresh)) {
			try {
				ul = ApiClient.getHospitals(this, page, Constants.PAGE_SIZE);
				if (ul != null) {
					saveObject(ul, key);
				}
			} catch (AppException e) {
				e.printStackTrace();
			}
		} else {
			ul = (HospitalList) readObject(key);
			if (ul == null)
				ul = new HospitalList();
		}
		return ul;
	}*/

}
