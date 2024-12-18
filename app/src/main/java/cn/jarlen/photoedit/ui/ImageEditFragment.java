package cn.jarlen.photoedit.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.adapter.FilterAdapter;
import cn.jarlen.photoedit.adapter.FilterItem;
import cn.jarlen.photoedit.adapter.WMFolderAdapter;
import cn.jarlen.photoedit.edit.OperateView;
import cn.jarlen.photoedit.filter.GPUImageFilterManager;
import cn.jarlen.photoedit.filter.base.GPUImageFilter;
import cn.jarlen.photoedit.net.Constant;
import cn.jarlen.photoedit.util.FragmentHelper;
import cn.jarlen.photoedit.util.LittleUtils;
import cn.jarlen.photoedit.util.LoadingDialog;
import cn.jarlen.photoedit.util.TipUtils;
import cn.jarlen.photoedit.view.HorizontalListView;
import cn.jarlen.photoedit.view.JarlenView;
import cn.jarlen.photoedit.view.TabHorizontalScrollView;
import cn.jarlen.photoedit.view.WMFilterOperateView;


public class ImageEditFragment extends BaseFragment implements OnClickListener,
		TabHorizontalScrollView.OnTabItemClickListener, JarlenView.OnRulerSeekChangeListener {

	private CameraUIActivity mActivity;

	private ImageView imageEditBackBtn;
	private Button imagEditNextBtn;

	/**
	 * 图像编辑界面View
	 */

	private WMFilterOperateView mWMFilterOperateView;

	/**
	 * 商店标签
	 */
	private RelativeLayout shopLabeRl;
	private RelativeLayout shoppingRl;

	private TabHorizontalScrollView mTabScrollView;

	private HorizontalListView mChooseContent;

	/** 微调选择区 **/
	private LinearLayout mChooseContentAdjust_LL;
	private RelativeLayout adjustBar_rl;
	private JarlenView mAdjustBar;
	private TextView adjustBar_tv;
	private TextView adjustBarResetBtn;

	/**
	 * 底部切换按钮,滤镜，水印，微调
	 */
	private TextView bottomMenuFilterBtn, bottomMenuWatermarkBtn,
			bottomMenuAdjustBtn;
	private LinearLayout imageEditBottomMenu_LL;

	/**
	 * 底部微调按钮，返回，确定
	 */
	private ImageView mAdjustBack, mAdjustOk;
	private LinearLayout adjustBottomMenu_LL;

	/**
	 * 
	 */
	private int menuType = 0;

	/**
	 * 编辑的图片
	 */
	private Bitmap mBitmapSrc = null;

	/**
	 * 编辑的图片路径
	 */
	private String mImagePath;
	private ImageView mShoppinBtn;
	private float mScale = 1.0f;

	private WMFolderAdapter mWMFolderAdapter;
	private ArrayList<String> mWMTabList;

	private int mCurrentWmFolderPosition = 0;

	private ArrayList<FilterItem> mFilterList;
	private FilterAdapter mFilterAdapter;

	private String WMRootPath = null;
	
	private LoadingDialog mLoadingDialog;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (CameraUIActivity) activity;
		this.WMRootPath = mActivity.getExternalFilesDir(null).getAbsolutePath()
				+ Constant.WM_NET_Local;
	}

	/**
	 * 初始化view控件
	 * 
	 * @param view
	 */
	private void initView(View view) {

		mImagePath = getArguments().getString("imgPath");
		mScale = getArguments().getFloat("scale");

		imageEditBackBtn = (ImageView) view.findViewById(R.id.image_edit_back);
		imageEditBackBtn.setOnClickListener(this);
		imagEditNextBtn = (Button) view.findViewById(R.id.image_edit_next);
		imagEditNextBtn.setClickable(false);
		imagEditNextBtn.setOnClickListener(this);

		mBitmapSrc = BitmapFactory.decodeFile(mImagePath);

		/** 商店 标签栏 **/
		shopLabeRl = (RelativeLayout) view.findViewById(R.id.shop_and_label_ll);
		shoppingRl = (RelativeLayout) view.findViewById(R.id.shopping_Rl);
		mShoppinBtn = (ImageView) view.findViewById(R.id.shopping_btn);
		mShoppinBtn.setOnClickListener(this);

		/** 标签栏 **/
		mTabScrollView = (TabHorizontalScrollView) view
				.findViewById(R.id.tabScrollView);
		mTabScrollView.setOnTabItemClickListener(this);

		/** 滤镜，水印选择区 **/
		mChooseContent = (HorizontalListView) view
				.findViewById(R.id.chooseContent);

		mWMFilterOperateView = (WMFilterOperateView) view
				.findViewById(R.id.wm_filter_operateview);

		/** 微调选择区 **/
		mChooseContentAdjust_LL = (LinearLayout) view
				.findViewById(R.id.chooseContent_adjust);
		view.findViewById(R.id.adjust_brightness).setOnClickListener(
				onAdjustClickListener);
		view.findViewById(R.id.adjust_color).setOnClickListener(
				onAdjustClickListener);
		view.findViewById(R.id.adjust_tonecurve).setOnClickListener(
				onAdjustClickListener);
		adjustBar_rl = (RelativeLayout) view.findViewById(R.id.adjustBar_rl);
		mAdjustBar = (JarlenView) view.findViewById(R.id.adjustBar);
		mAdjustBar.setOnRulerSeekChangeListener(this);
		adjustBar_tv = (TextView) view.findViewById(R.id.adjustBar_tv);
		adjustBarResetBtn = (TextView) view
				.findViewById(R.id.adjustBar_reset_btn);
		adjustBarResetBtn.setOnClickListener(onAdjustClickListener);

		/** 滤镜，水印，微调选择栏按钮 **/
		imageEditBottomMenu_LL = (LinearLayout) view
				.findViewById(R.id.image_edit_bottom_menu_ll);
		bottomMenuFilterBtn = (TextView) view
				.findViewById(R.id.image_edit_bottom_menu_filter);
		bottomMenuFilterBtn.setOnClickListener(this);
		bottomMenuWatermarkBtn = (TextView) view
				.findViewById(R.id.image_edit_bottom_menu_watermark);
		bottomMenuWatermarkBtn.setOnClickListener(this);
		bottomMenuAdjustBtn = (TextView) view
				.findViewById(R.id.image_edit_bottom_menu_adjust);
		bottomMenuAdjustBtn.setOnClickListener(this);

		/** 微调界面底部按钮 **/
		adjustBottomMenu_LL = (LinearLayout) view
				.findViewById(R.id.adjust_bottom_menu_ll);
		mAdjustBack = (ImageView) view.findViewById(R.id.adjust_back);
		mAdjustBack.setOnClickListener(onAdjustClickListener);
		mAdjustOk = (ImageView) view.findViewById(R.id.adjust_ok);
		mAdjustOk.setOnClickListener(onAdjustClickListener);
		
		mWMFilterOperateView.post(new Runnable() {
			
			@Override
			public void run() {
				
				if (!mWMFilterOperateView.isInvalidating()) {

					mWMFilterOperateView.setImageResource(mBitmapSrc,
							new OperateView.OnTouchWaterMarkListener() {
								@Override
								public void onTouchDown() {
									if (menuType == 1) {
										shopLabeRl.setVisibility(View.GONE);
									} else if (menuType == 0) {
										mWMFilterOperateView
												.setCompareViewVISIBLE();
									}
								}

								@Override
								public void OnTouchUp() {
									if (menuType == 1) {
										shopLabeRl.setVisibility(View.VISIBLE);
									} else if (menuType == 0) {
										mWMFilterOperateView
												.setCompareViewHide();
									}
								}
							});
					
					imagEditNextBtn.setClickable(true);

				} else {
					mWMFilterOperateView.post(this);
				}
			}
		});
		
		mLoadingDialog = new LoadingDialog(mActivity);
	}

	private void initData() {
		mFilterAdapter = new FilterAdapter(mActivity);
		mChooseContent.setAdapter(mFilterAdapter);
		mChooseContent
				.setOnItemClickListener(new OnChooseContentItemClickListener());

		mFilterAsyncTask = new FilterAsyncTask();
		mFilterAsyncTask.execute();
		mWMFolderAdapter = new WMFolderAdapter(mActivity, mBitmapSrc);
		mWMTabList = mWMFolderAdapter.getWMFolderData();

	}

	@Override
	public void onResume() {
		super.onResume();
		if (menuType == 1) {
			int lastsize = 0;
			if (mWMTabList != null) {
				lastsize = mWMTabList.size();
			}
			mWMTabList = mWMFolderAdapter.getWMFolderData();
			mTabScrollView.setTabData(mWMTabList);
			if (mCurrentWmFolderPosition < mWMFolderAdapter.getLocalWMNum() || mWMTabList.size() == lastsize) {
				mTabScrollView.setCurrentPosition(mCurrentWmFolderPosition);
			} else {
				mTabScrollView.setCurrentPosition(0);
			}

		}
	}

	@Override
	public void onClick(View view) {

		imagEditNextBtn.setVisibility(View.VISIBLE);
		adjustBar_rl.setVisibility(View.GONE);

		int id = view.getId();

		if (id == R.id.image_edit_bottom_menu_filter) {
			// 滤镜
			menuType = 0;
			/** 设置水印按钮高亮色 **/
			bottomMenuFilterBtn.setTextColor(Color.parseColor("#FFBA89EB"));
			bottomMenuWatermarkBtn.setTextColor(Color.parseColor("#FFFFFFFF"));
			bottomMenuAdjustBtn.setTextColor(Color.parseColor("#FFFFFFFF"));

			/** 将其他不属于滤镜的控件隐藏，滤镜控件显示 **/
			if (mChooseContentAdjust_LL != null
					&& mChooseContentAdjust_LL.isShown()) {
				mChooseContentAdjust_LL.setVisibility(View.GONE);
			}

			if (mChooseContent != null && !mChooseContent.isShown()) {
				mChooseContent.setVisibility(View.VISIBLE);
			}

			if (shopLabeRl.isShown()) {
				shopLabeRl.setVisibility(View.GONE);
			}

			if (mFilterList != null) {
				mFilterAdapter.setFilterList(mFilterList);
				mChooseContent.setAdapter(mFilterAdapter);
			}
		} else if (id == R.id.image_edit_bottom_menu_watermark) {
			// 水印
			menuType = 1;

			/** 设置水印按钮高亮色 **/
			bottomMenuFilterBtn.setTextColor(Color.parseColor("#FFFFFFFF"));
			bottomMenuWatermarkBtn.setTextColor(Color.parseColor("#FFBA89EB"));
			bottomMenuAdjustBtn.setTextColor(Color.parseColor("#FFFFFFFF"));

			mFilterAdapter.setFilterList(null);

			/** 将其他不属于水印的控件隐藏，水印控件显示 **/

			if (mChooseContentAdjust_LL != null
					&& mChooseContentAdjust_LL.isShown()) {
				mChooseContentAdjust_LL.setVisibility(View.GONE);
			}

			if (mChooseContent != null && !mChooseContent.isShown()) {
				mChooseContent.setVisibility(View.VISIBLE);
			}

			if (!shopLabeRl.isShown()) {
				shopLabeRl.setVisibility(View.VISIBLE);
			}

			mTabScrollView.setTabData(mWMTabList);
			mChooseContent.setAdapter(mWMFolderAdapter);

			mTabScrollView.setCurrentPosition(mCurrentWmFolderPosition);
		} else if (id == R.id.image_edit_bottom_menu_adjust) {
			// 微调
			menuType = 3;

			/** 设置微调按钮高亮色 **/
			bottomMenuFilterBtn.setTextColor(Color.parseColor("#FFFFFFFF"));
			bottomMenuWatermarkBtn.setTextColor(Color.parseColor("#FFFFFFFF"));
			bottomMenuAdjustBtn.setTextColor(Color.parseColor("#FFBA89EB"));

			mFilterAdapter.setFilterList(null);

			/** 将其他不属于微调的控件隐藏，微调控件显示 **/
			if (shopLabeRl.isShown()) {
				shopLabeRl.setVisibility(View.GONE);
			}
			if (mChooseContent != null && mChooseContent.isShown()) {
				mChooseContent.setVisibility(View.GONE);
			}
			if (mChooseContentAdjust_LL != null
					&& !mChooseContentAdjust_LL.isShown()) {
				mChooseContentAdjust_LL.setVisibility(View.VISIBLE);
			}
		} else if (id == R.id.image_edit_back) {
			FragmentHelper.popBackStack(mActivity);
			
		} else if (id == R.id.image_edit_next) {
			
			imagEditNextBtn.setClickable(false);
			if(mLoadingDialog != null){
				mLoadingDialog.show();
			}

			mWMFilterOperateView
					.WMFilterSaveToPictures(new WMFilterOperateView.OnPictureSavedListener() {

						@Override
						public void onPictureSaved(String resultPath) {
							imagEditNextBtn.setClickable(true);
							
							if(mLoadingDialog != null){
								mLoadingDialog.dismiss();
							}
							
							Bundle bundle = new Bundle();
							bundle.putString("scale", mScale + "");
							bundle.putString("picturePath", resultPath);

							LittleUtils.startActivity(mActivity,
									PublishActivity.class, bundle);
							mActivity.overridePendingTransition(
									R.anim.ac_transition_fade_in,
									R.anim.ac_transition_fade_out);

						}
					});

		} else if (id == R.id.shopping_btn) {
			TipUtils.ShowText(mActivity,"水印商店跳转，此功能正在开发中");
		}

	}

	/**
	 * 生成的滤镜缩略图的线程
	 * 
	 */
	private FilterAsyncTask mFilterAsyncTask;

	private class FilterAsyncTask extends
			AsyncTask<Integer, Integer, ArrayList<FilterItem>> {

		private String[] filterName;
		private int[] filterType;

		public FilterAsyncTask() {
			filterName = mActivity.getResources().getStringArray(
					R.array.filterName);
			filterType = mActivity.getResources().getIntArray(
					R.array.filterType);
		}

		@Override
		protected ArrayList<FilterItem> doInBackground(Integer... params) {

			ArrayList<FilterItem> filterList = new ArrayList<FilterItem>();
			int width = (int) mActivity.getResources().getDimension(
					R.dimen.filter_item_width);
			int height = (int) mActivity.getResources().getDimension(
					R.dimen.filter_item_height);
			Bitmap thumbnailBmp = ThumbnailUtils.extractThumbnail(mBitmapSrc,
					width, height);
			GPUImageFilterManager filterManaager = null;

			filterManaager = new GPUImageFilterManager(mActivity);
			filterManaager.setImageSrc(thumbnailBmp);

			for (int index = 0; index < filterType.length; index++) {
				FilterItem filterItem = new FilterItem();

				/** 设置滤镜类型 **/
				filterItem.setFilterType(filterType[index]);

				/** 设置滤镜名字 **/
				filterItem.setFilterName(filterName[index]);

				/** 生成滤镜缩略图 **/

				Bitmap thumbnailFilterBmp = filterManaager
						.createBitmapWithFilterApplied(filterType[index]);

				/** 设置滤镜缩略图 **/
				filterItem.setFilterThumbnail(thumbnailFilterBmp);

				filterList.add(filterItem);
			}

			return filterList;
		}

		@Override
		protected void onPostExecute(ArrayList<FilterItem> result) {
			mFilterList = result;
			mFilterAdapter.setFilterList(result);
			super.onPostExecute(result);
		}

	}

	@Override
	public void onClickTabItem(int value) {
		mWMFolderAdapter.setWMFolderSelected(value);
		this.mCurrentWmFolderPosition = value;
	}

	private GPUImageFilterManager mFilterManaager = null;

	private class OnChooseContentItemClickListener implements
			OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {

			if (menuType == 0) {

				if (mFilterManaager == null) {
					mFilterManaager = new GPUImageFilterManager(mActivity);

				}

				ArrayList<GPUImageFilter> list = mFilterManaager
						.GPUCreateGPUImageFilter(mFilterList.get(position)
								.getFilterType());

				mWMFilterOperateView.setFilterTypeInManager(mFilterList.get(
						position).getFilterType());

				mWMFilterOperateView.setOperateFilter(list);
				mFilterAdapter.setSelectItem(position);

			} else if (menuType == 1) {
				String folderName = mWMFolderAdapter.getCurrentWMFolder()
						.getFolderId();
				ArrayList<String> mResList = (ArrayList<String>) mWMFolderAdapter
						.getCurrentWMFolder().getResource();

				if (mWMFolderAdapter.getCurrentWMFolder().getFixedIcon() != null) {
					// 添加两个水印

					Bitmap bmp = null;
					Bitmap fixedBmp = null;

					if (mWMFolderAdapter.getIsLocal()) {
						InputStream isFixed = null;

						String resPath = Constant.WM_Local + folderName + "/"
								+ mResList.get(position);
						String resFixedPath = Constant.WM_Local
								+ folderName
								+ "/"
								+ mWMFolderAdapter.getCurrentWMFolder()
										.getFixedIcon();
						try {
							AssetManager assetManager = mActivity.getAssets();
							InputStream is = null;
							is = assetManager.open(resPath);
							isFixed = assetManager.open(resFixedPath);

							bmp = BitmapFactory.decodeStream(is);
							fixedBmp = BitmapFactory.decodeStream(isFixed);

						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {

						String resPath = WMRootPath + folderName + "/"
								+ mResList.get(position);
						String resFixedPath = WMRootPath
								+ folderName
								+ "/"
								+ mWMFolderAdapter.getCurrentWMFolder()
										.getFixedIcon();
						bmp = BitmapFactory.decodeFile(resPath);
						fixedBmp = BitmapFactory.decodeFile(resFixedPath);
					}

					mWMFilterOperateView.addWaterMark(fixedBmp, bmp);
				} else {
					// 添加单个水印

					Bitmap bmp = null;
					if (mWMFolderAdapter.getIsLocal()) {
						String resPath = Constant.WM_Local + folderName + "/"
								+ mResList.get(position);
						try {
							AssetManager assetManager = mActivity.getAssets();
							InputStream is = null;
							is = assetManager.open(resPath);
							bmp = BitmapFactory.decodeStream(is);

						} catch (IOException e) {
							Log.e("===", "IOException");
							e.printStackTrace();
						}
					} else {
						String resPath = WMRootPath + folderName + "/"
								+ mResList.get(position);
						bmp = BitmapFactory.decodeFile(resPath);
					}

					mWMFilterOperateView.addWaterMark(bmp);
				}

				mWMFolderAdapter.setItemSelected(position);

			}
		}
	}

	/************************ 微调 Begin ****************************************/

	private OnAdjustClickListener onAdjustClickListener = new OnAdjustClickListener();

	private float brightnessValue = 0.0f;
	private float whiteBalanceValue = 0.5f;
	private float vignetteValue = 1.0f;

	private float brightnessTemp = 0.0f;
	private float whiteBalancetemp = 0.5f;
	private float vignetteTemp = 1.0f;

	private int adjustType = 0;

	private class OnAdjustClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {

			mChooseContentAdjust_LL.setVisibility(View.GONE);
			imageEditBottomMenu_LL.setVisibility(View.GONE);
			adjustBottomMenu_LL.setVisibility(View.VISIBLE);

			int id = view.getId();

			if (id == R.id.adjust_brightness) {
				// 微调－亮度
				imagEditNextBtn.setVisibility(View.GONE);
				imageEditBackBtn.setVisibility(View.GONE);
				adjustType = 1;
				mAdjustBar.setOnlySide(1, brightnessValue);
				adjustBar_rl.setVisibility(View.VISIBLE);
				/** 范围 －1.0f ～ 1.0f 0.0 为原图 **/
				mWMFilterOperateView.setBrightnessValue(brightnessValue);
			} else if (id == R.id.adjust_color) {
				// 微调－－色温
				imagEditNextBtn.setVisibility(View.GONE);
				imageEditBackBtn.setVisibility(View.GONE);
				adjustType = 2;
				mAdjustBar.setOnlySide(2, whiteBalanceValue);
				adjustBar_rl.setVisibility(View.VISIBLE);
				/** 范围 0 ～ 1，从冷到原图，再到温 **/
				mWMFilterOperateView.setWhiteBalanceValue(whiteBalanceValue);
			} else if (id == R.id.adjust_tonecurve) {
				// 微调－暗角
				imagEditNextBtn.setVisibility(View.GONE);
				imageEditBackBtn.setVisibility(View.GONE);
				adjustType = 3;
				/** 范围 0 ～ 1，从无到有 **/
				mAdjustBar.setOnlySide(3, 1 - vignetteValue);
				adjustBar_rl.setVisibility(View.VISIBLE);

				mWMFilterOperateView.setVignetteValue(vignetteValue);
			} else if (id == R.id.adjust_ok) {
				// 微调－确定
				imagEditNextBtn.setVisibility(View.VISIBLE);
				imageEditBackBtn.setVisibility(View.VISIBLE);
				adjustBar_rl.setVisibility(View.GONE);
				adjustBottomMenu_LL.setVisibility(View.GONE);
				imageEditBottomMenu_LL.setVisibility(View.VISIBLE);
				mChooseContentAdjust_LL.setVisibility(View.VISIBLE);

				switch (adjustType) {
				case 1:
					brightnessValue = brightnessTemp;
					mWMFilterOperateView.setBrightnessValue(brightnessValue);

					break;
				case 2:
					whiteBalanceValue = whiteBalancetemp;
					mWMFilterOperateView
							.setWhiteBalanceValue(whiteBalanceValue);
					break;
				case 3:
					vignetteValue = vignetteTemp;
					mWMFilterOperateView.setVignetteValue(vignetteValue);

					break;

				default:
					break;
				}

			} else if (id == R.id.adjust_back) {
				// 微调－取消
				imagEditNextBtn.setVisibility(View.VISIBLE);
				imageEditBackBtn.setVisibility(View.VISIBLE);
				adjustBar_rl.setVisibility(View.GONE);
				adjustBottomMenu_LL.setVisibility(View.GONE);
				imageEditBottomMenu_LL.setVisibility(View.VISIBLE);
				mChooseContentAdjust_LL.setVisibility(View.VISIBLE);

				switch (adjustType) {
				case 1:

					mWMFilterOperateView.setBrightnessValue(brightnessValue);
					break;
				case 2:

					mWMFilterOperateView
							.setWhiteBalanceValue(whiteBalanceValue);

					break;
				case 3:

					mWMFilterOperateView.setVignetteValue(vignetteValue);

					break;

				default:
					break;
				}

				mWMFilterOperateView.requestOperateFilter();

			} else if (id == R.id.adjustBar_reset_btn) {
				imagEditNextBtn.setVisibility(View.GONE);
				switch (adjustType) {
				case 1:
					brightnessTemp = 0.0f;
					mAdjustBar.setOnlySide(1, brightnessTemp);
					mWMFilterOperateView.setBrightnessValue(brightnessTemp);
					break;
				case 2:
					whiteBalancetemp = 0.5f;
					mAdjustBar.setOnlySide(2, whiteBalancetemp);
					mWMFilterOperateView.setWhiteBalanceValue(whiteBalancetemp);
					break;
				case 3:
					vignetteTemp = 1.0f;
					mAdjustBar.setOnlySide(3, 1 - vignetteTemp);
					mWMFilterOperateView.setVignetteValue(vignetteTemp);

					break;

				default:
					break;
				}
			} else {
				adjustType = 0;
			}
		}

	}

	@Override
	public void OnRulerSeekValueChange(float value, float maxSum) {
	}

	@Override
	public void OnRulerSeekFactorChange(float factor) {
		switch (adjustType) {
		case 1:

			float brightDegree = (factor - 0.5f) / 0.5f * 0.7f;

			final int tv = (int) ((factor - 0.5f) / 0.5f * 12);

			brightnessTemp = brightDegree;

			/** 范围 －1.0f ～ 1.0f 0.0 为原图 **/
			mWMFilterOperateView.adjustBrightnessFilter(brightDegree);

			adjustBar_tv.setText(tv + "");

			break;
		case 2:
			float whiteBalanceDegree = factor;
			int whiteBalance_tv = (int) (12 * (factor - 0.5f) / 0.5f);
			whiteBalancetemp = whiteBalanceDegree;
			mWMFilterOperateView.adjustWhiteBalanceFilter(whiteBalanceDegree);
			adjustBar_tv.setText(whiteBalance_tv + "");

			break;
		case 3:
			float vignetteDegree = 1 - factor;
			int vignette_tv = (int) (factor * 24);
			vignetteTemp = vignetteDegree;
			mWMFilterOperateView.adjustVignetteFilter(vignetteDegree);
			adjustBar_tv.setText(vignette_tv + "");
			break;

		default:
			break;
		}

	}

	/************************ 微调 END ****************************************/

	@Override
	public int getLayoutId() {
		return R.layout.camera_fragment_image_edit;
	}

	@Override
	public void initViews(View root) {
		initView(root);
		initData();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		recycle();
	}
	
	private void recycle()
	{
		if (mFilterAsyncTask != null
				&& mFilterAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
			mFilterAsyncTask.cancel(true);
		}
		
		imageEditBackBtn = null;
		imagEditNextBtn = null;
		
		if(mWMFilterOperateView != null)
		{
			mWMFilterOperateView.recycle();
			mWMFilterOperateView = null;
		}
		
		shopLabeRl = null;
		shoppingRl = null;
		
		if(mTabScrollView != null)
		{
			mTabScrollView.recycle();
			mTabScrollView = null;
		}
		
		mChooseContent = null;
		mChooseContentAdjust_LL = null;
		adjustBar_rl = null;
		
		if(mAdjustBar != null)
		{
			mAdjustBar.recycle();
			mAdjustBar = null;
		}
		
		adjustBar_tv = null;
		adjustBarResetBtn = null;
		
		bottomMenuFilterBtn = null;
		bottomMenuAdjustBtn = null;
		bottomMenuWatermarkBtn = null;
		
		imageEditBottomMenu_LL = null;
		
		mAdjustBack = null;
		mAdjustOk = null;
		
		adjustBottomMenu_LL = null;
		
		mImagePath = null;
		mShoppinBtn = null;
		
		if(mWMFolderAdapter != null)
		{
			mWMFolderAdapter.clear();
			mWMFolderAdapter = null;
		}
		
		if(mWMTabList != null)
		{
			mWMTabList.clear();
			mWMTabList = null;
		}
		
		if(mFilterList != null)
		{
			mFilterList.clear();
			mFilterList = null;
		}
		
		if(mFilterAdapter != null)
		{
			mFilterAdapter.clear();
			mFilterAdapter = null;
		}
		
		WMRootPath = null;
		mLoadingDialog = null;
		
		if(mBitmapSrc != null)
		{
			mBitmapSrc.recycle();
			mBitmapSrc = null;
		}
	}

}
