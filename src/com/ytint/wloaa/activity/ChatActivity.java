//package com.ytint.wloaa.activity;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import android.app.TabActivity;
//import android.content.Context;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.SpannableString;
//import android.text.SpannableStringBuilder;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
//import android.view.Window;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TabHost;
//import android.widget.TabHost.TabSpec;
//import android.widget.TabWidget;
//import android.widget.TextView;
//
//import com.ytint.wloaa.R;
//
//public class ChatActivity extends TabActivity{
//
////	int[] faceId={R.drawable.f_static_000,R.drawable.f_static_001,R.drawable.f_static_002,R.drawable.f_static_003
////			,R.drawable.f_static_004,R.drawable.f_static_005,R.drawable.f_static_006,R.drawable.f_static_009,R.drawable.f_static_010,R.drawable.f_static_011
////			,R.drawable.f_static_012,R.drawable.f_static_013,R.drawable.f_static_014,R.drawable.f_static_015,R.drawable.f_static_017,R.drawable.f_static_018};
////	String[] faceName={"\\呲牙","\\淘气","\\流汗","\\偷笑","\\再见","\\敲打","\\擦汗","\\流泪","\\掉泪","\\小声","\\炫酷","\\发狂"
////			 ,"\\委屈","\\便便","\\菜刀","\\微笑","\\色色","\\害羞"};
//	
////	HashMap<String,Integer> faceMap=null;
//	ArrayList<HashMap<String,Object>> chatList=null;
//	String[] from={"image","text"};
//	int[] to={R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_image_other,R.id.chatlist_text_other};
//	int[] layout={R.layout.chat_listitem_me,R.layout.chat_listitem_other};
//	String userQQ=null;
//	/**
//	 * 这里两个布局文件使用了同一个id，测试一下是否管用
//	 * TT事实证明这回产生id的匹配异常！所以还是要分开。。
//	 * 
//	 * userQQ用于接收Intent传递的qq号，进而用来调用数据库中的相关的联系人信息，这里先不讲
//	 * 先暂时使用一个头像
//	 */
//	
//	public final static int OTHER=1;
//	public final static int ME=0;
//	public final static int ActivityID=0;
//	
//	
//	protected ListView chatListView=null;
//	protected Button chatSendButton=null;
//	protected EditText editText=null;
//
////	protected ImageButton chatBottomLook=null;
//	protected RelativeLayout faceLayout=null;
//	protected TabHost tabHost=null;
//	protected TabWidget tabWidget=null;
//
//   
//	private boolean expanded=false;
//
//	protected MyChatAdapter adapter=null;
//	protected View tabFaceHistory=null,tabFace=null;
//	protected ImageView tabFaceHistoryImage=null,tabFaceImage=null;
//	public static Handler chatHandler=null;
//	public static String currentTabTag="face";
//	public TabSpec tabSpecFaceHistory,tabSpecFace;
//	
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.layout_collection_source);
//	
//		chatHandler=new MyChatHandler(Looper.myLooper());
//		
////		faceMap=new HashMap<String,Integer>();	
//		chatList=new ArrayList<HashMap<String,Object>>();
//		
//        
//		
//		addTextToList("不管你是谁", ME);
//		addTextToList("群发的我不回\n  ^_^", OTHER);
//		addTextToList("哈哈哈哈", ME);
//		addTextToList("新年快乐！", OTHER);
//		
//		chatSendButton=(Button)findViewById(R.id.chat_bottom_sendbutton);
//		editText=(EditText)findViewById(R.id.chat_bottom_edittext);
//		chatListView=(ListView)findViewById(R.id.chat_list);
//		tabWidget=(TabWidget)findViewById(android.R.id.tabs);
//		tabHost=(TabHost)findViewById(android.R.id.tabhost);
//
//		
////		chatBottomLook=(ImageButton)findViewById(R.id.chat_bottom_look);
//		faceLayout=(RelativeLayout)findViewById(R.id.faceLayout);
//
// 
//		
//		/**
//		 * 添加选项卡
//		 */
////		tabSpecFaceHistory=tabHost.newTabSpec("faceHistory");
////		tabFaceHistory=LayoutInflater.from(this).inflate(R.layout.tabwidget_image_disselected,null);
////		tabFaceHistoryImage=(ImageView)tabFaceHistory.findViewById(R.id.tabImage_disselected);
////		tabFaceHistoryImage.setImageResource(R.drawable.face_history_disselected);
////		tabSpecFaceHistory.setIndicator(tabFaceHistory);
////		Intent intent1=new Intent();
////		intent1.setClass(ChatActivity.this, FaceHistoryActivity.class);
////		tabSpecFaceHistory.setContent(intent1);
////		tabHost.addTab(tabSpecFaceHistory);
////		
////		tabSpecFace=tabHost.newTabSpec("face");
////		tabFace=LayoutInflater.from(this).inflate(R.layout.tabwidget_image_selected, null);
////		tabFaceImage=(ImageView)tabFace.findViewById(R.id.tabImage_selected);
////		tabFaceImage.setImageResource(R.drawable.face_look_selected);
////		tabSpecFace.setIndicator(tabFace);
////		Intent intent2=new Intent();
////		intent2.setClass(ChatActivity.this, MyFaceActivity.class);
////		tabSpecFace.setContent(intent2);
////		tabHost.addTab(tabSpecFace);
////		
////		tabHost.setCurrentTabByTag("face");     
////		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
////			
////			@Override
////			public void onTabChanged(String tabId) {
////				// TODO Auto-generated method stub
////			//	System.out.println("current Selected Tab "+tabId);
////				currentTabTag=tabId;
////	
////				
////				
////				if(tabId.equals("face")){
////					
////					
////				 					
////					tabFace.setBackgroundResource(R.drawable.tabwidget_selected);
////					tabFaceImage.setImageResource(R.drawable.face_look_selected);
////					tabSpecFace.setIndicator(tabFace);
////					
////					
////					tabFaceHistory.setBackgroundResource(R.drawable.tab_widget_disselected);
////					tabFaceHistoryImage.setImageResource(R.drawable.face_history_disselected);
////					tabSpecFaceHistory.setIndicator(tabFaceHistory);
////				}
////				else if(tabId.equals("faceHistory")){
////					
////					tabFace.setBackgroundResource(R.drawable.tabwidget_disselected);
////					tabFaceImage.setImageResource(R.drawable.face_look_disselected);
////					tabSpecFace.setIndicator(tabFace);
////					
////					tabFaceHistory.setBackgroundResource(R.drawable.tabwidget_selected);
////					tabFaceHistoryImage.setImageResource(R.drawable.face_history_selected);
////					tabSpecFaceHistory.setIndicator(tabFaceHistory);
////					
////				}
////				
////				
////			}
////		});
////		
//		//TODO 原来的表情按钮 可以改为联系人
////		chatBottomLook.setOnClickListener(new OnClickListener(){
////  
////			@Override
////			public void onClick(View v) {
////				// TODO Auto-generated method stub
////				if(expanded){
////					setFaceLayoutExpandState(false);
////					expanded=false;
////					
////					
////					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
////					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
////
////					/**height不设为0是因为，希望可以使再次打开时viewFlipper已经初始化为第一页 避免
////					*再次打开ViewFlipper时画面在动的结果,
////					*为了避免因为1dip的高度产生一个白缝，所以这里在ViewFlipper所在的RelativeLayout
////					*最上面添加了一个1dip高的黑色色块
////					*/
////					
////					
////				}
////				else{
////
////					setFaceLayoutExpandState(true);  
////					expanded=true;
////				    
////
////				}
////			}
////			
////		});
//		
//		/**EditText从未获得焦点到首次获得焦点时不会调用OnClickListener方法，所以应该改成OnTouchListener
//		 * 从而保证点EditText第一下就能够把表情界面关闭
//		editText.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				ViewGroup.LayoutParams params=viewFlipper.getLayoutParams();
//				params.height=0;
//				viewFlipper.setLayoutParams(params);
//				expanded=false;
//				System.out.println("WHYWHWYWHYW is Clicked");
//			}
//			
//		});
//		**/
//		editText.setOnTouchListener(new OnTouchListener() {
//			
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				if(expanded){
//					
//					setFaceLayoutExpandState(false);
//					expanded=false;
//				}
//				return false;
//			}
//		});
//		adapter=new MyChatAdapter(this,chatList,layout,from,to);			
//		chatSendButton.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				String myWord=null;
//				
//				/**
//				 * 这是一个发送消息的监听器，注意如果文本框中没有内容，那么getText()的返回值可能为
//				 * null，这时调用toString()会有异常！所以这里必须在后面加上一个""隐式转换成String实例
//				 * ，并且不能发送空消息。
//				 */
//				
//				myWord=(editText.getText()+"").toString();
//				if(myWord.length()==0)
//					return;
//				editText.setText("");
//				addTextToList(myWord, ME);
//				/**
//				 * 更新数据列表，并且通过setSelection方法使ListView始终滚动在最底端
//				 */
//				adapter.notifyDataSetChanged();
//				chatListView.setSelection(chatList.size()-1);
//				
//			} 
//		});
//		
//		chatListView.setAdapter(adapter);
//		
//		chatListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				setFaceLayoutExpandState(false);
//				((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).
//				hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//			    expanded=false;
//			}
//		});
//		
////		/**
////		 * 为表情Map添加数据
////		 */
////		for(int i=0; i<faceId.length; i++){
////			faceMap.put(faceName[i], faceId[i]);
////		}
//		
//		
//		
//		
//		
//	}
//	
//	
//	
//	public class MyChatHandler extends Handler{
//		
//		public MyChatHandler(Looper looper){
//			super(looper);
//		}
//
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
////			switch(msg.what){
////			case MyFaceActivity.ActivityId:
////				if(msg.arg1==0){            //添加表情字符串
////					editText.append(msg.obj.toString());
////				}
////			
////			}
//		}
//		
//		
//		
//	}
//	
//	
//	/**
//	 * 打开或者关闭软键盘，之前若打开，调用该方法后关闭；之前若关闭，调用该方法后打开
//	 */
//	
//	private void setSoftInputState(){
//		((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//	}
//	
//	private void setFaceLayoutExpandState(boolean isexpand){
//		if(isexpand==false){
//
//				
//			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
//			params.height=1;
//			faceLayout.setLayoutParams(params);	
//			
////			chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_look);
//			Message msg=new Message();
//			msg.what=this.ActivityID;
//			msg.obj="collapse";
////			if(MyFaceActivity.faceHandler!=null)
////				MyFaceActivity.faceHandler.sendMessage(msg);
//			
//			Message msg2=new Message();
//			msg2.what=this.ActivityID;
//			msg2.obj="collapse";
////			if(FaceHistoryActivity.faceHistoryHandler!=null)
////				FaceHistoryActivity.faceHistoryHandler.sendMessage(msg2);
//	
//			chatListView.setSelection(chatList.size()-1);//使会话列表自动滑动到最低端
//			
//		}
//		else{
//			
//			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
//			(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
//			params.height=185;
//		//	faceLayout.setLayoutParams(new RelativeLayout.LayoutParams( ));    
//		    RelativeLayout.LayoutParams relativeParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//		    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//			faceLayout.setLayoutParams(relativeParams);
//		    
//		    
////			chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_keyboard);
//
//		}
//	}
//	
//	
//	
//	
//	
//	
//	
//	
//	private void setFaceText(TextView textView,String text){
//		SpannableString spanStr=parseString(text);
//		textView.setText(spanStr);
//	}
//	
////	private void setFace(SpannableStringBuilder spb, String faceName){
////		Integer faceId=faceMap.get(faceName);
////		if(faceId!=null){
////			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), faceId);
////			bitmap=Bitmap.createScaledBitmap(bitmap, 30, 30, true);
////			ImageSpan imageSpan=new ImageSpan(this,bitmap);
////			SpannableString spanStr=new SpannableString(faceName);
////			spanStr.setSpan(imageSpan, 0, faceName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
////			spb.append(spanStr);	
////		}
////		else{
////			spb.append(faceName);
////		}
////		
////	}
//	
//	private SpannableString parseString(String inputStr){
//		SpannableStringBuilder spb=new SpannableStringBuilder();
//		Pattern mPattern= Pattern.compile("\\\\..");
//		Matcher mMatcher=mPattern.matcher(inputStr);
//		String tempStr=inputStr;
//		
//		while(mMatcher.find()){
//			int start=mMatcher.start();
//			int end=mMatcher.end();
//			spb.append(tempStr.substring(0,start));
////			String faceName=mMatcher.group();
////			setFace(spb, faceName);
//			tempStr=tempStr.substring(end, tempStr.length());
//			/**
//			 * 更新查找的字符串
//			 */
//			mMatcher.reset(tempStr);
//		}
//		spb.append(tempStr);
//		return new SpannableString(spb);
//	}
//	
//	
//	
//	protected void addTextToList(String text, int who){
//		HashMap<String,Object> map=new HashMap<String,Object>();
//		map.put("person",who );
//		map.put("image", who==ME?R.drawable.contact_0:R.drawable.contact_1);
//		map.put("text", text);
//		chatList.add(map);
//	}
//	
//	private class MyChatAdapter extends BaseAdapter{
//
//		Context context=null;
//		ArrayList<HashMap<String,Object>> chatList=null;
//		int[] layout;
//		String[] from;
//		int[] to;
//		  
//		
//		
//		public MyChatAdapter(Context context,
//				ArrayList<HashMap<String, Object>> chatList, int[] layout,
//				String[] from, int[] to) {
//			super();
//			this.context = context;
//			this.chatList = chatList;
//			this.layout = layout;
//			this.from = from;
//			this.to = to;
//		}
//
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return chatList.size();
//		}
//
//		@Override
//		public Object getItem(int arg0) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			// TODO Auto-generated method stub
//			return position;
//		}
//
//		class ViewHolder{
//			public ImageView imageView=null;
//			public TextView textView=null;
//		
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			ViewHolder holder=null;   
//			int who=(Integer)chatList.get(position).get("person");
// 
//				convertView= LayoutInflater.from(context).inflate(
//						layout[who==ME?0:1], null);
//				holder=new ViewHolder();
//				holder.imageView=(ImageView)convertView.findViewById(to[who*2+0]);
//				holder.textView=(TextView)convertView.findViewById(to[who*2+1]);
//			
//			
//			holder.imageView.setBackgroundResource((Integer)chatList.get(position).get(from[0]));
//			setFaceText(holder.textView, chatList.get(position).get(from[1]).toString());
//			return convertView;
//		}
//		
//	}
//	
//	
//
//}
