package cn.jarlen.photoedit.util;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;


/**
 * commentUtils 常用功能: <br>
 * ========1、根据手机的分辨率从 dp 的单位 转成为 px(像素) <br>
 * ========2、px转换成sp <br>
 * ========2、创建桌面快捷方式(需要权限：com.android.launcher.permission.INSTALL_SHORTCUT) <br>
 * ========3、判断是否已创建快捷方式(需要权限com.android.launcher.permission.READ_SETTINGS) <br>
 * ========4、隐藏软键盘 <br>
 * 4、展示软键盘 <br>
 * ========5、获取屏幕高度 <br>
 * ========6、获取屏幕宽度 <br>
 * ========7、应用评价 <br>
 * ========8、手机是否安装了市场 <br>
 * ========9、获取某个view 的宽度和高度 o[0]---width o[1]---height 网络是否可用
 * 
 * @author fengmiao
 * 
 */
public class LittleUtils {

	/**
	 * activity跳转
	 * 
	 * @param activity
	 *            ：当前activity
	 * @param cls
	 *            ：目标activity
	 * @param extras
	 *            ：intent要携带的数据，可传null
	 */
	public static void startActivity(Activity activity, Class<?> cls,
			Bundle extras) {
		try{
			Intent intent = new Intent(activity, cls);
			if (extras != null) {
				intent.putExtras(extras);
			}
			activity.startActivity(intent);
		}catch(Exception e){
			Lg.e(e.toString());
		}
	}
	
	/**
	 * activity跳转(需要回调)
	 * 
	 * @param activity
	 *            当前activity
	 * @param requestCode
	 *            请求标志
	 * @param cls
	 *            目标activity
	 * @param extras
	 *            intent要携带的数据，可传null
	 */
	public static void startActivityForResult(Activity activity, int requestCode,Class<?> cls,
			Bundle extras){
		
		try{
			Intent intent = new Intent(activity, cls);
			if (extras != null) {
				intent.putExtras(extras);
			}
			activity.startActivityForResult(intent, requestCode);
		}catch(Exception e){
			Lg.e(e.toString());
		}
		
	}

	/**
	 * 延迟跳转到某个Activity
	 * @param context
	 * @param cls
	 * @param delayMillis
	 */
	public static void delayToActivity(final Context context,final Class<?> cls,long delayMillis){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				context.startActivity(new Intent(context, cls));
			}
		}, delayMillis);
	}
	
	/**
	 * 延迟跳转到某个Activity,并且关闭当前Activity
	 * @param context
	 * @param cls
	 * @param delayMillis
	 */
	public static void delayToActivityDie(final Activity context,final Class<?> cls,long delayMillis){
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				context.startActivity(new Intent(context, cls));
				context.finish();
			}
		}, delayMillis);
	}
	
	
	/**
	 * 显示软键盘
	 * 
	 * @param context
	 */
	public static void showSoftInput(Context context) {
		try{
			if(context == null)
				return;
			
			InputMethodManager imm = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
					InputMethodManager.HIDE_NOT_ALWAYS);
		}catch(Exception e){
			Lg.e(e.toString());
		}
	}

//	/**
//	 * 隐藏软键盘
//	 * 
//	 * @param activity
//	 * @param anyView
//	 *            :activity内的任意view
//	 */
//	public static void hideSoftInput(Context context, View anyView) {
//		InputMethodManager imm = (InputMethodManager) context
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.hideSoftInputFromWindow(anyView.getWindowToken(), 0);
//	}
	
	 /**
	  * 
	  *隐藏键盘
     * Hide soft keyboard method.
     *
     * @param context
	  */
    public static void hideSoftInput(Context context) {
        try {
        	
        	if(context == null)
        		return;
        	
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (((Activity)context).getCurrentFocus() != null) {
                if (((Activity)context).getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Hide soft keyboard by click。
     *
     * @param context
     * @param motionEvent Judge by motion event
     */
    public static void hiddenKeyBoardByClick(Context context,MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            LittleUtils.hideSoftInput(context);
        }
    }

	

	

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param phoneNumber
	 */
	public static void call(Context context, String phoneNumber) {
		try {
			Intent myIntentDial = new Intent(Intent.ACTION_CALL,
					Uri.parse("tel:" + phoneNumber));
			context.startActivity(myIntentDial);
		} catch (Exception e) {
			TipUtils.ShowText(context, "操作失败");
		}
	}
	/**
	 * 分享图片
	 * @param context
	 * @param file
	 */
	public static void shareImage(final Context context, File file) {

		if (file != null) {
			try {
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("image/*");
				Uri u = Uri.fromFile(file);
				intent.putExtra(Intent.EXTRA_STREAM, u);
				context.startActivity(Intent.createChooser(intent, "分享"));
			} catch (Exception e) {
				TipUtils.ShowText(context, "操作失败");
			}
		}

	}
	
	/**
	 * 打开浏览器
	 * @param context
	 * @param url
	 */
	public static void openBroswer(Context context,String url) {
		try{
			Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  
//	        it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");  
	        context.startActivity(it); 
		}catch(Exception e){
			TipUtils.ShowText(context, "暂无应用可执行此操作");
		}
	}
	
	/**
	 * 打开图片
	 * @param context
	 * @param path
	 */
	public static void openImage(Context context,String path) {
		if (path != null && path.length() > 0 && context != null) {
			try{
				Intent intent = new Intent(Intent.ACTION_VIEW);     
				intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
				context.startActivity(intent);
			}catch(Exception e){
				TipUtils.ShowText(context, "操作失败");
			}
		}
	}
	
	
	/**
	 * 提示框
	 * @param context
	 * @param message
	 */
	public static void showInfoDialog(Context context, String message) {
		showInfoDialog(context, message, "提示", "确定", null);
	}
	
	/**
	 * 提示框
	 * @param context
	 * @param message
	 * @param titleStr
	 * @param positiveStr
	 * @param onClickListener
	 */
	public static void showInfoDialog(Context context, String message,
			String titleStr, String positiveStr,
			DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
		localBuilder.setTitle(titleStr);
		localBuilder.setMessage(message);
		if (onClickListener == null)
			onClickListener = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			};
		localBuilder.setPositiveButton(positiveStr, onClickListener);
		localBuilder.show();
	}
	
	/**
	 * 重置listview的高度
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}
	
	/**
	 * 将百分数转换成小数，然后乘5
	 * 
	 * @param percent
	 * @return
	 */
	public static float percent2float(String percent) {
		if (percent.contains("%")) {
			percent = percent.replaceAll("%", "");
			Float f = Float.valueOf(percent);
			return 5 * f / 100;
		}
		return 0;
	}
	
	/**
	 * 将运势百分比转化为float
	 * @param percent
	 * @return
	 */
	public static float percent2float2(String percent) {
		if (percent.contains("%")) {
			percent = percent.replaceAll("%", "");
			Float f = Float.valueOf(percent);
			return  f / 100;
		}
		return 0;
	}
}
