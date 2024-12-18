package cn.jarlen.photoedit.ui;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.crop.CropImageSampleView;
import cn.jarlen.photoedit.util.FileOperateUtil;
import cn.jarlen.photoedit.util.FileUtils;
import cn.jarlen.photoedit.util.FragmentHelper;
import cn.jarlen.photoedit.util.LoadingDialog;


public class ImageCropFragment extends BaseFragment implements OnClickListener {

	private CameraUIActivity mActivity;

	/** 剪切view **/
	private CropImageSampleView mCropImageSampleView;

	/** 剪切切换选择 **/
	private Button cropChoose43Btn, cropChoose11Btn, cropChoose34Btn;

	/** 旋转按钮 **/
	private Button imageCropRotateBtn;

	/** 剪切按钮 **/
	private ImageView imageCropOkBtn;

	private String imagePath;

	/** 被剪切的图片 **/
	private Bitmap mBitmapSrc;

	private LoadingDialog mLoadingDialog;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (CameraUIActivity) activity;
	}

	@Override
	public void onClick(View view) {

		if (view.getId() == R.id.cropChoose43) {
			mCropImageSampleView.setCropScale(40, 30);
			cropChoose43Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_chosen));
			cropChoose43Btn.setTextColor(Color.parseColor("#FFB989EA"));

			cropChoose11Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_choose));
			cropChoose11Btn.setTextColor(Color.parseColor("#FF535477"));
			cropChoose34Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_choose));
			cropChoose34Btn.setTextColor(Color.parseColor("#FF535477"));
		} else if (view.getId() == R.id.cropChoose11) {
			mCropImageSampleView.setCropScale(30, 30);
			cropChoose11Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_chosen));
			cropChoose11Btn.setTextColor(Color.parseColor("#FFB989EA"));

			cropChoose43Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_choose));
			cropChoose43Btn.setTextColor(Color.parseColor("#FF535477"));

			cropChoose34Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_choose));
			cropChoose34Btn.setTextColor(Color.parseColor("#FF535477"));

		} else if (view.getId() == R.id.cropChoose34) {
			mCropImageSampleView.setCropScale(30, 40);
			cropChoose34Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_chosen));
			cropChoose34Btn.setTextColor(Color.parseColor("#FFB989EA"));

			cropChoose11Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_choose));
			cropChoose11Btn.setTextColor(Color.parseColor("#FF535477"));
			cropChoose43Btn.setBackgroundDrawable(mActivity.getResources()
					.getDrawable(R.drawable.camera_shape_crop_choose));
			cropChoose43Btn.setTextColor(Color.parseColor("#FF535477"));
		} else if (view.getId() == R.id.imageCropRotate) {
			mCropImageSampleView.rotateBGDrawable(90);
		} else if (view.getId() == R.id.imageCropOk) {

			imageCropOkBtn.setClickable(false);

			Bitmap bitmap = mCropImageSampleView.getCropImage();

			if (bitmap == null) {
				imageCropOkBtn.setClickable(true);
				return;
			}

			if (mLoadingDialog != null) {
				mLoadingDialog.show();
			}

			String mImageCropPath = FileOperateUtil.getFolderPath(mActivity,
					FileOperateUtil.TYPE_IMAGE, "constellation")
					+ File.separator + FileOperateUtil.createFileNmae(".jpg");

			FileUtils.writeImage(bitmap, mImageCropPath, 90);

			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}

			mActivity.setScale(mCropImageSampleView.getCropScale());
			mActivity.setEditImagePath(mImageCropPath);
			mActivity.switchFragment(mActivity.FragmentTypeEdit);
			imageCropOkBtn.setClickable(true);

			if (bitmap != null) {
				bitmap.recycle();
				bitmap = null;
			}

		} else if (view.getId() == R.id.image_crop_back) {
			FragmentHelper.popBackStack(mActivity);
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		recycle();

		mActivity = null;
		imagePath = null;
		cropChoose43Btn = null;
		cropChoose11Btn = null;
		cropChoose34Btn = null;
		imageCropRotateBtn = null;
		imageCropOkBtn = null;
		mLoadingDialog = null;

	}

	private void recycle() {
		if (mCropImageSampleView != null) {
			mCropImageSampleView.recycle();
			mCropImageSampleView = null;
		}

		if (mBitmapSrc != null) {
			mBitmapSrc.recycle();
			mBitmapSrc = null;
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.camera_fragment_image_crop;
	}

	@Override
	public void initViews(View root) {
		imagePath = getArguments().getString("imgPath");
		Log.i("jarlen","initViews  imagePath = "+imagePath);
		root.findViewById(R.id.image_crop_back).setOnClickListener(this);
		mCropImageSampleView = (CropImageSampleView) root
				.findViewById(R.id.cropImgView);
		Options options = new Options();
		options.inSampleSize = 1;

		try {
			mBitmapSrc = BitmapFactory.decodeFile(imagePath, options);
		} catch (Exception e) {
			e.printStackTrace();
			mBitmapSrc = null;
		}

		if (mBitmapSrc == null) {
			FragmentHelper.popBackStack(mActivity);
			recycle();
		}
		mCropImageSampleView.setBGBitmap(mBitmapSrc);

		cropChoose43Btn = (Button) root.findViewById(R.id.cropChoose43);
		cropChoose11Btn = (Button) root.findViewById(R.id.cropChoose11);
		cropChoose34Btn = (Button) root.findViewById(R.id.cropChoose34);
		imageCropRotateBtn = (Button) root.findViewById(R.id.imageCropRotate);
		imageCropOkBtn = (ImageView) root.findViewById(R.id.imageCropOk);

		cropChoose43Btn.setOnClickListener(this);
		cropChoose11Btn.setOnClickListener(this);
		cropChoose34Btn.setOnClickListener(this);
		imageCropRotateBtn.setOnClickListener(this);
		imageCropOkBtn.setOnClickListener(this);

		mLoadingDialog = new LoadingDialog(mActivity);
	}

}
