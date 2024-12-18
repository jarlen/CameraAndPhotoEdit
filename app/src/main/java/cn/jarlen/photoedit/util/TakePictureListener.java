package cn.jarlen.photoedit.util;

import android.graphics.Bitmap;

/**
 * 拍照监听接口，用以在拍照开始和结束后执行相应操作
 * 
 * @author jarlen
 *
 */
public interface TakePictureListener
{
	
	/**
	 * 拍照结束执行的动作，该方法会在onPictureTaken函数执行后触发
	 * 
	 * @param bm
	 *            拍照生成的图片
	 */
	public void onTakePictureEnd(Bitmap bm);

	/**
	 * 生成的图片的路径
	 * 
	 * @param pathImg
	 */
	public void onTakePictureEnd(String pathImg);


}
