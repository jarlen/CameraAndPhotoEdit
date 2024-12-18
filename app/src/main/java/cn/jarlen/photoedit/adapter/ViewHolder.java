package cn.jarlen.photoedit.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewHolder {

	private LayoutInflater mLayoutInflater;
	private View mConvertView;
	private SparseArray<View> mViews;

	private int mPosition;

	public ViewHolder(Context context, int position, int layoutId,
			ViewGroup parent) {
		this.mPosition = position;
		this.mViews = new SparseArray<View>();
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId,
				parent, false);
		mConvertView.setTag(this);
	}

	public static ViewHolder getViewHolder(Context context, ViewGroup parent,
			View convertView, int position, int layoutId) {

		if (convertView == null) {
			return new ViewHolder(context, position, layoutId, parent);
		} else {
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mPosition = position;
			return viewHolder;
		}

	}

	public <T extends View> T getView(int resId) {
		View view = mViews.get(resId);

		if (view == null) {
			view = mConvertView.findViewById(resId);
			mViews.put(resId, view);
		}

		return (T) view;
	}

	public View getConvertView() {
		return mConvertView;
	}

}
