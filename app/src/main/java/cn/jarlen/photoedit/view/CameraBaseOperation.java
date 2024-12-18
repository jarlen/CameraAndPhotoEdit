package cn.jarlen.photoedit.view;

import android.graphics.Bitmap;
import android.hardware.Camera.PictureCallback;


/**
 * 相机基础操作
 * 
 * @author jarlen
 *
 */
public interface CameraBaseOperation
{

	/**  
	 *   切换前置和后置相机
	 */
	public void switchCamera();
	/**  
	 *  获取当前闪光灯模式
	 *  @return   
	 */
	public FlashMode getFlashMode();
	/**  
	 *  设置闪光灯模式
	 *  @param flashMode   
	 */
	public void setFlashMode(FlashMode flashMode);
	/**  
	 *  拍照
	 *  @param callback 拍照回调函数 
	 *  @param listener 拍照动作监听函数  
	 */
	public void takePicture(PictureCallback callback, TakePictureListener listener);
	/**  
	 *  相机最大缩放级别
	 *  @return   
	 */
	public int getMaxZoom();
	/**  
	 *  设置当前缩放级别
	 *  @param zoom   
	 */
	public void setZoom(int zoom);
	/**  
	 *  获取当前缩放级别
	 *  @return   
	 */
	public int getZoom();
	
	
	/**
	 * @ClassName: TakePictureListener
	 * @Description: 拍照监听接口，用以在拍照开始和结束后执行相应操作
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
	
	
	/**
	 * @Description: 闪光灯类型枚举 默认为关闭
	 */
	public enum FlashMode
	{
		/** ON:拍照时打开闪光灯 */
		ON,
		/** OFF：不打开闪光灯 */
		OFF,
		/** AUTO：系统决定是否打开闪光灯 */
		AUTO,
		/** TORCH：一直打开闪光灯 */
		TORCH
	}
	

}
