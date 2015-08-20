package com.ytint.wloaa.widget;


import java.text.SimpleDateFormat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ytint.wloaa.R;

/***
 * �Զ�������ListView
 * 
 * @author zhang
 * 
 */
public class DragListView extends ListView implements OnScrollListener{
	// ����ListViewö������״̬
	private enum DListViewState {
		LV_NORMAL, // ��ͨ״̬
		LV_PULL_REFRESH, // ����״̬��Ϊ����mHeadViewHeight��
		LV_RELEASE_REFRESH, // �ɿ���ˢ��״̬������mHeadViewHeight��
		LV_LOADING;// ����״̬
	}

	// ������ظ���ö������״̬
	private enum DListViewLoadingMore {
		LV_NORMAL, // ��ͨ״̬
		LV_LOADING, // ����״̬
		LV_OVER; // ����״̬
	}

	public View mHeadView;// ͷ��headView
	private TextView mRefreshTextview; // ˢ��msg��mHeadView��
	private TextView mLastUpdateTextView;// �����¼���mHeadView��
	private ImageView mArrowImageView;// ����ͼ�꣨mHeadView��
	private ProgressBar mHeadProgressBar;// ˢ�½����壨mHeadView��

	private int mHeadViewWidth; // headView�Ŀ��mHeadView��
	private int mHeadViewHeight;// headView�ĸߣ�mHeadView��

	public View mFootView;// β��mFootView
	private View mLoadMoreView;// mFootView ��view(mFootView)
	private TextView mLoadMoreTextView;// ���ظ���.(mFootView)
	private View mLoadingView;// ������...View(mFootView)

	private Animation animation, reverseAnimation;// ��ת��������ת����֮����ת����.

	private int mFirstItemIndex = -1;// ��ǰ��ͼ�ܿ����ĵ�һ���������

	// ���ڱ�֤startY��ֵ��һ��������touch�¼���ֻ����¼һ��
	private boolean mIsRecord = false;

	private int mStartY, mMoveY;// �����ǵ�y����,moveʱ��y����

	private DListViewState mlistViewState = DListViewState.LV_NORMAL;// ����״̬.(�Զ���ö��)

	private DListViewLoadingMore loadingMoreState = DListViewLoadingMore.LV_NORMAL;// ���ظ���Ĭ��״̬.

	private final static int RATIO = 2;// �������������.

	private boolean mBack = false;// headView�Ƿ���.

	private OnRefreshLoadingMoreListener onRefreshLoadingMoreListener;// ����ˢ�½ӿڣ��Զ��壩

	private boolean isScroller = true;// �Ƿ�����ListView������
	String time = null;// ����ʱ��

	public DragListView(Context context) {
		super(context, null);
		initDragListView(context);
	}

