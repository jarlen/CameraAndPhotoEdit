package cn.jarlen.photoedit;

import android.app.Application;

import com.tencent.bugly.crashreport.CrashReport;

public class PhotoEditApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "adfdeef7c7", false);
    }
}
