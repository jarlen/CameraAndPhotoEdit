package cn.jarlen.photoedit.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.util.FileUtils;
import cn.jarlen.photoedit.util.TipUtils;
import cn.jarlen.photoedit.view.BaseCameraContainer;
import cn.jarlen.photoedit.view.BaseCameraView;
import cn.jarlen.photoedit.view.CameraBaseOperation;

/**
 * 相机
 *
 * @author jarlen
 */
public class CameraFragment extends BaseFragment implements OnClickListener,
        CameraBaseOperation.TakePictureListener {

    private CameraUIActivity mActivity;

    /**
     * 相机模块View
     **/
    private BaseCameraContainer mContainer;

    /**
     * 拍照按钮
     **/
    private ImageView mTakePictureBtn;

    /**
     * 切换闪光灯
     **/
    private ImageView mSwitchFlashModeBtn;

    /**
     * 切换摄像头
     **/
    private ImageView mSwitchCameraBtn;

    /**
     * 获取相册
     **/
    private ImageView mAlbumBtn;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.mActivity = (CameraUIActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 设置拍摄照片的路径
     */
    private void initData() {
        String albumThumbnailPath = FileUtils.getAlbumThumbnailPath(mActivity);

        if (albumThumbnailPath != null) {

            File imageFile = new File(albumThumbnailPath);

            if (imageFile.exists()) {
                Bitmap thumbnailBitmap = BitmapFactory
                        .decodeFile(albumThumbnailPath);
                thumbnailBitmap = ThumbnailUtils.extractThumbnail(
                        thumbnailBitmap, 100, 100);
                mAlbumBtn.setImageBitmap(thumbnailBitmap);

            } else {
                mAlbumBtn.setBackgroundResource(R.drawable.camera_photo_img);
            }

        } else {
            mAlbumBtn.setBackgroundResource(R.drawable.camera_photo_img);
        }

        if (mContainer != null) {
            mContainer.setFlashMode(CameraBaseOperation.FlashMode.OFF);
        }

    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.camera_capture) {
            /** 拍照 **/
            if (!FileUtils.isSDAvailable()) {
                TipUtils.ShowText(mActivity, "SD卡拔出,拍照等功能不可用");
                return;
            }

            mAlbumBtn.setClickable(false);
            mTakePictureBtn.setClickable(false);

            if (mContainer == null) {
                return;
            }
            mContainer.takePicture(this);

        } else if (view.getId() == R.id.camera_flash_mode) {
            /** 闪光灯 **/

            if (mContainer == null) {
                return;
            }

            if (mContainer.getFlashMode() == CameraBaseOperation.FlashMode.OFF) {
                mContainer.setFlashMode(CameraBaseOperation.FlashMode.AUTO);
                mSwitchFlashModeBtn
                        .setImageResource(R.drawable.camera_flash_auto_btn);
            } else if (mContainer.getFlashMode() == CameraBaseOperation.FlashMode.AUTO) {
                mContainer.setFlashMode(CameraBaseOperation.FlashMode.ON);
                mSwitchFlashModeBtn
                        .setImageResource(R.drawable.camera_flash_on_btn);
            } else if (mContainer.getFlashMode() == CameraBaseOperation.FlashMode.ON) {
                mContainer.setFlashMode(CameraBaseOperation.FlashMode.OFF);
                mSwitchFlashModeBtn
                        .setImageResource(R.drawable.camera_flash_off_btn);
            }
        } else if (view.getId() == R.id.camera_switch) {
            /** 相机前后置切换 **/

            if (mContainer == null) {
                return;
            }

            mContainer.switchCamera();
            if (mContainer.isFrontCamera()) {
                mSwitchFlashModeBtn.setVisibility(View.GONE);
            } else {
                mSwitchFlashModeBtn.setVisibility(View.VISIBLE);
            }
        } else if (view.getId() == R.id.camera_album) {
            if (!FileUtils.isSDAvailable()) {
                TipUtils.ShowText(mActivity, "SD卡拔出,图片等功能不可用");
                return;
            }

            getPictureFromPhoto();
        } else if (view.getId() == R.id.camera_back) {
            if (mActivity != null)
                mActivity.onBackPressed();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mContainer != null) {
            mContainer.recycle();
            mContainer = null;
        }
        mTakePictureBtn = null;
        mSwitchFlashModeBtn = null;
        mSwitchCameraBtn = null;
        mAlbumBtn = null;
        mActivity = null;
    }

    /**
     * 从相册中获取照片
     **/
    private void getPictureFromPhoto() {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent openphotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openphotoIntent.setType("image/*");

        if (isKitKat) {
            startActivityForResult(openphotoIntent,
                    mActivity.PHOTO_PICKED_WITH_DATA_AFTER_KIKAT);
        } else {
            startActivityForResult(openphotoIntent,
                    mActivity.PHOTO_PICKED_WITH_DATA);
        }

    }

    /**
     * 从相册中获取图片回调，供Activity调用
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == mActivity.PHOTO_PICKED_WITH_DATA) {

            if (data == null) {
                TipUtils.ShowText(mActivity, "选取图片失败");
                return;
            }
            Uri originalUri = data.getData();

            if (originalUri == null || originalUri.getPath() == null) {
                TipUtils.ShowText(mActivity, "选取图片失败");
                return;
            }

            String[] filePathColumn = {MediaColumns.DATA};

            Cursor cursor = mActivity.getContentResolver().query(originalUri,
                    filePathColumn, null, null, null);

            String picturePath = null;

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                } else {
                    TipUtils.ShowText(mActivity, "选取图片失败");
                    return;
                }

            } else {
                picturePath = originalUri.getPath();
            }

            toImageCropFragment(picturePath);
        } else if (requestCode == mActivity.PHOTO_PICKED_WITH_DATA_AFTER_KIKAT) {
            String picturePath = FileUtils.getPath(mActivity, data.getData());
            toImageCropFragment(picturePath);
        }
    }

    @Override
    public void onTakePictureEnd(Bitmap bm) {

    }

    @Override
    public void onTakePictureEnd(String pathImg) {

        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(pathImg);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        mActivity.sendBroadcast(intent);

        mAlbumBtn.setClickable(true);
        mTakePictureBtn.setClickable(true);

        /** 拍摄成功跳转到剪切界面 **/
        toImageCropFragment(pathImg);
    }

    /**
     * 跳转到剪切界面
     *
     * @param path
     */
    private void toImageCropFragment(String path) {
        Log.e("===", " toImageCropFragment path = " + path);
        File file = new File(path);
        if (!file.exists()) {
            TipUtils.ShowText(mActivity, "加载图片失败");
            return;
        }

        mActivity.setPicturePath(path);
        mActivity.switchFragment(mActivity.FragmentTypeCrop);
    }

    @Override
    public int getLayoutId() {
        return R.layout.camera_fragment_camera;
    }

    @Override
    public void initViews(View root) {

        root.findViewById(R.id.camera_back).setOnClickListener(this);

        mContainer = (BaseCameraContainer) root.findViewById(R.id.container);
        mContainer
                .setCameraErrorDialogCallBack(new BaseCameraView.CameraErrorDialogCallBack() {

                    @Override
                    public void errorDialogCallBack() {
                        if (mActivity != null)
                            mActivity.onBackPressed();
                    }
                });

        mSwitchFlashModeBtn = (ImageView) root
                .findViewById(R.id.camera_flash_mode);
        mSwitchCameraBtn = (ImageView) root.findViewById(R.id.camera_switch);

        if (!mContainer.hasFrontFacingCamera()) {
            mSwitchCameraBtn.setVisibility(View.GONE);
        }

        mTakePictureBtn = (ImageView) root.findViewById(R.id.camera_capture);

        mAlbumBtn = (ImageView) root.findViewById(R.id.camera_album);

        mTakePictureBtn.setOnClickListener(this);
        mTakePictureBtn.setClickable(false);

        mSwitchFlashModeBtn.setOnClickListener(this);
        mSwitchCameraBtn.setOnClickListener(this);
        mAlbumBtn.setOnClickListener(this);
        mContainer.post(new Runnable() {

            @Override
            public void run() {
                mTakePictureBtn.setClickable(true);
            }
        });

        initData();
    }
}