	public DragListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initDragListView(context);
	}

	// ע������ˢ�½ӿ�
	public void setOnRefreshListener(
			OnRefreshLoadingMoreListener onRefreshLoadingMoreListener) {
		this.onRefreshLoadingMoreListener = onRefreshLoadingMoreListener;
	}

	/***
	 * ��ʼ��ListView
	 */
	public void initDragListView(Context context) {
		
		initHeadView(context);// ��ʼ����head.

		initLoadMoreView(context);// ��ʼ��footer

		setOnScrollListener(this);// ListView��������
	}

	/***
	 * ��ʼ��ͷ��HeadView
	 * @param context
	 *            ������
	 *            �ϴθ���ʱ��
	 */
	public void initHeadView(Context context) {
		mHeadView = LayoutInflater.from(context).inflate(R.layout.head, null);
		mArrowImageView = (ImageView) mHeadView
				.findViewById(R.id.head_arrowImageView);
		mArrowImageView.setMinimumWidth(60);

		mHeadProgressBar = (ProgressBar) mHeadView
				.findViewById(R.id.head_progressBar);

		mRefreshTextview = (TextView) mHeadView
				.findViewById(R.id.head_tipsTextView);

		mLastUpdateTextView = (TextView) mHeadView
				.findViewById(R.id.head_lastUpdatedTextView);
		// ��ʾ�����¼�
		mLastUpdateTextView.setText("�������:" + time);

		measureView(mHeadView);
		// ��ȡ��͸�
		mHeadViewWidth = mHeadView.getMeasuredWidth();
		mHeadViewHeight = mHeadView.getMeasuredHeight();

		addHeaderView(mHeadView, null, false);// ����ʼ�õ�ListView add����קListView
		// ����������Ҫ����headView���õ���������ʾλ��.
		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);

		initAnimation();// ��ʼ������
	}

	/***
	 * ��ʼ���ײ����ظ���ؼ�
	 */
	private void initLoadMoreView(Context context) {
		mFootView = LayoutInflater.from(context).inflate(R.layout.footer, null);

		mLoadMoreView = mFootView.findViewById(R.id.load_more_view);

		mLoadMoreTextView = (TextView) mFootView
				.findViewById(R.id.load_more_tv);

		mLoadingView = (LinearLayout) mFootView
				.findViewById(R.id.loading_layout);

		//mLoadMoreView.setOnClickListener(this);	
		mLoadingView.setVisibility(View.GONE);
		mLoadMoreTextView.setVisibility(View.GONE);
		//loadingMoreState=DListViewLoadingMore.LV_LOADING;
		addFooterView(mFootView);
		updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
		
	}
	public void changeLoadMoreViewText(String footString)
	{
		mLoadMoreTextView.setText(footString);
	}

	/***
	 * ��ʼ������
	 */
	private void initAnimation() {
		// ��ת����
		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());// ����
		animation.setDuration(250);
		animation.setFillAfter(true);// ͣ�������״̬.
		// ������ת����
		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(250);
		reverseAnimation.setFillAfter(true);
	}

	/***
	 * �趨�������ʱ��
	 */	
	public void setLastFreshTime(long lastTime)
	{
		SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //��ʽ����ǰϵͳ����				      	
	      time = dateFm.format(lastTime);
	}
	/***
	 * ���ã����� headView�Ŀ�͸�.
	 * 
	 * @param child
	 */
	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/***
	 * touch �¼�����
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		// ����
		case MotionEvent.ACTION_DOWN:
			doActionDown(ev);
			break;
		// �ƶ�
		case MotionEvent.ACTION_MOVE:
			doActionMove(ev);
			break;
		// ̧��
		case MotionEvent.ACTION_UP:
			doActionUp(ev);
			break;
		default:
			break;
		}
		/***
		 * �����ListView�������������ô����true������ListView�������϶�.
		 * �������ListView����������ô���ø��෽���������Ϳ�������ִ��.
		 */
		if (isScroller) {
			return super.onTouchEvent(ev);
		} else {
			return true;
		}

	}

	/***
	 * ���²���
	 * 
	 * ���ã���ȡ�����ǵ�y����
	 * 
	 * @param event
	 */
	void doActionDown(MotionEvent event) {
		if (mFirstItemIndex == 0) {//mIsRecord == false && 
			mStartY = (int) event.getY();
			mIsRecord = true;
		}
	}

	/*** 
     * ��ק�ƶ����� 
     *  
     * @param event 
     */  
    void doActionMove(MotionEvent event) {  
        mMoveY = (int) event.getY();// ��ȡʵʱ����y����  
        // ����Ƿ���һ��touch�¼�.  
        if (mIsRecord == false && mFirstItemIndex == 0) {  
            mStartY = (int) event.getY();  
            mIsRecord = true;  
        }  
        /*** 
         * ���touch�رջ���������Loading״̬�Ļ� return. 
         */  
        if (mIsRecord == false || mlistViewState == DListViewState.LV_LOADING) {  
            return;  
        }  
        // ������headview�ƶ�����Ϊy�ƶ���һ��.���Ƚ��Ѻã�  
        int offset = (mMoveY - mStartY) / RATIO;  
  
        switch (mlistViewState) {  
        // ��ͨ״̬  
        case LV_NORMAL: {  
            // ���<0������ζ���ϻ���.  
            if (offset > 0) {  
                // ����headView��padding����.  
                mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);  
                switchViewState(DListViewState.LV_PULL_REFRESH);// ����״̬  
            }  
  
        }  
            break;  
        // ����״̬  
        case LV_PULL_REFRESH: {  
            setSelection(0);// ʱʱ�����ڶ���.  
            // ����headView��padding����.  
            mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);  
            if (offset < 0) {  
                /*** 
                 * Ҫ����ΪʲôisScroller = false; 
                 */  
                isScroller = false;  
                switchViewState(DListViewState.LV_NORMAL);// ��ͨ״̬  
                Log.e("jj", "isScroller=" + isScroller);  
            } else if (offset > mHeadViewHeight) {// ���������offset����headView�ĸ߶���Ҫִ��ˢ��.  
                switchViewState(DListViewState.LV_RELEASE_REFRESH);// ����Ϊ��ˢ�µ�����״̬.  
            }  
        }  
            break;  
        // ��ˢ��״̬  
        case LV_RELEASE_REFRESH: {  
            setSelection(0);//ʱʱ�����ڶ���  
            // ����headView��padding����.  
            mHeadView.setPadding(0, offset - mHeadViewHeight, 0, 0);  
            // ����offset>0������û�г���headView�ĸ߶�.��ôҪgoback ԭװ.  
            if (offset >= 0 && offset <= mHeadViewHeight) {  
                mBack = true;  
                switchViewState(DListViewState.LV_PULL_REFRESH);  
            } else if (offset < 0) {  
                switchViewState(DListViewState.LV_NORMAL);  
            } else {  
  
            }  
        }  
            break;  
        default:  
            return;  
        }  
         
    }  
	/***
	 * ����̧�����
	 * 
	 * @param event
	 */
	public void doActionUp(MotionEvent event) {
		mIsRecord = false;// ��ʱ��touch�¼���ϣ�Ҫ�رա�
		isScroller = true;// ListView����Scrooler����.
		mBack = false;
		// �������״̬����loading״̬.
		if (mlistViewState == DListViewState.LV_LOADING) {
			return;
		}
		// ������Ӧ״̬.
		switch (mlistViewState) {
		// ��ͨ״̬
		case LV_NORMAL:

			break;
		// ����״̬
		case LV_PULL_REFRESH:
			mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
			switchViewState(mlistViewState.LV_NORMAL);
			break;
		// ˢ��״̬
		case LV_RELEASE_REFRESH:
			mHeadView.setPadding(0, 0, 0, 0);
			switchViewState(mlistViewState.LV_LOADING);
			onRefresh();// ����ˢ��
			break;
		}

	}

	// �л�headview��ͼ
	private void switchViewState(DListViewState state) {

		switch (state) {
		// ��ͨ״̬
		case LV_NORMAL: {
			mArrowImageView.clearAnimation();// �������
			mArrowImageView.setImageResource(R.drawable.arrow);
		}
			break;
		// ����״̬
		case LV_PULL_REFRESH: {
			mHeadProgressBar.setVisibility(View.GONE);// ���ؽ�����
			mArrowImageView.setVisibility(View.VISIBLE);// ����ͼ��
			mRefreshTextview.setText("��������ˢ��");
			mLastUpdateTextView.setText("�������:" + time);
			mArrowImageView.clearAnimation();// �������

			// ���п�ˢ��״̬��LV_RELEASE_REFRESH��תΪ���״̬��ִ�У���ʵ��������������������ִ��.
			if (mBack) {
				mBack = false;
				mArrowImageView.clearAnimation();// �������
				mArrowImageView.startAnimation(reverseAnimation);// �����ת����
			}
		}
			break;
		// �ɿ�ˢ��״̬
		case LV_RELEASE_REFRESH: {
			mHeadProgressBar.setVisibility(View.GONE);// ���ؽ�����
			mArrowImageView.setVisibility(View.VISIBLE);// ��ʾ����ͼ��
			mRefreshTextview.setText("�ɿ���ȡ����");
			mLastUpdateTextView.setText("�������:" + time);
			mArrowImageView.clearAnimation();// �������
			mArrowImageView.startAnimation(animation);// �������
		}
			break;
		// ����״̬
		case LV_LOADING: {
			Log.e("!!!!!!!!!!!", "convert to IListViewState.LVS_LOADING");
			mHeadProgressBar.setVisibility(View.VISIBLE);
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mRefreshTextview.setText("������...");
		}
			break;
		default:
			return;
		}
		// �мǲ�Ҫ����ʱʱ����״̬��
		mlistViewState = state;

	}
	
	/**
	 * ��ʾ���ڼ���
	 */
	public void showLoading(){
        setSelection(0);
		mHeadView.setPadding(0, 0, 0, 0);
		mHeadProgressBar.setVisibility(View.VISIBLE);
		mArrowImageView.clearAnimation();
		mArrowImageView.setVisibility(View.GONE);
		mRefreshTextview.setText("������...");
		mLastUpdateTextView.setText("�������:" + time);
		mlistViewState = DListViewState.LV_LOADING;
	}

	/***
	 * ����ˢ��
	 */
	private void onRefresh() {
		if (onRefreshLoadingMoreListener != null) {
			onRefreshLoadingMoreListener.onRefresh();
		}
	}

	/***
	 * ����ˢ�����
	 */
	public void onRefreshComplete() {
		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);// �ع�.
		switchViewState(mlistViewState.LV_NORMAL);//
	}

	/***
	 * ������ظ���
	 * 
	 * @param flag
	 *            �����Ƿ���ȫ���������
	 * true��������ϣ�false�����ظ���
	 */
	public void onLoadMoreComplete(boolean flag) {
		if (flag) {
			updateLoadMoreViewState(DListViewLoadingMore.LV_OVER);
		} else {
			updateLoadMoreViewState(DListViewLoadingMore.LV_NORMAL);
		}

	}
	
	public void setLoading(){
		updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
	}

	// ����Footview��ͼ
	private void updateLoadMoreViewState(DListViewLoadingMore state) {
		switch (state) {
		// ��ͨ״̬
		case LV_NORMAL:
			mLoadingView.setVisibility(View.GONE);
			mLoadMoreTextView.setVisibility(View.VISIBLE);
			mLoadMoreTextView.setText("�鿴����");
			break;
		// ������״̬
		case LV_LOADING:
			mLoadingView.setVisibility(View.VISIBLE);
			mLoadMoreTextView.setVisibility(View.GONE);
			break;
		// �������״̬
		case LV_OVER:
			mLoadingView.setVisibility(View.GONE);
			mLoadMoreTextView.setVisibility(View.VISIBLE);
			mLoadMoreTextView.setText("�������");
			break;
		default:
			break;
		}
		loadingMoreState = state;
	}

	/***
	 * ListView ��������
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		Log.d("view.getLastVisiblePosition()",String.valueOf(view.getLastVisiblePosition()));
		Log.d("view.getCount() -1",String.valueOf(view.getCount() -1));
		if(view.getLastVisiblePosition() == view.getCount()-1
				&&loadingMoreState!=DListViewLoadingMore.LV_LOADING)
		{
			updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
			onRefreshLoadingMoreListener.onLoadMore();// �����ṩ�������ظ���.
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstItemIndex = firstVisibleItem;
	}

	/***
	 * �ײ�����¼�
	 */
/*	@Override
	public void onClick(View v) {
		// ��ֹ�ظ����
		if (onRefreshLoadingMoreListener != null
				&& loadingMoreState == DListViewLoadingMore.LV_NORMAL) {
			updateLoadMoreViewState(DListViewLoadingMore.LV_LOADING);
			onRefreshLoadingMoreListener.onLoadMore();// �����ṩ�������ظ���.
		}

	}*/

	/***
	 * �Զ���ӿ�
	 */
	public interface OnRefreshLoadingMoreListener {
		/***
		 * // ����ˢ��ִ��
		 */
		void onRefresh();

		/***
		 * ������ظ���
		 */
		void onLoadMore();
	}

}
