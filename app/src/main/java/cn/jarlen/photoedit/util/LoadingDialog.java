package cn.jarlen.photoedit.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import cn.jarlen.photoedit.R;

@SuppressLint("HandlerLeak")
public class LoadingDialog {
	private Dialog dialog;
	View contentView;
	View bg_iv;
	View circle_iv;
	RotateAnimation rotate;
	
	OnDismissListener mDismissListener;
	
	public void setOnDismissListener(OnDismissListener mDismissListener){
		this.mDismissListener = mDismissListener;
		dialog.setOnDismissListener(mDismissListener);
	}

	public LoadingDialog(Context context) {
		dialog = new Dialog(context, R.style.LoadingDialogTheme);
		LayoutInflater inflater = LayoutInflater.from(context);
		contentView = inflater.inflate(R.layout.loadingdialog3, null);
		bg_iv = contentView.findViewById(R.id.loadingdialog3_bg);
		circle_iv = contentView.findViewById(R.id.loadingdialog3_circle);
		dialog.setContentView(contentView);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				circle_iv.clearAnimation();
			}
		});
		rotate = new RotateAnimation(0f, 180000f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		rotate.setDuration(900000);
		rotate.setInterpolator(new LinearInterpolator());
		rotate.setFillAfter(true);
		rotate.setRepeatCount(Animation.INFINITE);
	}

	public void show() {
		if (dialog != null) {
			dialog.show();
			circle_iv.startAnimation(rotate);
		}
	}

	public void dismiss() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public boolean isShowing() {
		if (dialog == null) {
			return false;
		}
		return dialog.isShowing();
	}
	public void hideBg(){
		bg_iv.setVisibility(View.GONE);
	}
}
