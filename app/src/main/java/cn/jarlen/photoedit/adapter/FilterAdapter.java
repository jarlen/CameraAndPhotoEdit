package cn.jarlen.photoedit.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.jarlen.photoedit.R;

public class FilterAdapter extends BaseAdapter {
	private Context mContext;

	private LayoutInflater mInflater;

	private int mSelectItem = 0;

	private ArrayList<FilterItem> mFilterList;

	public FilterAdapter(Context context) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}

	public FilterAdapter(Context context, ArrayList<FilterItem> filterList) {
		this(context);
		this.mFilterList = filterList;
	}

	public void setFilterList(ArrayList<FilterItem> list) {
		this.mFilterList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		if (mFilterList != null && mFilterList.size() > 0) {
			return mFilterList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {

		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setSelectItem(int position) {
		this.mSelectItem = position;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = ViewHolder.getViewHolder(mContext, parent,
				convertView, position, R.layout.camera_layout_filter_griditem);
		Bitmap bmp = mFilterList.get(position).getFilterThumbnail();

		((ImageView) holder.getView(R.id.filterThumbnail)).setImageBitmap(bmp);

		String name = mFilterList.get(position).getFilterName();
		((TextView) holder.getView(R.id.filterName)).setText(name);

		if (position == mSelectItem) {
			holder.getConvertView().setBackgroundDrawable(
					mContext.getResources().getDrawable(
							R.drawable.camera_shape_wm_item_selected));
		} else {
			holder.getConvertView().setBackgroundDrawable(
					mContext.getResources().getDrawable(
							R.drawable.camera_shape_wm_item));
		}

		return holder.getConvertView();
	}

	public void clear() {
		mInflater = null;
		
		if (mFilterList != null) {
			mFilterList.clear();
			mFilterList = null;
		}
		
		mContext = null;
	}

}
