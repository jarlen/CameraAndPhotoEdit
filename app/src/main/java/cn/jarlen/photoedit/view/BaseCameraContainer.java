package cn.jarlen.photoedit.view;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.util.CameraUtil;
import cn.jarlen.photoedit.util.FileUtils;

/**
 * 
 * @author jarlen
 * 
 */
public class BaseCameraContainer extends RelativeLayout
		implements
			CameraBaseOperation
{

	public final static String TAG = "CameraContainer";

	/** 相机绑定的SurfaceView */
	private BaseCameraView mCameraView;

	/** 触摸屏幕时显示的聚焦图案 */
	private BaseFocusImageView mFocusImageView;

	private String mImagePath;;

	/** 照片字节流处理类 */
	private DataHandler mDataHandler;

	/** 拍照监听接口，用以在拍照开始和结束后执行相应操作 */
	private TakePictureListener mListener;

	private boolean isTaking = false;

	// 设置焦点
	private Point point = null;

	public BaseCameraContainer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initView(context);
		setOnTouchListener(new TouchListener());
	}

	/**
	 * 初始化子控件
	 * 
	 * @param context
	 */
	private void initView(Context context)
	{
		inflate(context, R.layout.base_camera_container, this);
		mCameraView = (BaseCameraView) findViewById(R.id.cameraView);
		mFocusImageView = (BaseFocusImageView) findViewById(R.id.focusImageView);
	}

	/**
	 * 前置、后置摄像头转换
	 */
	@Override
	public void switchCamera()
	{
		if (mCameraView == null)
		{
			return;
		}
		if (isTaking)
		{
			return;
		}
		mCameraView.switchCamera();
	}
	/**
	 * 获取当前闪光灯类型
	 * 
	 * @return
	 */
	@Override
	public FlashMode getFlashMode()
	{
		return mCameraView.getFlashMode();
	}

	/**
	 * 设置闪光灯类型
	 * 
	 * @param flashMode
	 */
	@Override
	public void setFlashMode(FlashMode flashMode)
	{
		if (mCameraView == null)
		{
			return;
		}

		if (isTaking)
		{
			return;
		}
		mCameraView.setFlashMode(flashMode);
	}

	/**
	 * 拍照方法
	 *
	 */
	public void takePicture()
	{
		takePicture(pictureCallback, null);
	}

	/**
	 * @Description: 拍照方法
	 * @param @param listener 拍照监听接口
	 * @return void
	 * @throws
	 */
	public void takePicture(TakePictureListener listener)
	{
		if (mCameraView == null)
		{
			return;
		}

		if (isTaking)
		{
			return;
		}

		isTaking = true;
		this.mListener = listener;

		takePicture(pictureCallback, mListener);
	}

	@Override
	public void takePicture(PictureCallback callback,
			TakePictureListener listener)
	{
		mCameraView.takePicture(callback, listener);
	}

	public void setCameraErrorDialogCallBack(BaseCameraView.CameraErrorDialogCallBack callback)
	{
		if (mCameraView == null)
		{
			return;
		}
		mCameraView.setCameraErrorDialogCallBack(callback);
	}

	@Override
	public int getMaxZoom()
	{
		return mCameraView.getMaxZoom();
	}

	public Boolean hasBackFacingCamera()
	{
		return CameraUtil.hasBackFacingCamera();
	}

	public Boolean hasFrontFacingCamera()
	{
		return CameraUtil.hasFrontFacingCamera();
	}

	@Override
	public void setZoom(int zoom)
	{
		mCameraView.setZoom(zoom);
	}

	@Override
	public int getZoom()
	{
		return mCameraView.getZoom();
	}

	public boolean isFrontCamera()
	{
		return mCameraView.isFrontCamera();
	}

	private final AutoFocusCallback autoFocusCallback = new AutoFocusCallback()
	{

		@Override
		public void onAutoFocus(boolean success, Camera camera)
		{
			// 聚焦之后根据结果修改图片
			if (success)
			{
				if (mFocusImageView != null)
				{
					mFocusImageView.onFocusSuccess();
				}

			} else
			{
				// 聚焦失败显示的图片，由于未找到合适的资源，这里仍显示同一张图片
				if (mFocusImageView != null)
				{
					mFocusImageView.onFocusFailed();
				}
			}
		}
	};

	private final PictureCallback pictureCallback = new PictureCallback()
	{

		@Override
		public void onPictureTaken(byte[] data, Camera camera)
		{

			if (mDataHandler == null)
			{
				mDataHandler = new DataHandler();
			}

			mDataHandler.setMaxSize(200);
			Bitmap bm = mDataHandler.save(data);

			// 重新打开预览图，进行下一次的拍照准备
			if (camera != null)
			{
				camera.startPreview();
			}

			isTaking = false;

			if (mListener != null)
			{
				mListener.onTakePictureEnd(bm);
				mListener.onTakePictureEnd(mImagePath);
			}

		}
	};

	private final class TouchListener implements OnTouchListener
	{

		/** 记录是拖拉照片模式还是放大缩小照片模式 */

		private static final int MODE_INIT = 0;
		/** 放大缩小照片模式 */
		private static final int MODE_ZOOM = 1;
		private int mode = MODE_INIT;// 初始状态

		/** 用于记录拖拉图片移动的坐标位置 */

		private float startDis;

		@Override
		public boolean onTouch(View v, MotionEvent event)
		{
			/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
			// 手指压下屏幕
				case MotionEvent.ACTION_DOWN :
					mode = MODE_INIT;
					break;
				case MotionEvent.ACTION_POINTER_DOWN :

					mode = MODE_ZOOM;
					/** 计算两个手指间的距离 */
					startDis = distance(event);

					break;
				case MotionEvent.ACTION_MOVE :
					if (mode == MODE_ZOOM)
					{
						// 只有同时触屏两个点的时候才执行
						if (event.getPointerCount() < 2)
							return true;
						float endDis = distance(event);// 结束距离
						// 每变化10f zoom变1
						int scale = (int) ((endDis - startDis) / 10f);
						if (scale >= 1 || scale <= -1)
						{
							int zoom = mCameraView.getZoom() + scale;
							// zoom不能超出范围
							if (zoom > mCameraView.getMaxZoom())
								zoom = mCameraView.getMaxZoom();
							if (zoom < 0)
								zoom = 0;
							mCameraView.setZoom(zoom);
							// 将最后一次的距离设为当前距离
							startDis = endDis;
						}
					}
					break;
				// 手指离开屏幕
				case MotionEvent.ACTION_UP :
					if (mode != MODE_ZOOM)
					{
						// 设置聚焦
						if (point == null)
						{
							point = new Point();
						}

						point.set((int) event.getX(), (int) event.getY());

						if (!mCameraView.isFrontCamera())
						{
							if (!isTaking)
							{
								mCameraView.onFocus(point, autoFocusCallback);
								mFocusImageView.startFocus(point);
							}
						}
					}

					break;
			}
			return true;
		}
		/** 计算两个手指间的距离 */
		private float distance(MotionEvent event)
		{
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** 使用勾股定理返回两点之间的距离 */
			return (float) Math.sqrt(dx * dx + dy * dy);
		}

	}

	/**
	 * 拍照返回的byte数据处理类
	 * 
	 * @author linj
	 * 
	 */
	private final class DataHandler
	{
		/** 大图存放路径 */
		private String mThumbnailFolder;
		/** 小图存放路径 */
		private String mImageFolder;
		/** 压缩后的图片最大值 单位KB */
		private int maxSize = 200;

		public DataHandler()
		{
			mImageFolder = FileUtils.DCIMCamera_PATH;
			File folder = new File(mImageFolder);

			if (!folder.exists())
			{
				folder.mkdirs();
			}

			folder = null;
		}

		/**
		 * 保存图片
		 * 
		 * @param data
		 * @return 解析流生成的缩略图
		 */
		public Bitmap save(byte[] data)
		{
			if (data != null)
			{
				// 解析生成相机返回的图片
				Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);

				// 产生新的文件名
				String imgName = FileOperateUtil.createFileNmae(".jpg");
				String imagePath = mImageFolder + File.separator + imgName;

				FileUtils.writeImage(bm, imagePath, 95);

				if (bm != null)
				{
					bm.recycle();
					bm = null;
				}

				mImagePath = imagePath;

			} else
			{
				Toast.makeText(getContext(), "拍照失败，请重试", Toast.LENGTH_SHORT)
						.show();
			}
			return null;
		}
		public void setMaxSize(int maxSize)
		{
			this.maxSize = maxSize;
		}
	}

	public void recycle()
	{
		mListener = null;
		mDataHandler = null;
		mImagePath = null;

		if (mFocusImageView != null)
		{
			mFocusImageView.recycle();
			mFocusImageView = null;
		}

		if (mCameraView != null)
		{
			mCameraView.recycle();
			mCameraView = null;
		}

		point = null;
	}

}
