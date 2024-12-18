package cn.jarlen.photoedit.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlowLabelView extends ViewGroup {

    /**
     * 标签数据
     */
    private List<List<View>> allViewsList = new ArrayList<List<View>>();
    private List<Integer> heightsList = new ArrayList<Integer>();

    /**
     * 选择监听
     */
    private OnFlowLabelViewListener mOnFlowLabelViewListener = null;
    /**
     * 可多选的最大数
     */
    private int mMaxSelectLabelCount = 0;

    /**
     * 临时标记当前选择的个数
     */
    private int mSelectLabelCount = 0;

    /**
     * 标签的最大行数
     */
    private int mMaxLineNums = 0;

    /**
     * 选择标签，当超过最大选择数时，会自动取消第一个；
     */
    private ArrayList<Integer> selectChildPosition = null;

    public FlowLabelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FlowLabelView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public FlowLabelView(Context context) {
        super(context);

    }

    /**
     * 设置标签可多选的个数
     *
     * @param mMaxSelectLabelCount
     */
    public void setMaxSelectLabelCount(int mMaxSelectLabelCount) {
        this.mMaxSelectLabelCount = mMaxSelectLabelCount;
    }

    /**
     * 设置标签列表的最大行数
     *
     * @param mMaxLineNums
     */
    public void setMaxLineNums(int mMaxLineNums) {
        this.mMaxLineNums = mMaxLineNums;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);

        // measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 当viewgroup为wrapcontent 时 使用下面的 width 和 height
        int width = 0;
        int height = 0;

        int lineNumes = 0;
        int lineWidth = 0;
        int lineHeight = 0;
        MarginLayoutParams mlp = null;
        int childCount = getChildCount();

        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);
            mlp = (MarginLayoutParams) childView.getLayoutParams();

            measureChild(childView, widthMeasureSpec, heightMeasureSpec);

            int cWidth = childView.getMeasuredWidth() + mlp.leftMargin
                    + mlp.rightMargin;
            int cHeight = childView.getMeasuredHeight() + mlp.topMargin
                    + mlp.bottomMargin;

            if (lineWidth + cWidth > sizeWidth) {// 开始换行

                if (lineNumes >= mMaxLineNums) {
                    break;
                }

                lineNumes++;
                width = Math.max(cWidth, lineWidth);
                lineWidth = cWidth; // 新一行 行宽的记录

                height += lineHeight;

                lineHeight = cHeight;// 新一行 行高的记录
            } else {
                lineWidth += cWidth;
                lineHeight = Math.max(lineHeight, cHeight);
            }
            if (i == getChildCount() - 1 && lineNumes < mMaxLineNums) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        setMeasuredDimension(MeasureSpec.EXACTLY == widthMode
                ? sizeWidth
                : width, MeasureSpec.EXACTLY == heightMode
                ? sizeHeight
                : height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        allViewsList.clear();
        heightsList.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        int lineNumes = 0;

        final int childCount = getChildCount();
        if (childCount > 0) {
            lineNumes = 0;
        }

        List<View> lineList = new ArrayList<View>();
        if (selectChildPosition == null) {
            selectChildPosition = new ArrayList<Integer>();
        }
        // 保存行信息
        for (int index = 0; index < childCount; index++) {
            View childView = getChildAt(index);

            MarginLayoutParams mlp = (MarginLayoutParams) childView
                    .getLayoutParams();

            int cWidth = childView.getMeasuredWidth();
            int cHeight = childView.getMeasuredHeight();

            if (cWidth + mlp.leftMargin + mlp.rightMargin + lineWidth > width) {// 如果要换行

                if (lineNumes >= mMaxLineNums) {
                    break;
                }

                allViewsList.add(lineList);
                heightsList.add(lineHeight);
                lineNumes++;
                lineWidth = 0;
                lineList = new ArrayList<View>();
            }

            lineWidth += cWidth + mlp.leftMargin + mlp.rightMargin;
            lineHeight = Math.max(cHeight + mlp.topMargin + mlp.bottomMargin,
                    lineHeight);

            final int clickPosition = index;

            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.isSelected()) {
                        view.setSelected(false);
                        selectChildPosition.remove(Integer
                                .valueOf(clickPosition));
                        mSelectLabelCount--;
                    } else {
                        if (mSelectLabelCount < mMaxSelectLabelCount) {
                            mSelectLabelCount++;
                        } else {
                            int pos = selectChildPosition.get(0);
                            getChildAt(pos).setSelected(false);
                            selectChildPosition.remove(0);
                        }
                        selectChildPosition.add(Integer.valueOf(clickPosition));
                        view.setSelected(true);

                    }

                    if (mOnFlowLabelViewListener != null) {
                        mOnFlowLabelViewListener.onFlowLabelSlected(childCount,
                                clickPosition);
                    }

                }
            });
            lineList.add(childView);
        }

        // 记录最后一行信息
        if (lineNumes < mMaxLineNums) {
            allViewsList.add(lineList);
            heightsList.add(lineHeight);
        }

        int left = 0, top = 0;

        for (int i = 0; i < allViewsList.size(); i++) {
            List<View> lineViewList = allViewsList.get(i);

            for (int j = 0; j < lineViewList.size(); j++) {

                View cView = lineViewList.get(j);

                MarginLayoutParams mlp = (MarginLayoutParams) cView
                        .getLayoutParams();

                int rl = left + mlp.leftMargin;
                int rt = top + mlp.topMargin;
                int cr = rl + cView.getMeasuredWidth();
                int cb = rt + cView.getMeasuredHeight();

                cView.layout(rl, rt, cr, cb);

                left += cView.getMeasuredWidth() + mlp.leftMargin
                        + mlp.rightMargin;
            }

            left = 0;
            top += heightsList.get(i);

        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        // TODO Auto-generated method stub
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * 设置标签选择监听
     *
     * @param listener
     */
    public void setOnFlowLabelViewListener(OnFlowLabelViewListener listener) {
        this.mOnFlowLabelViewListener = listener;
    }

    public interface OnFlowLabelViewListener {
        public void onFlowLabelSlected(int childCount, int position);
    }

}