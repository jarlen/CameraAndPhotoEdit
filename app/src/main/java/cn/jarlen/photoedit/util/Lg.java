package cn.jarlen.photoedit.util;

import android.util.Log;

/**
 * 打印tag值为"MyClass.java(11)"格式的日志。（数字表示行号）
 * 
 * @author fengmiao
 * 
 */
public class Lg {
	private static final String CLASS_METHOD_LINE_FORMAT = "%s(%d)";
	
	public static boolean LogEnable = false;

	public static void e(String msg) {
		if (LogEnable) {// 获取堆栈信息会影响性能，发布应用时不执行下面代码
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.e(tag, msg);// 打印Log
		}
	}
	
	public static void e(String msg,Throwable throwable){
		if(LogEnable){
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.e(tag, msg, throwable);
		}
	}
	

	public static void v(String msg) {
		if (LogEnable) {// 获取堆栈信息会影响性能，发布应用时不执行下面代码
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.v(tag, msg);// 打印Log
		}
	}
	
	public static void v(String msg,Throwable throwable){
		if(LogEnable){
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.v(tag, msg, throwable);
		}
	}
	
	
	public static void i(String msg){
		if (LogEnable) {// 获取堆栈信息会影响性能，发布应用时不执行下面代码
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.i(tag, msg);// 打印Log
		}
	}
	
	
	public static void i(String msg,Throwable throwable){
		if(LogEnable){
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.i(tag, msg, throwable);
		}
	}
	
	public static void d(String msg){
		if (LogEnable) {// 获取堆栈信息会影响性能，发布应用时不执行下面代码
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.d(tag, msg);// 打印Log
		}
	}
	
	
	public static void d(String msg,Throwable throwable){
		if(LogEnable){
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.d(tag, msg, throwable);
		}
	}
	
	public static void w(String msg){
		if (LogEnable) {// 获取堆栈信息会影响性能，发布应用时不执行下面代码
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.w(tag, msg);// 打印Log
		}
	}
	
	
	public static void w(String msg,Throwable throwable){
		if(LogEnable){
			StackTraceElement traceElement = new Throwable().getStackTrace()[1];// 从堆栈信息中获取当前被调用的方法信息

			String tag = String.format(CLASS_METHOD_LINE_FORMAT,
					traceElement.getFileName(), traceElement.getLineNumber());
			Log.w(tag, msg, throwable);
		}
	}
	
}
