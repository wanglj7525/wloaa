package com.ytint.wloaa.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.ytint.wloaa.R;

public class ChatActivity extends TabActivity{

//	int[] faceId={R.drawable.f_static_000,R.drawable.f_static_001,R.drawable.f_static_002,R.drawable.f_static_003
//			,R.drawable.f_static_004,R.drawable.f_static_005,R.drawable.f_static_006,R.drawable.f_static_009,R.drawable.f_static_010,R.drawable.f_static_011
//			,R.drawable.f_static_012,R.drawable.f_static_013,R.drawable.f_static_014,R.drawable.f_static_015,R.drawable.f_static_017,R.drawable.f_static_018};
//	String[] faceName={"\\����","\\����","\\����","\\͵Ц","\\�ټ�","\\�ô�","\\����","\\����","\\����","\\С��","\\�ſ�","\\����"
//			 ,"\\ί��","\\���","\\�˵�","\\΢Ц","\\ɫɫ","\\����"};
	
//	HashMap<String,Integer> faceMap=null;
	ArrayList<HashMap<String,Object>> chatList=null;
	String[] from={"image","text"};
	int[] to={R.id.chatlist_image_me,R.id.chatlist_text_me,R.id.chatlist_image_other,R.id.chatlist_text_other};
	int[] layout={R.layout.chat_listitem_me,R.layout.chat_listitem_other};
	String userQQ=null;
	/**
	 * �������������ļ�ʹ����ͬһ��id������һ���Ƿ����
	 * TT��ʵ֤����ز���id��ƥ���쳣�����Ի���Ҫ�ֿ�����
	 * 
	 * userQQ���ڽ���Intent���ݵ�qq�ţ����������������ݿ��е���ص���ϵ����Ϣ�������Ȳ���
	 * ����ʱʹ��һ��ͷ��
	 */
	
	public final static int OTHER=1;
	public final static int ME=0;
	public final static int ActivityID=0;
	
	
	protected ListView chatListView=null;
	protected Button chatSendButton=null;
	protected EditText editText=null;

//	protected ImageButton chatBottomLook=null;
	protected RelativeLayout faceLayout=null;
	protected TabHost tabHost=null;
	protected TabWidget tabWidget=null;

   
	private boolean expanded=false;

	protected MyChatAdapter adapter=null;
	protected View tabFaceHistory=null,tabFace=null;
	protected ImageView tabFaceHistoryImage=null,tabFaceImage=null;
	public static Handler chatHandler=null;
	public static String currentTabTag="face";
	public TabSpec tabSpecFaceHistory,tabSpecFace;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.layout_collection_source);
	
		chatHandler=new MyChatHandler(Looper.myLooper());
		
//		faceMap=new HashMap<String,Integer>();	
		chatList=new ArrayList<HashMap<String,Object>>();
		
        
		
		addTextToList("��������˭", ME);
		addTextToList("Ⱥ�����Ҳ���\n  ^_^", OTHER);
		addTextToList("��������", ME);
		addTextToList("������֣�", OTHER);
		
		chatSendButton=(Button)findViewById(R.id.chat_bottom_sendbutton);
		editText=(EditText)findViewById(R.id.chat_bottom_edittext);
		chatListView=(ListView)findViewById(R.id.chat_list);
		tabWidget=(TabWidget)findViewById(android.R.id.tabs);
		tabHost=(TabHost)findViewById(android.R.id.tabhost);

		
//		chatBottomLook=(ImageButton)findViewById(R.id.chat_bottom_look);
		faceLayout=(RelativeLayout)findViewById(R.id.faceLayout);

 
		
		/**
		 * ���ѡ�
		 */
