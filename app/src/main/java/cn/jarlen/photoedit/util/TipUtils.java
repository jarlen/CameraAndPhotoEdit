package cn.jarlen.photoedit.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Toast工具类:
		1、toast文字
		2、图片提示
		3、图片文字提示
 * @author fengmiao
 *
 */
public class TipUtils {
	static Toast toast;
	static String mText = "";
	static int mDrawableId = -12;//随便写的数字
	
	/**
	 * 文字提示
	 * 
	 * @param context
	 * @param text
	 */
	public static void ShowText(Context context, String text) {
		
		if(mText.equals(text) && isShowing())
			return;
		
		if(context != null){
			mText = text;
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}
	
	
	public static  boolean isShowing(){
		if(toast == null)
			return false;
		return (toast.getView().getWindowVisibility() == View.VISIBLE);
	}
	
	/**
	 * 图片提示
	 * 
	 * @param context
	 * @param drawableId
	 */
	public static void showImage(Context context, int drawableId) {
		
		if(mDrawableId == drawableId && isShowing())
			return;
		
		if(context != null){
			mDrawableId = drawableId;
			toast = new Toast(context);
			ImageView iv = new ImageView(context);
			iv.setImageResource(drawableId);
			toast.setView(iv);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
	}
}
