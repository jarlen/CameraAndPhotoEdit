package cn.jarlen.photoedit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class ShopGridView extends GridView
{
	public ShopGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public ShopGridView(Context context)
	{
		super(context);
	}

	public ShopGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