//		tabSpecFaceHistory=tabHost.newTabSpec("faceHistory");
//		tabFaceHistory=LayoutInflater.from(this).inflate(R.layout.tabwidget_image_disselected,null);
//		tabFaceHistoryImage=(ImageView)tabFaceHistory.findViewById(R.id.tabImage_disselected);
//		tabFaceHistoryImage.setImageResource(R.drawable.face_history_disselected);
//		tabSpecFaceHistory.setIndicator(tabFaceHistory);
//		Intent intent1=new Intent();
//		intent1.setClass(ChatActivity.this, FaceHistoryActivity.class);
//		tabSpecFaceHistory.setContent(intent1);
//		tabHost.addTab(tabSpecFaceHistory);
//		
//		tabSpecFace=tabHost.newTabSpec("face");
//		tabFace=LayoutInflater.from(this).inflate(R.layout.tabwidget_image_selected, null);
//		tabFaceImage=(ImageView)tabFace.findViewById(R.id.tabImage_selected);
//		tabFaceImage.setImageResource(R.drawable.face_look_selected);
//		tabSpecFace.setIndicator(tabFace);
//		Intent intent2=new Intent();
//		intent2.setClass(ChatActivity.this, MyFaceActivity.class);
//		tabSpecFace.setContent(intent2);
//		tabHost.addTab(tabSpecFace);
//		
//		tabHost.setCurrentTabByTag("face");     
//		tabHost.setOnTabChangedListener(new OnTabChangeListener() {
//			
//			@Override
//			public void onTabChanged(String tabId) {
//				// TODO Auto-generated method stub
//			//	System.out.println("current Selected Tab "+tabId);
//				currentTabTag=tabId;
//	
//				
//				
//				if(tabId.equals("face")){
//					
//					
//				 					
//					tabFace.setBackgroundResource(R.drawable.tabwidget_selected);
//					tabFaceImage.setImageResource(R.drawable.face_look_selected);
//					tabSpecFace.setIndicator(tabFace);
//					
//					
//					tabFaceHistory.setBackgroundResource(R.drawable.tab_widget_disselected);
//					tabFaceHistoryImage.setImageResource(R.drawable.face_history_disselected);
//					tabSpecFaceHistory.setIndicator(tabFaceHistory);
//				}
//				else if(tabId.equals("faceHistory")){
//					
//					tabFace.setBackgroundResource(R.drawable.tabwidget_disselected);
//					tabFaceImage.setImageResource(R.drawable.face_look_disselected);
//					tabSpecFace.setIndicator(tabFace);
//					
//					tabFaceHistory.setBackgroundResource(R.drawable.tabwidget_selected);
//					tabFaceHistoryImage.setImageResource(R.drawable.face_history_selected);
//					tabSpecFaceHistory.setIndicator(tabFaceHistory);
//					
//				}
//				
//				
//			}
//		});
//		
		//TODO ԭ���ı��鰴ť ���Ը�Ϊ��ϵ��
//		chatBottomLook.setOnClickListener(new OnClickListener(){
//  
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if(expanded){
//					setFaceLayoutExpandState(false);
//					expanded=false;
//					
//					
//					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
//					imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
//
//					/**height����Ϊ0����Ϊ��ϣ������ʹ�ٴδ�ʱviewFlipper�Ѿ���ʼ��Ϊ��һҳ ����
//					*�ٴδ�ViewFlipperʱ�����ڶ��Ľ��,
//					*Ϊ�˱�����Ϊ1dip�ĸ߶Ȳ���һ���׷죬����������ViewFlipper���ڵ�RelativeLayout
//					*�����������һ��1dip�ߵĺ�ɫɫ��
//					*/
//					
//					
//				}
//				else{
//
//					setFaceLayoutExpandState(true);  
//					expanded=true;
//				    
//
//				}
//			}
//			
//		});
		
		/**EditText��δ��ý��㵽�״λ�ý���ʱ�������OnClickListener����������Ӧ�øĳ�OnTouchListener
		 * �Ӷ���֤��EditText��һ�¾��ܹ��ѱ������ر�
		editText.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewGroup.LayoutParams params=viewFlipper.getLayoutParams();
				params.height=0;
				viewFlipper.setLayoutParams(params);
				expanded=false;
				System.out.println("WHYWHWYWHYW is Clicked");
			}
			
		});
		**/
		editText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(expanded){
					
					setFaceLayoutExpandState(false);
					expanded=false;
				}
				return false;
			}
		});
		adapter=new MyChatAdapter(this,chatList,layout,from,to);			
		chatSendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String myWord=null;
				
				/**
				 * ����һ��������Ϣ�ļ�������ע������ı�����û�����ݣ���ôgetText()�ķ���ֵ����Ϊ
				 * null����ʱ����toString()�����쳣��������������ں������һ��""��ʽת����Stringʵ��
				 * �����Ҳ��ܷ��Ϳ���Ϣ��
				 */
				
				myWord=(editText.getText()+"").toString();
				if(myWord.length()==0)
					return;
				editText.setText("");
				addTextToList(myWord, ME);
				/**
				 * ���������б�����ͨ��setSelection����ʹListViewʼ�չ�������׶�
				 */
				adapter.notifyDataSetChanged();
				chatListView.setSelection(chatList.size()-1);
				
			} 
		});
		
		chatListView.setAdapter(adapter);
		
		chatListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				setFaceLayoutExpandState(false);
				((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).
				hideSoftInputFromWindow(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			    expanded=false;
			}
		});
		
