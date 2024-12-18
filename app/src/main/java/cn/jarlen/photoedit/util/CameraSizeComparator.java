package cn.jarlen.photoedit.util;

import java.util.Comparator;

import android.hardware.Camera;
import android.hardware.Camera.Size;

/**
 * 相机 供比较预览或输出图片窗口大小使用
 * 
 * @author jarlen
 * 
 */
public class CameraSizeComparator implements Comparator<Size>
{

	@Override
	public int compare(Size lhs, Size rhs)
	{
		// TODO Auto-generated method stub
		if (lhs.width == rhs.width)
		{
			return 0;
		} else if (lhs.width > rhs.width)
		{
			return -1;
		} else
		{
			return 1;
		}
	}

}
