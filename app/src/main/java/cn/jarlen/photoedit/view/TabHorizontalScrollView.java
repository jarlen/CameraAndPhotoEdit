package cn.jarlen.photoedit.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jarlen.photoedit.R;

public class TabHorizontalScrollView extends HorizontalScrollView
{

	private Context mContext;
	/**
	 * view的高度
	 */
	private int mViewHeight = 0;

	/**
	 * Tab 标签文字颜色
	 */
	private int mTabTextColor = 0xFFBDBDC5;

	/**
	 * Tab 标签文字大小
	 */
	private int mTabTextSize = 0;

	/**
	 * Tab 选中标签文字颜色
	 */
	private int mTabPressTextColor = 0xFFFFFFFF;

	/**
	 * 下划线的颜色
	 */
	private int mUnderlineColor = 0xFFEFEFF0;

	/**
	 * tab下划线高度
	 */
	private int underlineHeight = 0;

	/**
	 * tab下划线宽度偏移
	 */
	private int underlineWidthOffset = 0;

	/**
	 * 下划线绘制笔
	 */
	private Paint mLinePaint;

	/**
	 * 下划线切换动画有关
	 */
	private float detalLeft = 0;
	private float detalRight = 0;
	private float minDetal = 2.05f;

	private int mTabPadding = 0;
	// private int tabBackgroundResId = R.drawable.background_tab;

	private boolean shouldExpand = false;

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	/**
	 * 标签容器
	 */
	private LinearLayout tabsContainer;

	/**
	 * 当前标签下标
	 */
	private int currentPosition = 0;

	/**
	 * 标签数据
	 */
	private ArrayList<String> mTabList = null;

	/**
	 * 标签个数
	 */
	private int mTabCount = 0;

	public TabHorizontalScrollView(Context context, AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public TabHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle)
	{
		super(context, attrs, defStyle);
		this.mContext = context;

		initParameter();

		// initTabData();
	}

	/**
	 * 初始化绘制所需工具
	 */
	private void initParameter()
	{

		tabsContainer = new LinearLayout(mContext);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);

