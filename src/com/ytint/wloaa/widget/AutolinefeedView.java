package com.ytint.wloaa.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class AutolinefeedView extends ViewGroup {
	
	private int mleft = 0 ;
	private int mRight = 0 ;
	private int VIEW_MARGIN = 0;
	private int maxLine = Integer.MAX_VALUE ;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!isEnabled()) {
			return true ;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int count = getChildCount();
		int maxHeight = 0;
		int maxWidth = 0;
		int lengthX=mleft;
		int row=0;

		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != GONE) {
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
				int width = child.getMeasuredWidth();
				int height = child.getMeasuredHeight();
				maxWidth = Math.max(maxWidth, width);
				maxHeight= Math.max(maxHeight, height+VIEW_MARGIN);
				if (mRight != 0) {
					lengthX+=width+VIEW_MARGIN;
					maxHeight=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height;
					//if it can't drawing on a same line , skip to next line
					if (width >= mRight) {
						width = mRight - 2*VIEW_MARGIN ;
					}
					if(lengthX>mRight){
						lengthX=width+VIEW_MARGIN+mleft;
						row++;
						if (row >= maxLine) {
							break ;
						}
						maxHeight=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height;
					}
				}
			}
		}

		// Check against our minimum height and width
		maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
		maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

		// Check against our foreground's minimum height and width
		setMeasuredDimension(resolveSize(maxWidth, widthMeasureSpec),
				resolveSize(maxHeight, heightMeasureSpec));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		this.mleft = left ;
		this.mRight = right ;
		final int count = getChildCount();
		int row=0;// which row lay you view relative to parent
		int lengthX=left;    // right position of child relative to parent
		int lengthY=top;    // bottom position of child relative to parent
		for(int i=0;i<count;i++){

			final View child = this.getChildAt(i);
			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight() ;
			if (width >= right) {
				width = right - 2*VIEW_MARGIN ;
			}
			lengthX+=width+VIEW_MARGIN;
			lengthY=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height+top;
			//if it can't drawing on a same line , skip to next line
			if(lengthX>right){
				lengthX=width+VIEW_MARGIN+left;
				row++;
				lengthY=row*(height+VIEW_MARGIN)+VIEW_MARGIN+height+top;
			}
			if (row >= maxLine) {
				break ;
			}
			child.layout(lengthX-width, lengthY-height, lengthX, lengthY);
		}

	}
	
	public AutolinefeedView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		float density = getResources().getDisplayMetrics().density ;
		VIEW_MARGIN = dip2px(2, density);
	}

	public AutolinefeedView(Context context, AttributeSet attrs) {
		super(context, attrs);
		float density = getResources().getDisplayMetrics().density ;
		VIEW_MARGIN = dip2px(2, density);
	}

	public AutolinefeedView(Context context) {
		super(context);
		float density = getResources().getDisplayMetrics().density ;
		VIEW_MARGIN = dip2px(2, density);
	}
	
	private int dip2px(float dipValue, float scale) {
		return (int) (dipValue * scale + 0.5f);
	}
	
}
