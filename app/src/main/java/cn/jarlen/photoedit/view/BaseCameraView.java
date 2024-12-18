package cn.jarlen.photoedit.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.util.CameraUtil;

public class BaseCameraView extends SurfaceView implements CameraBaseOperation
{

	public final static String TAG = "BaseCameraView";

	/** 和该View绑定的Camera对象 */
	private Camera mCamera;

	private Context mContext;

	/** 当前闪光灯类型，默认为关闭 */
	private FlashMode mFlashMode = FlashMode.OFF;

	/** 当前缩放级别 默认为0 */
	private int mZoom = 0;

	/** 当前屏幕旋转角度 */
	private int mOrientation = 0;

	/** 是否打开前置相机,true为前置,false为后置 */
	private boolean mIsFrontCamera;

	private int viewWidth, viewHeight;

	private AlertDialog errorDialog;

	public BaseCameraView(Context context)
	{
		this(context, null);
	}

	public BaseCameraView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.mContext = context;

		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay().getMetrics(dm);
		viewWidth = dm.widthPixels;
		viewHeight = dm.heightPixels;

		getHolder().addCallback(callback);

		mIsFrontCamera = false;
	}

	private SurfaceHolder.Callback callback = new SurfaceHolder.Callback()
	{
		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			try
			{
				if (mCamera == null)
				{
					boolean state = openCamera();

					if (!state)
					{
						showErrorDialog();
					} else
					{
						disMissErrorDialog();
					}
				}

				if (mCamera == null)
				{
					return;
				}
				mCamera.setPreviewDisplay(getHolder());
			} catch (Exception e)
			{
				Toast.makeText(getContext(), "打开相机失败", Toast.LENGTH_SHORT)
						.show();
				Log.e(TAG, e.getMessage());
			}

		}

		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height)
		{
			if (mCamera != null)
			{
				setCameraParameters(viewWidth, viewHeight);
				updateCameraOrientation();
				mCamera.startPreview();
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{
			if (mCamera != null)
			{
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}

		}
	};

	/**
	 * 转换前置和后置照相机
	 */
	@Override
	public void switchCamera()
	{
		mIsFrontCamera = !mIsFrontCamera;
		boolean state = openCamera();
		mZoom = 0;
		if (mCamera != null)
		{
			setCameraParameters(viewWidth, viewHeight);
			updateCameraOrientation();
			try
			{
				mCamera.setPreviewDisplay(getHolder());
				mCamera.startPreview();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!state)
		{
			showErrorDialog();
		} else
		{
			disMissErrorDialog();
		}
	}

	/**
	 * 根据当前照相机状态(前置或后置)，打开对应相机
	 */
	private boolean openCamera()
	{

		if (mCamera != null)
		{
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		if (mIsFrontCamera)
		{
			CameraInfo cameraInfo = new CameraInfo();
			for (int i = 0; i < Camera.getNumberOfCameras(); i++)
			{
				Camera.getCameraInfo(i, cameraInfo);
				if (cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT)
				{
					try
					{
						mCamera = Camera.open(i);
					} catch (Exception e)
					{
						mCamera = null;
						return false;
					}

				}
			}
		} else
		{
			try
			{
				mCamera = Camera.open();
			} catch (Exception e)
			{
				Log.e("===", e.toString());
				mCamera = null;
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取当前闪光灯类型
	 * 
	 * @return
	 */
	@Override
	public FlashMode getFlashMode()
	{
		return mFlashMode;
	}

	/**
	 * 设置闪光灯类型
	 * 
	 * @param flashMode
	 */
	@Override
	public void setFlashMode(FlashMode flashMode)
	{
		if (mCamera == null)
		{
			return;
		}

		Camera.Parameters parameters = mCamera.getParameters();
		if(parameters == null || parameters.flatten().isEmpty())
		{
			return;
		}
		
		mFlashMode = flashMode;
		

		List<String> focusList = null;
		try
		{
			focusList = parameters.getSupportedFlashModes();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		if (focusList == null || focusList.size() == 0)
		{
			return;
		}

		switch (flashMode)
		{
			case ON :
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				break;
			case AUTO :
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
				break;
			case TORCH :
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
				break;
			default :
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				break;
		}
		mCamera.setParameters(parameters);
	}
	@Override
	public void takePicture(PictureCallback callback,
			TakePictureListener listener)
	{
		if (mCamera != null)
		{
			if (callback != null)
			{
				mCamera.takePicture(null, null, callback);
			}
		}
	}

	/**
	 * 手动聚焦
	 * 
	 * @param point
	 *            触屏坐标
	 */
	protected void onFocus(Point point, AutoFocusCallback callback)
	{
		if (mCamera == null)
		{
			return;
		}

		Camera.Parameters parameters = mCamera.getParameters();
		if(parameters == null || parameters.flatten().isEmpty())
		{
			return;
		}

		List<String> focusList = null;
		try
		{
			focusList = parameters.getSupportedFlashModes();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		if (focusList == null || focusList.size() == 0)
		{
			return;
		}

		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

		Rect focusRect = calculateTapArea(point.x, point.y, 1f);
		Rect meteringRect = calculateTapArea(point.x, point.y, 1.5f);

		// 不支持设置自定义聚焦，则使用自动聚焦，返回
		if (parameters.getMaxNumFocusAreas() > 0)
		{
			List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
			focusAreas.add(new Camera.Area(focusRect, 1000));
			parameters.setFocusAreas(focusAreas);
		}

		if (parameters.getMaxNumMeteringAreas() > 0)
		{
			List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
			meteringAreas.add(new Camera.Area(meteringRect, 1000));
			parameters.setMeteringAreas(meteringAreas);
		}

		mCamera.setParameters(parameters);
		mCamera.autoFocus(callback);
	}

	/**
	 * 获取最大缩放级别，最大为40
	 * 
	 * @return
	 */
	@Override
	public int getMaxZoom()
	{
		if (mCamera == null)
		{
			return -1;
		}

		Camera.Parameters parameters = mCamera.getParameters();
		if(parameters == null || parameters.flatten().isEmpty())
		{
			return -1;
		}
		
		if (!parameters.isZoomSupported())
		{
			return -1;
		}

		return parameters.getMaxZoom() > CameraUtil.MAX_ZOOM
				? CameraUtil.MAX_ZOOM
				: parameters.getMaxZoom();
	}

	public Boolean isFrontCamera()
	{
		return mIsFrontCamera;
	}

	/**
	 * 设置相机缩放级别
	 * 
	 * @param zoom
	 */
	@Override
	public void setZoom(int zoom)
	{
		if (mCamera == null)
		{
			return;
		}

		Camera.Parameters parameters = mCamera.getParameters();
		if(parameters == null || parameters.flatten().isEmpty())
		{
			return;
		}

		if (!parameters.isZoomSupported())
		{
			return;
		}
		parameters.setZoom(zoom);
		mCamera.setParameters(parameters);
		mZoom = zoom;
	}
	@Override
	public int getZoom()
	{
		return mZoom;
	}

	/**
	 * 设置照相机参数
	 */
	private void setCameraParameters(int viewWidth, int viewHeight)
	{
		Camera.Parameters parameters = mCamera.getParameters();
		if(parameters == null || parameters.flatten().isEmpty())
		{
			return;
		}

		// if(parameters == null){
		// return;
		// }
		// 选择合适的预览尺寸
		List<Size> mPreviewSizes = parameters.getSupportedPreviewSizes();
		if (mPreviewSizes != null && mPreviewSizes.size() > 0)
		{
			Size size = CameraUtil.getOptimalPreviewSize(mPreviewSizes,
					viewWidth, viewHeight);

			Log.d("===", "setPreviewSize width = " + size.width + " height = "
					+ size.height);
			parameters.setPreviewSize(size.width, size.height);

		}

		// 设置生成的图片大小
		List<Size> supportedPictureSizes = parameters
				.getSupportedPictureSizes();

		Size size = parameters.getPreviewSize();
		Size pictureSize = CameraUtil.getPropPictureSize(supportedPictureSizes,
				size);

		Log.d("===", "setPictureSize width = " + pictureSize.width
				+ " height = " + pictureSize.height);

		// 1840x3264
		parameters.setPictureSize(pictureSize.width, pictureSize.height);
		// 设置图片格式
		parameters.setPictureFormat(ImageFormat.JPEG);

		parameters.set("jpeg-quality", 80);
		mCamera.setParameters(parameters);

		if (!mIsFrontCamera)
		{
			// 后置摄像头无闪光灯
			setFlashMode(mFlashMode);
		}
		// 设置缩放级别
		setZoom(mZoom);
		// 开启屏幕朝向监听
		startOrientationChangeListener();
	}

	/**
	 * 启动屏幕朝向改变监听函数 用于在屏幕横竖屏切换时改变保存的图片的方向
	 */
	private void startOrientationChangeListener()
	{
		OrientationEventListener mOrEventListener = new OrientationEventListener(
				getContext())
		{
			@Override
			public void onOrientationChanged(int rotation)
			{

				if (((rotation >= 0) && (rotation <= 45)) || (rotation > 315))
				{
					rotation = 0;
				} else if ((rotation > 45) && (rotation <= 135))
				{
					rotation = 90;
				} else if ((rotation > 135) && (rotation <= 225))
				{
					rotation = 180;
				} else if ((rotation > 225) && (rotation <= 315))
				{
					rotation = 270;
				} else
				{
					rotation = 0;
				}
				if (rotation == mOrientation)
					return;
				mOrientation = rotation;
				updateCameraOrientation();
			}
		};
		mOrEventListener.enable();
	}

	/**
	 * 根据当前朝向修改保存图片的旋转角度
	 */
	private void updateCameraOrientation()
	{
		if (mCamera != null)
		{
			Camera.Parameters parameters = mCamera.getParameters();
			if(parameters == null || parameters.flatten().isEmpty())
			{
				return;
			}
			// rotation参数为 0、90、180、270。水平方向为0。
			int rotation = 90 + mOrientation == 360 ? 0 : 90 + mOrientation;
			// 前置摄像头需要对垂直方向做变换，否则照片是颠倒的
			if (mIsFrontCamera)
			{
				if (rotation == 90)
					rotation = 270;
				else if (rotation == 270)
					rotation = 90;
			}
			parameters.setRotation(rotation);// 生成的图片转90°
			// 预览图片旋转90°
			mCamera.setDisplayOrientation(90);// 预览转90°
			mCamera.setParameters(parameters);
		}
	}

	private Rect calculateTapArea(float x, float y, float coefficient)
	{
		float focusAreaSize = 300;

		int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();

		int centerX = (int) (x / viewWidth * 2000 - 1000);
		int centerY = (int) (y / viewHeight * 2000 - 1000);

		int left = Math.max(centerX - areaSize / 2, -1000);
		int right = Math.min(left + areaSize, 1000);
		int top = Math.max(centerY - areaSize / 2, -1000);
		int bottom = Math.min(top + areaSize, 1000);

		return new Rect(left, top, right, bottom);
	}

	private CameraErrorDialogCallBack mCameraErrorDialogCallBack;

	public void setCameraErrorDialogCallBack(CameraErrorDialogCallBack callback)
	{
		this.mCameraErrorDialogCallBack = callback;
	}

	public interface CameraErrorDialogCallBack
	{
		public void errorDialogCallBack();
	}

	/**
	 * 显示打开相机失败的dialog
	 */
	private void showErrorDialog()
	{
		if (errorDialog == null)
		{
			View dialogView = LayoutInflater.from(mContext).inflate(
					R.layout.dialog_camera_permission_error, null);
			errorDialog = new AlertDialog.Builder(mContext).create();
			errorDialog.show();
			errorDialog.getWindow().setContentView(dialogView);
		} else
		{
			errorDialog.show();
		}

		errorDialog.getWindow().findViewById(R.id.dialog_btn)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{

						if (mCameraErrorDialogCallBack != null)
						{
							mCameraErrorDialogCallBack.errorDialogCallBack();
							errorDialog.dismiss();
						}

					}
				});

	}

	/**
	 * 隐藏打开相机失败的dialog
	 */
	private void disMissErrorDialog()
	{
		if (errorDialog != null)
		{
			if (errorDialog.isShowing())
			{
				errorDialog.dismiss();
			}
		}
	}

	public void recycle()
	{
		if (mCamera != null)
		{
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}

		mFlashMode = null;

		errorDialog = null;
		mContext = null;
	}
}