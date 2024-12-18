package cn.jarlen.photoedit.crop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.util.ViewUtil;

/**
 * 浮层不动 ，图片可动
 * <p>
 * 可改变浮层大小(含动画),即剪切比例
 *
 * @author jarlen
 */
public class CropImageSampleView extends View {

    /* 单点触摸的时候 */
    private float oldX = 0;
    private float oldY = 0;

    /**
     * 多点触摸的时候
     */
    private float oldx_0 = 0;
    private float oldy_0 = 0;

    private float oldx_1 = 0;
    private float oldy_1 = 0;

    /**
     * 操作状态
     */
    private final int STATUS_Touch_SINGLE = 1; // 单点
    private final int STATUS_TOUCH_MULTI_START = 2; // 多点开始
    private final int STATUS_TOUCH_MULTI_TOUCHING = 3; // 多点拖拽中

    private int mStatus = STATUS_Touch_SINGLE;

    /**
     * 默认的裁剪图片宽度与高度 1:1
     */
    private int cropWidth = 0;
    private int cropHeight = 0;

    /**
     * 原始宽高比率
     */
    private float oriRationWH = 0;

    /**
     * 最大扩大到多少倍
     */
    private float maxZoomOut = 3.0f;

    /**
     * 最小缩小到多少倍
     */
    private float minZoomIn = 1f;

    /**
     * 原图
     */
    private Drawable mDrawable;
    private Bitmap mBitmapSrc;
    private Bitmap bitmapSrcTemp;

    private int rotateDegree = 0;
    ;

    /**
     * 浮层
     */
    private FloatDrawable mFloatDrawable;

    private Rect mDrawableSrc = new Rect();

    /**
     * 原图区域
     */
    private Rect mDrawableDst = new Rect();

    /**
     * 浮层区域框
     */
    private Rect mDrawableFloat = new Rect();

    /**
     * 是否是第一次创建
     */
    private boolean isFrist = true;

    /**
     * 屏幕宽度
     */
    private int mScreenWidth = 0;

    /**
     * 屏幕高度
     */
    private int mScreenHeight = 0;

    /**
     * 动画有关
     */
    private float mixValue = 1.25f;
    private int leftDelta = 0;
    private int topDelta = 0;
    private int rightdeta = 0;
    private int bottomDeta = 0;

    /**
     * 动画开关，默认是打开
     */
    private Boolean animationOn = true;

    /**
     * 正在改变界面
     */
    private Boolean isChanging = false;

    protected Context mContext;

    private int floatViewTop = 0;

    /**
     * 剪切最小的剪切的限制宽度
     */
    private final int MaxCropWidth = 1080;// px

    public CropImageSampleView(Context context) {
        this(context, null);
    }

    public CropImageSampleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageSampleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        float value = context.getResources().getDisplayMetrics().density * 0.75f;
        this.mixValue = Math.max(1.25f, value);
        init(context);
        floatViewTop = ViewUtil.dipTopx(context, (int) context.getResources()
                .getDimension(R.dimen.carmera_imagecrop_float_top));
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        mScreenWidth = wm.getDefaultDisplay().getWidth();
        mScreenHeight = wm.getDefaultDisplay().getHeight();

        try {
            if (android.os.Build.VERSION.SDK_INT >= 11) {
                this.setLayerType(LAYER_TYPE_SOFTWARE, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mFloatDrawable = new FloatDrawable(context);
    }

    /**
     * 设置图片缩放的最大比例
     *
     * @param maxZoomOut 缩放最大比例
     */
    public void setMaxZoomOut(float maxZoomOut) {
        this.maxZoomOut = maxZoomOut;
    }

    public void rotateBGDrawable(int degrees) {
        if (isChanging) {
            return;
        }

        isChanging = true;
        rotateDegree += degrees;
        rotateDegree = rotateDegree % 360;

        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);

        if (bitmapSrcTemp != null) {
            bitmapSrcTemp = null;
        }

        Bitmap bit = Bitmap.createBitmap(mBitmapSrc, 0, 0,
                mBitmapSrc.getWidth(), mBitmapSrc.getHeight(), matrix, true);

        bitmapSrcTemp = bit;

        if (mDrawable != null) {
            mDrawable = null;
        }

        BitmapDrawable drawable = new BitmapDrawable(mContext.getResources(),
                bitmapSrcTemp);

        this.mDrawable = drawable;

        isFrist = true;
        isChanging = false;
        invalidate();
    }

    /**
     * 设置图片缩放的最小比例 (默认 1.0为原图)
     *
     * @param minZoomIn 缩放最小比例
     */
    public void setMinZoomIn(float minZoomIn) {
        this.minZoomIn = minZoomIn;
    }

    /**
     * 设置需要剪切的图片 默认浮层剪切比例为 1:1，宽度为屏幕宽度
     *
     * @param mDrawable 图片
     */
    public void setBGDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
        this.isFrist = true;
        invalidate();
    }