//		/**
//		 * Ϊ����Map�������
//		 */
//		for(int i=0; i<faceId.length; i++){
//			faceMap.put(faceName[i], faceId[i]);
//		}
		
		
		
		
		
	}
	
	
	
	public class MyChatHandler extends Handler{
		
		public MyChatHandler(Looper looper){
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
//			switch(msg.what){
//			case MyFaceActivity.ActivityId:
//				if(msg.arg1==0){            //��ӱ����ַ���
//					editText.append(msg.obj.toString());
//				}
//			
//			}
		}
		
		
		
	}
	
	
	/**
	 * �򿪻��߹ر�����̣�֮ǰ���򿪣����ø÷�����رգ�֮ǰ���رգ����ø÷������
	 */
	
	private void setSoftInputState(){
		((InputMethodManager)ChatActivity.this.getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void setFaceLayoutExpandState(boolean isexpand){
		if(isexpand==false){

				
			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
			params.height=1;
			faceLayout.setLayoutParams(params);	
			
//			chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_look);
			Message msg=new Message();
			msg.what=this.ActivityID;
			msg.obj="collapse";
//			if(MyFaceActivity.faceHandler!=null)
//				MyFaceActivity.faceHandler.sendMessage(msg);
			
			Message msg2=new Message();
			msg2.what=this.ActivityID;
			msg2.obj="collapse";
//			if(FaceHistoryActivity.faceHistoryHandler!=null)
//				FaceHistoryActivity.faceHistoryHandler.sendMessage(msg2);
	
			chatListView.setSelection(chatList.size()-1);//ʹ�Ự�б��Զ���������Ͷ�
			
		}
		else{
			
			((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow
			(ChatActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			ViewGroup.LayoutParams params=faceLayout.getLayoutParams();
			params.height=185;
		//	faceLayout.setLayoutParams(new RelativeLayout.LayoutParams( ));    
		    RelativeLayout.LayoutParams relativeParams=new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		    relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			faceLayout.setLayoutParams(relativeParams);
		    
		    
//			chatBottomLook.setBackgroundResource(R.drawable.chat_bottom_keyboard);

		}
	}
	
	
	
	
	
	
	
	
	private void setFaceText(TextView textView,String text){
		SpannableString spanStr=parseString(text);
		textView.setText(spanStr);
	}
	
//	private void setFace(SpannableStringBuilder spb, String faceName){
//		Integer faceId=faceMap.get(faceName);
//		if(faceId!=null){
//			Bitmap bitmap=BitmapFactory.decodeResource(getResources(), faceId);
//			bitmap=Bitmap.createScaledBitmap(bitmap, 30, 30, true);
//			ImageSpan imageSpan=new ImageSpan(this,bitmap);
//			SpannableString spanStr=new SpannableString(faceName);
//			spanStr.setSpan(imageSpan, 0, faceName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//			spb.append(spanStr);	
//		}
//		else{
//			spb.append(faceName);
//		}
//		
//	}
	
	private SpannableString parseString(String inputStr){
		SpannableStringBuilder spb=new SpannableStringBuilder();
		Pattern mPattern= Pattern.compile("\\\\..");
		Matcher mMatcher=mPattern.matcher(inputStr);
		String tempStr=inputStr;
		
		while(mMatcher.find()){
			int start=mMatcher.start();
			int end=mMatcher.end();
			spb.append(tempStr.substring(0,start));
//			String faceName=mMatcher.group();
//			setFace(spb, faceName);
			tempStr=tempStr.substring(end, tempStr.length());
			/**
			 * ���²��ҵ��ַ���
			 */
			mMatcher.reset(tempStr);
		}
		spb.append(tempStr);
		return new SpannableString(spb);
	}
	
	
	
	protected void addTextToList(String text, int who){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("person",who );
		map.put("image", who==ME?R.drawable.contact_0:R.drawable.contact_1);
		map.put("text", text);
		chatList.add(map);
	}
	
	private class MyChatAdapter extends BaseAdapter{

		Context context=null;
		ArrayList<HashMap<String,Object>> chatList=null;
		int[] layout;
		String[] from;
		int[] to;
		  
		
		
		public MyChatAdapter(Context context,
				ArrayList<HashMap<String, Object>> chatList, int[] layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return chatList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder{
			public ImageView imageView=null;
			public TextView textView=null;
		
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;   
			int who=(Integer)chatList.get(position).get("person");
 
				convertView= LayoutInflater.from(context).inflate(
						layout[who==ME?0:1], null);
				holder=new ViewHolder();
				holder.imageView=(ImageView)convertView.findViewById(to[who*2+0]);
				holder.textView=(TextView)convertView.findViewById(to[who*2+1]);
			
			
			holder.imageView.setBackgroundResource((Integer)chatList.get(position).get(from[0]));
			setFaceText(holder.textView, chatList.get(position).get(from[1]).toString());
			return convertView;
		}
		
	}
	
	

}