		tabsContainer.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		addView(tabsContainer);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);

		mTabTextSize = (int) mContext.getResources().getDimension(
				R.dimen.tab_text_size);
		mTabPadding = (int) mContext.getResources().getDimension(
				R.dimen.tab_padding);
		underlineHeight = (int) mContext.getResources().getDimension(
				R.dimen.tab_underline_eight);

		underlineWidthOffset = (int) mContext.getResources().getDimension(
				R.dimen.tab_underline_widthoffset);

		mLinePaint = new Paint();
		mLinePaint.setColor(mUnderlineColor);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setStyle(Style.FILL);
	}

	public ArrayList<String> getTabList()
	{
		return mTabList;
	}

	public void setTabList(ArrayList<String> mTabList)
	{
		this.mTabList = mTabList;
	}

	/**
	 * 清除TabText数据
	 */
	public void clearTabText()
	{
		tabsContainer.removeAllViews();
		mTabList.clear();
		mTabCount = 0;
		currentPosition = 0;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);

		mViewHeight = getHeight();
	}

	public void setCurrentPosition(int position)
	{
		currentPosition = position;

		if (mOnTabItemClickListener != null)
		{
			mOnTabItemClickListener.onClickTabItem(position);
		}
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		/** 绘制tab标签下划线 **/
		View currentTab = tabsContainer.getChildAt(currentPosition);
		if (currentTab != null)
		{
			float lineLeft = currentTab.getLeft();
			canvas.drawRect(lineLeft - detalLeft + underlineWidthOffset,
					mViewHeight - underlineHeight,
					lineLeft + currentTab.getWidth() - detalRight
							- underlineWidthOffset, mViewHeight, mLinePaint);
		}

	}

	/**
	 * 设置标签数据
	 * 
	 * @param tabList
	 */
	public void setTabData(ArrayList<String> tabList)
	{
		if (mTabList != null && !mTabList.isEmpty())
		{
			mTabList.clear();
		} else
		{
			mTabList = new ArrayList<String>();
		}

		tabsContainer.removeAllViews();
		mTabCount = tabList.size();

		for (int index = 0; index < mTabCount; index++)
		{
			String str = tabList.get(index);
			mTabList.add(str);
			addTextTab(index, str);
			invalidate();
		}

	}

	/**
	 * 设置标签数据
	 * 
	 * @param tabList
	 */
	public void setTabData(String[] tabList)
	{
		if (mTabList != null && !mTabList.isEmpty())
		{
			mTabList.clear();
		} else
		{
			mTabList = new ArrayList<String>();
		}

		tabsContainer.removeAllViews();
		mTabCount = tabList.length;

		for (int index = 0; index < mTabCount; index++)
		{
			String str = tabList[index];
			mTabList.add(str);
			addTextTab(index, str);
			invalidate();
		}
	}

	/**
	 * 添加单个标签
	 * 
	 * @param tabStr
	 */
	public void addTabView(String tabStr)
	{
		mTabList.add(tabStr);
		mTabCount++;
		addTextTab(mTabCount - 1, mTabList.get(mTabCount - 1));
		invalidate();
	}

	/**
	 * 向容器中添加标签view
	 * 
	 * @param position
	 * @param title
	 */
	private void addTextTab(final int position, String title)
	{
		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		if (position == currentPosition)
		{
			tab.setTextColor(mTabPressTextColor);
		} else
		{
			tab.setTextColor(mTabTextColor);
		}

		tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
		addTab(position, tab);
	}

	/**
	 * 获取tab标签个数
	 * 
	 * @return
	 */
	public int getTabCount()
	{
		return mTabCount;
	}

	/**
	 * 向容器中添加标签view
	 * 
	 * @param position
	 * @param tab
	 */
	private void addTab(final int position, final View tab)
	{
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				scrollLineToCurrentPosition(currentPosition, position);
				if (mOnTabItemClickListener != null)
				{
					mOnTabItemClickListener.onClickTabItem(position);
				}
			}
		});

		tab.setPadding(mTabPadding, 0, mTabPadding, 0);
		tabsContainer.addView(tab, position, shouldExpand
				? expandedTabLayoutParams
				: defaultTabLayoutParams);
	}

	/**
	 * 模拟动画滚动下划线
	 * 
	 * @param fromPosition
	 * @param toPosition
	 */
	private void scrollLineToCurrentPosition(int fromPosition, int toPosition)
	{
		TextView lastTab = (TextView) tabsContainer.getChildAt(fromPosition);
		lastTab.setTextColor(mTabTextColor);

		TextView currentTab = (TextView) tabsContainer.getChildAt(toPosition);
		currentTab.setTextColor(mTabPressTextColor);

		currentPosition = toPosition;

		float lineLeft = currentTab.getLeft();
		float lineRight = currentTab.getRight();

		detalLeft = lineLeft - lastTab.getLeft();
		detalRight = lineRight - lastTab.getRight();

		this.post(new Runnable()
		{
			@Override
			public void run()
			{

				if (Math.abs(detalLeft) > minDetal
						|| Math.abs(detalRight) > minDetal)
				{

					if (Math.abs(detalLeft) > minDetal)
					{
						detalLeft = detalLeft / minDetal;
					}

					if (Math.abs(detalRight) > minDetal)
					{
						detalRight = detalRight / minDetal;
					}
					invalidate();
					TabHorizontalScrollView.this.post(this);
				} else
				{
					invalidate();
				}
			}
		});
	}

	/**
	 * 标签监听事件
	 */
	private OnTabItemClickListener mOnTabItemClickListener;

	/**
	 * 绑定标签切换监听事件
	 * 
	 * @param listener
	 */
	public void setOnTabItemClickListener(OnTabItemClickListener listener)
	{
		mOnTabItemClickListener = listener;
	}

	/**
	 * 标签监听类
	 * 
	 * @author jarlen
	 * 
	 */
	public interface OnTabItemClickListener
	{
		public void onClickTabItem(int value);
	}
	
	public void recycle()
	{
		mLinePaint = null;
		defaultTabLayoutParams = null;
		expandedTabLayoutParams = null;
		tabsContainer = null;
		
		if(mTabList != null)
		{
			mTabList.clear();
			mTabList = null;
		}
		
		mOnTabItemClickListener = null;
		mContext = null;
	}

}
