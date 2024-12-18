package cn.jarlen.photoedit.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import java.util.List;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.util.FragmentHelper;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

/**
 * @author jarlen
 * @ClassName: CameraActivity
 * @Description: 自定义照相机类
 */
public class CameraUIActivity extends FragmentActivity implements EasyPermissions.PermissionCallbacks {

    private CameraFragment mCameraFragment;
    private ImageCropFragment mImageCropFragment;
    private ImageEditFragment mImageEditFragment;

    public static final int FragmentTypeCamera = 1;
    public static final int FragmentTypeCrop = 2;
    public static final int FragmentTypeEdit = 3;

    /* 用来标识请求gallery的activity */
    public static final int PHOTO_PICKED_WITH_DATA = 3021;

    public static final int PHOTO_PICKED_WITH_DATA_AFTER_KIKAT = 3022;

    /* 拍照获取的图片 */
    private String picturePath;

    /* 剪切获取的图片 */
    private String pictureCropPath;

    /* 发布时所需的图片剪切比例 */
    private float scale = 1.0f;

    private final String[] permissions = {
            Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.camera_activity_camera);
        CameraActivityManager.addActivity(this);

        if (EasyPermissions.hasPermissions(this, permissions)) {
            switchFragment(FragmentTypeCamera);
        } else {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, 1, permissions).build());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) {
            return;
        }

        if (mCameraFragment != null) {
            mCameraFragment.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    public void switchFragment(int flag) {

        switch (flag) {
            case FragmentTypeCamera:

                mCameraFragment = (CameraFragment) FragmentHelper.findFragment(
                        CameraUIActivity.this, CameraFragment.class);
                if (mCameraFragment == null) {
                    mCameraFragment = new CameraFragment();
                    FragmentHelper.replaceFragment(CameraUIActivity.this,
                            R.id.content, mCameraFragment);
                }

                break;
            case FragmentTypeCrop:

                mImageCropFragment = (ImageCropFragment) FragmentHelper
                        .findFragment(CameraUIActivity.this,
                                ImageCropFragment.class);

                if (mImageCropFragment == null) {
                    mImageCropFragment = new ImageCropFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("imgPath", getPicturePath());
                    mImageCropFragment.setArguments(bundle);
                    FragmentHelper.addFragmentToStack(CameraUIActivity.this,
                            R.id.content, mImageCropFragment);
                }

                break;
            case FragmentTypeEdit:

                mImageEditFragment = (ImageEditFragment) FragmentHelper
                        .findFragment(CameraUIActivity.this,
                                ImageEditFragment.class);

                if (mImageEditFragment == null) {
                    mImageEditFragment = new ImageEditFragment();
                    Bundle bundle = new Bundle();
                    bundle.putFloat("scale", getScale());
                    bundle.putString("imgPath", getEditImagePath());
                    mImageEditFragment.setArguments(bundle);
                    FragmentHelper.addFragmentToStack(CameraUIActivity.this,
                            R.id.content, mImageEditFragment);
                }

                break;
            default:
                break;
        }

    }

    /**
     * 设置拍照图片
     *
     * @param imgPath
     */
    public void setPicturePath(String imgPath) {
        this.picturePath = imgPath;
    }

    public String getPicturePath() {
        return picturePath;
    }

    /**
     * 设置剪切图片
     *
     * @param imgPath
     */
    public void setEditImagePath(String imgPath) {
        this.pictureCropPath = imgPath;
    }

    public String getEditImagePath() {
        return pictureCropPath;
    }


    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.overridePendingTransition(0, R.anim.ac_transition_fade_out);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        mCameraFragment = null;
        mImageCropFragment = null;
        mImageEditFragment = null;
        picturePath = null;
        pictureCropPath = null;
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        switchFragment(FragmentTypeCamera);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "获取所需权限失败", Toast.LENGTH_SHORT).show();
        finish();
    }
}