    /**
     * 设置需要剪切的图片 默认浮层剪切比例为 1:1，宽度为屏幕宽度
     *
     * @param bitmapSrc
     */
    public void setBGBitmap(Bitmap bitmapSrc) {

        this.cropWidth = ViewUtil.px2dip(mContext, mScreenWidth);
        this.cropHeight = ViewUtil.px2dip(mContext, mScreenWidth);

        if (bitmapSrc.getWidth() < mScreenWidth
                || bitmapSrc.getHeight() < mScreenWidth) {
            float scaleWidth = mScreenWidth / (float) bitmapSrc.getWidth();
            float scaleHeight = mScreenWidth / (float) bitmapSrc.getHeight();

            float scale = scaleWidth > scaleHeight ? scaleWidth : scaleHeight;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            this.bitmapSrcTemp = Bitmap.createBitmap(bitmapSrc, 0, 0,
                    bitmapSrc.getWidth(), bitmapSrc.getHeight(), matrix, true);

        } else {

            this.bitmapSrcTemp = bitmapSrc;

        }

        this.mBitmapSrc = bitmapSrc;

        BitmapDrawable bd = new BitmapDrawable(mContext.getResources(),
                bitmapSrcTemp);

        setBGDrawable(bd);
    }

    /**
     * 设置剪切比例；默认的情况下是1:1，宽度为屏幕宽度
     *
     * @param scaleWidth  剪切宽度
     * @param scaleHeight 剪切高度
     */
    public void setCropScale(int scaleWidth, int scaleHeight) {
        if (isChanging) {
            return;
        }
        this.cropWidth = ViewUtil.px2dip(mContext, mScreenWidth);
        this.cropHeight = this.cropWidth * scaleHeight / scaleWidth;
        // this.isFrist = true;

        if (this.mBitmapSrc.getWidth() < ViewUtil.dipTopx(mContext, cropWidth)
                || this.mBitmapSrc.getHeight() < ViewUtil.dipTopx(mContext,
                cropHeight)) {
            float scaleW = ViewUtil.dipTopx(mContext, cropWidth)
                    / (float) this.mBitmapSrc.getWidth();
            float scaleH = ViewUtil.dipTopx(mContext, cropHeight)
                    / (float) this.mBitmapSrc.getHeight();

            float scale = scaleW > scaleH ? scaleW : scaleH;

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);
            this.bitmapSrcTemp = Bitmap
                    .createBitmap(this.mBitmapSrc, 0, 0, mBitmapSrc.getWidth(),
                            mBitmapSrc.getHeight(), matrix, true);

            this.mDrawable = new BitmapDrawable(mContext.getResources(),
                    bitmapSrcTemp);
            invalidate();
        }

