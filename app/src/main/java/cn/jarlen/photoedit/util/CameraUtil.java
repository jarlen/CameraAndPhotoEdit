package cn.jarlen.photoedit.util;

import java.util.Collections;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;

/**
 * 相机辅助类
 * 
 * @author jarlen
 *
 */
public class CameraUtil
{

	public static final int MAX_PICTURE_SIZE = 6500000;
	public static final int MAX_PREVIEW_SIZE = 5000000;
	public static final int MIN_PICTURE_SIZE = 410000;
	public static final int MIN_PREVIEW_SIZE = 50000;

	public static final int MAX_ZOOM = 30;

	public static Size getOptimalPreviewSize(List<Size> sizes, int w, int h)
	{
		final double ASPECT_TOLERANCE = 0.1;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes)
		{

			double ratio = 0.0;
			if (size.height > size.width)
			{
				ratio = (double) size.width / size.height;
			} else
			{
				ratio = (double) size.height / size.width;
			}

			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
			{
				continue;
			}

			int height = size.width > size.height ? size.width : size.height;

			if (Math.abs(height - targetHeight) < minDiff)
			{
				optimalSize = size;
				minDiff = Math.abs(height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null)
		{
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes)
			{
				int height = size.width > size.height
						? size.width
						: size.height;

				if (Math.abs(height - targetHeight) < minDiff)
				{
					optimalSize = size;
					minDiff = Math.abs(height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public static Size getPropPictureSize(List<Size> list,
			Size previewSize)
	{

		Collections.sort(list, new CameraSizeComparator());
		double previewRate = (double) previewSize.width / previewSize.height;

		Size picture_Size = previewSize;

		for (Size size : list)
		{
			int pictureSize = size.width * size.height;
			double rate = (double) size.width / size.height;

			if (pictureSize < MAX_PICTURE_SIZE
					&& Math.abs(previewRate - rate) < 0.03)
			{
				picture_Size = size;
				break;
			}
		}

		return picture_Size;
	}

	private static boolean checkCameraFacing(final int facing)
	{
		final int cameraCount = Camera.getNumberOfCameras();
		CameraInfo info = new CameraInfo();
		for (int i = 0; i < cameraCount; i++)
		{
			Camera.getCameraInfo(i, info);
			if (facing == info.facing)
			{
				return true;
			}
		}
		return false;
	}
	public static boolean hasBackFacingCamera()
	{
		final int CAMERA_FACING_BACK = 0;
		return checkCameraFacing(CAMERA_FACING_BACK);
	}
	public static boolean hasFrontFacingCamera()
	{
		final int CAMERA_FACING_BACK = 1;
		return checkCameraFacing(CAMERA_FACING_BACK);
	}
}
