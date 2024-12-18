package cn.jarlen.photoedit.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import cn.jarlen.photoedit.R;

/**
 * 头像图片选择框的浮层
 * 
 * @author jarlen
 * 
 */
public class FloatDrawable extends Drawable
{

	private Context mContext;
	private Drawable mCropPointDrawable;
	private Paint mLinePaint = new Paint();
	{
		mLinePaint.setColor(Color.parseColor("#CCFFFFFF"));
		mLinePaint.setStrokeWidth(2F);
		mLinePaint.setStyle(Paint.Style.STROKE);
		mLinePaint.setAntiAlias(true);
		mLinePaint.setColor(Color.WHITE);
	}

	public FloatDrawable(Context context)
	{
		super();
		this.mContext = context;
		init();
	}

	private void init()
	{
		mCropPointDrawable = mContext.getResources().getDrawable(
				R.drawable.clip_point);
	}

	public int getCirleWidth()
	{
		return mCropPointDrawable.getIntrinsicWidth();
	}

	public int getCirleHeight()
	{
		return mCropPointDrawable.getIntrinsicHeight();
	}

	@Override
	public void draw(Canvas canvas)
	{

		int left = getBounds().left;
		int top = getBounds().top;
		int right = getBounds().right;
		int bottom = getBounds().bottom;

		Rect mRect = new Rect(
				left + mCropPointDrawable.getIntrinsicWidth() / 2, top
						+ mCropPointDrawable.getIntrinsicHeight() / 2, right
						- mCropPointDrawable.getIntrinsicWidth() / 2, bottom
						- mCropPointDrawable.getIntrinsicHeight() / 2);
		// 方框
		canvas.drawRect(mRect, mLinePaint);

	}

	@Override
	public void setBounds(Rect bounds)
	{
		super.setBounds(new Rect(bounds.left
				- mCropPointDrawable.getIntrinsicWidth() / 2, bounds.top
				- mCropPointDrawable.getIntrinsicHeight() / 2, bounds.right
				+ mCropPointDrawable.getIntrinsicWidth() / 2, bounds.bottom
				+ mCropPointDrawable.getIntrinsicHeight() / 2));
	}

	@Override
	public void setAlpha(int alpha)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setColorFilter(ColorFilter cf)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getOpacity()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void recycle()
	{
		mContext = null;
		mCropPointDrawable = null;
		mLinePaint = null;
	}

}