        checkFloatDrawableBounds();
    }

    /**
     * 获取剪切比例
     *
     * @return
     */
    public float getCropScale() {
        float scale = this.cropWidth / (float) this.cropHeight;
        return scale;
    }

    /**
     * 设置操作动画 true : 打开 false : 关闭
     *
     * @param animationOn
     */
    public void setAnimationOn(Boolean animationOn) {
        this.animationOn = animationOn;
    }

    /**
     * 手势操作
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() > 1) {
            if (mStatus == STATUS_Touch_SINGLE) {
                mStatus = STATUS_TOUCH_MULTI_START;

                oldx_0 = event.getX(0);
                oldy_0 = event.getY(0);

                oldx_1 = event.getX(1);
                oldy_1 = event.getY(1);
            } else if (mStatus == STATUS_TOUCH_MULTI_START) {
                mStatus = STATUS_TOUCH_MULTI_TOUCHING;
            }
        } else {
            if (mStatus == STATUS_TOUCH_MULTI_START
                    || mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
                oldx_0 = 0;
                oldy_0 = 0;

                oldx_1 = 0;
                oldy_1 = 0;

                oldX = event.getX();
                oldY = event.getY();
            }

            mStatus = STATUS_Touch_SINGLE;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                oldX = event.getX();
                oldY = event.getY();
                break;

            case MotionEvent.ACTION_UP:

                checkBounds();
                break;

            case MotionEvent.ACTION_POINTER_UP:

                break;

            case MotionEvent.ACTION_MOVE:

                if (mStatus == STATUS_TOUCH_MULTI_TOUCHING) {
                    float newx_0 = event.getX(0);
                    float newy_0 = event.getY(0);

                    float newx_1 = event.getX(1);
                    float newy_1 = event.getY(1);

                    float oldWidth = Math.abs(oldx_1 - oldx_0);
                    float oldHeight = Math.abs(oldy_1 - oldy_0);

                    float newWidth = Math.abs(newx_1 - newx_0);
                    float newHeight = Math.abs(newy_1 - newy_0);

                    boolean isDependHeight = Math.abs(newHeight - oldHeight) > Math
                            .abs(newWidth - oldWidth);

                    float ration = isDependHeight
                            ? (newHeight / oldHeight)
                            : (newWidth / oldWidth);
                    int centerX = mDrawableDst.centerX();
                    int centerY = mDrawableDst.centerY();
                    int _newWidth = (int) (mDrawableDst.width() * ration);
                    int _newHeight = (int) (_newWidth / oriRationWH);

                    float tmpZoomRation = (float) _newWidth
                            / (float) mDrawableSrc.width();
                    if (tmpZoomRation >= maxZoomOut) {
                        _newWidth = (int) (maxZoomOut * mDrawableSrc.width());
                        _newHeight = (int) (_newWidth / oriRationWH);
                    } else if (tmpZoomRation <= minZoomIn) {
                        _newWidth = (int) (minZoomIn * mDrawableSrc.width());
                        _newHeight = (int) (_newWidth / oriRationWH);
                    }

                    mDrawableDst.set(centerX - _newWidth / 2, centerY
                            - _newHeight / 2, centerX + _newWidth / 2, centerY
                            + _newHeight / 2);
                    invalidate();
                    oldx_0 = newx_0;
                    oldy_0 = newy_0;

                    oldx_1 = newx_1;
                    oldy_1 = newy_1;
                } else if (mStatus == STATUS_Touch_SINGLE) {
                    int dx = (int) (event.getX() - oldX);
                    int dy = (int) (event.getY() - oldY);

                    oldX = event.getX();
                    oldY = event.getY();

                    if (!(dx == 0 && dy == 0)) {
                        mDrawableDst.offset(dx, dy);
                        invalidate();
                    }
                }
                break;
        }

        return true;
    }

    private ImageView iv;

    @Override
    protected void onDraw(Canvas canvas) {

        if (mDrawable == null) {
            return;
        }

        if (mDrawable.getIntrinsicWidth() == 0
                || mDrawable.getIntrinsicHeight() == 0) {
            return;
        }

        configureBounds();
        mDrawable.draw(canvas);
        canvas.save();
        canvas.clipRect(mDrawableFloat, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#B0000000"));
        canvas.restore();
        mFloatDrawable.draw(canvas);
    }

    /**
     * 构建界面数据 每次刷新都会调用
     */
    private void configureBounds() {
        if (isFrist) {
            oriRationWH = ((float) mDrawable.getIntrinsicWidth())
                    / ((float) mDrawable.getIntrinsicHeight());

            int floatWidth = ViewUtil.dipTopx(mContext, cropWidth);
            int floatHeight = ViewUtil.dipTopx(mContext, cropHeight);

            final float scale = mContext.getResources().getDisplayMetrics().density;
            int w = Math.min(getWidth(), (int) (mDrawable.getIntrinsicWidth()
                    * scale + 0.5f));
            int h = (int) (w / oriRationWH);

            if (w < floatWidth || h < floatHeight) {

                float scaleW = floatWidth / (float) w;
                float scaleH = floatHeight / (float) h;

                float maxScale = Math.max(scaleW, scaleH);

                w = (int) (w * maxScale);
                h = (int) (h * maxScale);
            }

            int left = (getWidth() - w) / 2;
            int top = (getHeight() - h) / 2 - floatViewTop;
            int right = left + w;
            int bottom = top + h;

            mDrawableSrc.set(left, top, right, bottom);
            mDrawableDst.set(mDrawableSrc);

            if (floatWidth > getWidth()) {
                floatWidth = getWidth();
                floatHeight = cropHeight * floatWidth / cropWidth;
            }

            if (floatHeight > getHeight()) {
                floatHeight = getHeight();
                floatWidth = cropWidth * floatHeight / cropHeight;
            }

            int floatLeft = (getWidth() - floatWidth) / 2;
            int floatTop = (getHeight() - floatHeight) / 2 - floatViewTop;
            int floatRight = floatLeft + floatWidth;
            int floatBottom = floatTop + floatHeight;

            mDrawableFloat.set(floatLeft, floatTop, floatRight, floatBottom);

            isFrist = false;
        }

        mDrawable.setBounds(mDrawableDst);
        mFloatDrawable.setBounds(mDrawableFloat);
    }

    /**
     * 切换剪切比例数据更新
     * <p>
     * (含有动画的浮层数据刷新)
     */
    private void checkFloatDrawableBounds() {
        if (mDrawableFloat == null || mDrawableSrc == null) {
            return;
        }

        final int oldFloatLeft = mDrawableFloat.left;
        final int oldFloatTop = mDrawableFloat.top;
        final int oldFloatRight = mDrawableFloat.right;
        final int oldFloatBottom = mDrawableFloat.bottom;

        int floatWidth = ViewUtil.dipTopx(mContext, cropWidth);
        int floatHeight = ViewUtil.dipTopx(mContext, cropHeight);

        if (floatWidth > getWidth()) {
            floatWidth = getWidth();
            floatHeight = (int) (cropHeight * floatWidth / (float) cropWidth);
            isChanging = true;
        }

        if (floatHeight > getHeight()) {
            floatHeight = getHeight();
            floatWidth = (int) (cropWidth * floatHeight / (float) cropHeight);
            isChanging = true;
        }

        final int floatLeft = (getWidth() - floatWidth) / 2;
        final int floatTop = (getHeight() - floatHeight) / 2 - floatViewTop;
        final int floatRight = floatLeft + floatWidth;
        final int floatBottom = floatTop + floatHeight;

        oriRationWH = ((float) mDrawable.getIntrinsicWidth())
                / ((float) mDrawable.getIntrinsicHeight());

        final float scale = mContext.getResources().getDisplayMetrics().density;

        int w = Math.min(getWidth(), (int) (mDrawable.getIntrinsicWidth()
                * scale + 0.5f));

        int h = (int) (w / oriRationWH);

        if (w < floatWidth || h < floatHeight) {

            float scaleW = floatWidth / (float) w;
            float scaleH = floatHeight / (float) h;

            float maxScale = Math.max(scaleW, scaleH);

            w = (int) (w * maxScale);
            h = (int) (h * maxScale);
        }

        int left = (getWidth() - w) / 2;
        int top = (getHeight() - h) / 2 - floatViewTop;
        int right = left + w;
        int bottom = top + h;

        mDrawableSrc.set(left, top, right, bottom);
        mDrawableDst.set(mDrawableSrc);

        if (animationOn) {
            isFrist = false;
            leftDelta = floatLeft - oldFloatLeft;
            topDelta = floatTop - oldFloatTop;
            rightdeta = floatRight - oldFloatRight;
            bottomDeta = floatBottom - oldFloatBottom;

            this.post(new Runnable() {

                @Override
                public void run() {

                    if (Math.abs(leftDelta) > mixValue
                            || Math.abs(topDelta) > mixValue
                            || Math.abs(rightdeta) > mixValue
                            || Math.abs(bottomDeta) > mixValue) {

                        if (Math.abs(leftDelta) > mixValue) {
                            leftDelta = (int) (leftDelta / mixValue);
                        }

                        if (Math.abs(topDelta) > mixValue) {
                            topDelta = (int) (topDelta / mixValue);
                        }

                        if (Math.abs(rightdeta) > mixValue) {
                            rightdeta = (int) (rightdeta / mixValue);
                        }

                        if (Math.abs(bottomDeta) > mixValue) {
                            bottomDeta = (int) (bottomDeta / mixValue);
                        }

                        if (mDrawableFloat != null) {
                            mDrawableFloat.set(floatLeft - leftDelta, floatTop
                                            - topDelta, floatRight - rightdeta,
                                    floatBottom - bottomDeta);
                        }

                        isChanging = true;
                        CropImageSampleView.this.post(this);
                    } else {
                        if (mDrawableFloat != null) {
                            mDrawableFloat.set(floatLeft, floatTop, floatRight,
                                    floatBottom);
                        }

                        isChanging = false;
                    }

                    invalidate();
                }
            });
        } else {
            if (mDrawableFloat != null) {
                mDrawableFloat
                        .set(floatLeft, floatTop, floatRight, floatBottom);
            }

            isChanging = false;
        }

    }

    /**
     * 操作图片的数据数据刷新 (含有动画的图片数据刷新)
     */
    private void checkBounds() {
        if (mDrawableDst == null || mDrawableFloat == null) {
            return;
        }

        int newLeft = mDrawableDst.left;
        int newTop = mDrawableDst.top;

        if (mDrawableDst.left > mDrawableFloat.left) {
            newLeft = mDrawableFloat.left;

            if (mDrawableDst.top + mDrawableDst.height() < mDrawableFloat.bottom) {
                newTop = mDrawableFloat.bottom - mDrawableDst.height();
            } else if (mDrawableDst.top > mDrawableFloat.top) {
                newTop = mDrawableFloat.top;
            }
        } else if (mDrawableDst.left + mDrawableDst.width() < mDrawableFloat.right) {
            newLeft = mDrawableFloat.right - mDrawableDst.width();

            if (mDrawableDst.top + mDrawableDst.height() < mDrawableFloat.bottom) {
                newTop = mDrawableFloat.bottom - mDrawableDst.height();
            } else if (mDrawableDst.top > mDrawableFloat.top) {
                newTop = mDrawableFloat.top;
            }
        } else if (mDrawableDst.top > mDrawableFloat.top) {
            newTop = mDrawableFloat.top;
        } else if (mDrawableDst.top + mDrawableDst.height() < mDrawableFloat.bottom) {
            newTop = mDrawableFloat.bottom - mDrawableDst.height();
        }

        /**
         * 动画有关
         */
        if (animationOn) {
            final int leftSum = newLeft;
            final int topSum = newTop;

            leftDelta = (newLeft - mDrawableDst.left);
            topDelta = (newTop - mDrawableDst.top);

            this.post(new Runnable() {

                @Override
                public void run() {
                    if (Math.abs(leftDelta) > mixValue
                            || Math.abs(topDelta) > mixValue) {

                        if (Math.abs(leftDelta) > mixValue) {
                            leftDelta = (int) (leftDelta / mixValue);
                        }

                        if (Math.abs(topDelta) > mixValue) {
                            topDelta = (int) (topDelta / mixValue);
                        }

                        if (mDrawableDst != null) {
                            mDrawableDst.offsetTo(leftSum - leftDelta, topSum
                                    - topDelta);
                        }

                        CropImageSampleView.this.post(this);
                    } else {
                        if (mDrawableDst != null) {
                            mDrawableDst.offsetTo(leftSum, topSum);
                        }

                    }
                    invalidate();
                }
            });
        } else {
            if (mDrawableDst != null) {
                mDrawableDst.offsetTo(newLeft, newTop);
            }

            invalidate();
        }

    }

    /**
     * 获取剪切的图片
     *
     * @return
     */
    public Bitmap getCropImage() {

        if (isChanging) {
            return null;
        }

        if (mDrawableSrc == null || mDrawableFloat == null
                || mDrawableDst == null || bitmapSrcTemp == null) {
            return null;
        }

        try {
            if (mDrawableSrc.contains(mDrawableFloat)) {
                int marginLeft = Math.abs(mDrawableFloat.left
                        - mDrawableDst.left);
                int marginTop = Math.abs(mDrawableFloat.top - mDrawableDst.top);

                float scale = (float) (bitmapSrcTemp.getWidth())
                        / (float) (mDrawableDst.width());

                int width = (int) (mDrawableFloat.width() * scale);
                int height = (int) (mDrawableFloat.height() * scale);

                int x = (int) (marginLeft * scale);
                int y = (int) (marginTop * scale);

                Bitmap corpBitmap = Bitmap.createBitmap(bitmapSrcTemp, x, y,
                        width, height);

                Matrix matrix = new Matrix();

                float resultScale = MaxCropWidth
                        / (float) corpBitmap.getWidth();
                matrix.setScale(resultScale, resultScale);

                Bitmap resultBitmap = Bitmap.createBitmap(
                        corpBitmap.copy(Config.RGB_565, false), 0, 0,
                        corpBitmap.getWidth(), corpBitmap.getHeight(), matrix,
                        true);

//				if (corpBitmap != null)
//				{
//					corpBitmap.recycle();
//					corpBitmap = null;
//				}
                return resultBitmap;
            }

        } catch (Exception e) {
            Log.e("===", e.toString());
            return null;
        }

        return null;
    }

    public void recycle() {
        mContext = null;
        if (bitmapSrcTemp != null) {
            bitmapSrcTemp.recycle();
            bitmapSrcTemp = null;
        }

        mDrawable = null;

        if (mFloatDrawable != null) {
            mFloatDrawable.recycle();
            mFloatDrawable = null;
        }

        mDrawableDst = null;

        mDrawableFloat = null;
    }

}
