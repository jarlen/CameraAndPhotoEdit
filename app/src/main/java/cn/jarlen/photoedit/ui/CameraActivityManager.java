package cn.jarlen.photoedit.ui;

import java.util.Stack;

import android.app.Activity;

/**
 * 相机 Activity管理类
 * 
 * @author jarlen
 * 
 */
public class CameraActivityManager {

	public static Stack<Activity> activityStack;
	private static CameraActivityManager instance;

	private CameraActivityManager() {
	}

	/**
	 * 单一实例
	 */
	public static CameraActivityManager getCameraActivityManager() {
		if (instance == null) {
			instance = new CameraActivityManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 * 
	 * @param activity
	 */
	public static void addActivity(Activity activity) {
		if (null == activityStack) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 * @param activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 * 
	 * @param cls
	 *            类名
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public static void exitAllActivitys() {
		if (null == activityStack) {
			return;
		}
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}
}
