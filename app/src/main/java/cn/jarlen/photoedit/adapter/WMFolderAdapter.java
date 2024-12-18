package cn.jarlen.photoedit.adapter;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import cn.jarlen.photoedit.R;
import cn.jarlen.photoedit.net.Constant;
import cn.jarlen.photoedit.util.Lg;


public class WMFolderAdapter extends BaseAdapter {
    private Context mContext;

    private ArrayList<String> icon;

    private String folderName;
    private Boolean isLocal = true;

    private Bitmap thumbnailBmp;
    private int selectedItem;

    private int folderPosition;

    private Boolean isFirst = true;

    private ArrayList<WaterMarkFolderBean> mFolderlist;

    private ArrayList<WaterMarkFolderBean> mLocalFolderlist;
    private ArrayList<String> mLocaltabList;

    private String WMRootPath = null;

    public WMFolderAdapter(Context context, Bitmap bmp) {
        this.mContext = context;
        WMRootPath = context.getExternalFilesDir(null).getAbsolutePath() + Constant.WM_NET_Local;
        setThumbnailBmp(bmp);
    }

    public void setThumbnailBmp(Bitmap bmp) {
        int wmWidth = (int) mContext.getResources().getDimension(
                R.dimen.wm_item_width);
        int wmHeight = (int) mContext.getResources().getDimension(
                R.dimen.wm_item_height);
        this.thumbnailBmp = ThumbnailUtils.extractThumbnail(bmp, wmWidth,
                wmHeight);
    }

    public void setItemSelected(int position) {
        this.selectedItem = position;
        isFirst = false;
        notifyDataSetChanged();
    }

    public void setWMFolderSelected(int position) {
        if (folderPosition != position) {
            this.selectedItem = 0;
            isFirst = true;
        }
        this.folderPosition = position;
        this.folderName = mFolderlist.get(position).getFolderId();
        this.icon = (ArrayList<String>) mFolderlist.get(position).getIcon();

        if (position > getLocalWMNum() - 1) {
            isLocal = false;
        } else {
            isLocal = true;
        }

        notifyDataSetChanged();
    }

    public WaterMarkFolderBean getCurrentWMFolder() {
        return mFolderlist.get(folderPosition);
    }

    public ArrayList<String> getWMFolderData() {
        isFirst = true;
        if (mFolderlist == null) {
            mFolderlist = new ArrayList<WaterMarkFolderBean>();
        } else {
            mFolderlist.clear();
        }

        ArrayList<String> tabList = new ArrayList<String>();

        /** 添加本地水印 **/
        if (mLocaltabList == null) {
            mLocalFolderlist = new ArrayList<WaterMarkFolderBean>();
        }
        if (mLocaltabList == null) {
            mLocaltabList = new ArrayList<String>();
        }

        if (mLocalFolderlist.size() < 1 || mLocaltabList.size() < 1) {
            addLocalWMData(mLocalFolderlist, mLocaltabList);
        }

        mFolderlist.addAll(mLocalFolderlist);
        tabList.addAll(mLocaltabList);

        /** 添加商店水印 **/
        File file = new File(WMRootPath);
        if (file != null && file.exists()) {
            try {
                scanSDCardWMFolder(file, "json", mFolderlist, tabList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tabList;
    }

    @Override
    public int getCount() {

        if (icon != null && icon.size() > 0) {
            return icon.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param flag
     */
    public void setIsLocal(Boolean flag) {
        this.isLocal = flag;
        notifyDataSetChanged();
    }

    public Boolean getIsLocal() {
        return isLocal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = ViewHolder.getViewHolder(mContext, parent,
                convertView, position, R.layout.camera_layout_wm_item);

        if (!isFirst && selectedItem == position) {

            holder.getView(R.id.wm_bg).setBackgroundColor(
                    Color.parseColor("#FF33374A"));
            holder.getConvertView().setBackgroundDrawable(
                    mContext.getResources().getDrawable(
                            R.drawable.camera_shape_wm_item_selected));
        } else {
            holder.getView(R.id.wm_bg).setBackgroundColor(
                    Color.parseColor("#EF33374A"));
            holder.getConvertView().setBackgroundDrawable(
                    mContext.getResources().getDrawable(
                            R.drawable.camera_shape_wm_item));
        }

        ImageView img_thumbnail = holder.getView(R.id.picture_thumbnail);

        if (thumbnailBmp != null) {
            img_thumbnail.setImageBitmap(thumbnailBmp);
        }

        ImageView wm_thumbnail = holder.getView(R.id.wm_thumbnail);

        if (isLocal) {
            AssetManager assetManager = mContext.getAssets();
            try {
                String wmPath = Constant.WM_Local + folderName + "/"
                        + icon.get(position);

                InputStream is = assetManager.open(wmPath);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                if (bmp != null) {
                    wm_thumbnail.setImageBitmap(bmp);
                }

            } catch (IOException e) {
                //Lg.e("水印获取失败");
                e.printStackTrace();
            }
        } else {
            String wmPath = WMRootPath + folderName + "/"
                    + icon.get(position);
            Bitmap bmp = BitmapFactory.decodeFile(wmPath);
            if (bmp != null) {
                wm_thumbnail.setImageBitmap(bmp);
            }
        }

        return holder.getConvertView();
    }

    private void scanSDCardWMFolder(File file, String ext,
                                    ArrayList<WaterMarkFolderBean> list, ArrayList<String> tabList) throws IOException {

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (int index = 0; index < files.length; index++) {
                    File tmp = files[index];
                    if (tmp.isFile()) {
                        String fileName = tmp.getName();
                        String filePath = tmp.getAbsolutePath();

                        fileName = fileName
                                .substring(fileName.lastIndexOf(".") + 1);

                        if (ext != null && ext.equalsIgnoreCase(fileName)) {

                            FileInputStream is = null;
                            String json = null;

                            try {
                                is = new FileInputStream(filePath);
                                int length = is.available();
                                byte[] buffer = new byte[length];
                                is.read(buffer);
                                json = EncodingUtils.getString(buffer, "utf-8");
                                is.close();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
//							WaterMarkFolderBean WMFBean = null;
                            WaterMarkFolderBean WMFBean = JSON.parseObject(json,
                                    WaterMarkFolderBean.class);
                            String wmfName = WMFBean.getTypeName();
                            tabList.add(wmfName);
                            list.add(WMFBean);
                        }

                    } else {
                        scanSDCardWMFolder(tmp, ext, list, tabList);
                    }

                }
            }
        } else {
            if (file.isFile()) {
                String fileName = file.getName();
                String filePath = file.getAbsolutePath();

                fileName = fileName.substring(fileName.lastIndexOf(".") + 1);
                if (ext != null && ext.equalsIgnoreCase(fileName)) {

                    FileInputStream is = null;
                    String json = null;

                    try {
                        is = new FileInputStream(filePath);
                        int length = is.available();
                        byte[] buffer = new byte[length];
                        is.read(buffer);
                        json = EncodingUtils.getString(buffer, "utf-8");
                        is.close();
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    WaterMarkFolderBean WMFBean = null;
                    WMFBean = JSON.parseObject(json,
                            WaterMarkFolderBean.class);
                    String wmfName = WMFBean.getTypeName();
                    tabList.add(wmfName);
                    list.add(WMFBean);

                }

            }
        }
    }

    /**
     * 加载本地水印
     */
    private void addLocalWMData(ArrayList<WaterMarkFolderBean> list,
                                ArrayList<String> tabList) {
        String[] localWMPath = mContext.getResources().getStringArray(
                R.array.localWMFolder);

        AssetManager s = mContext.getAssets();

        for (int index = 0; index < localWMPath.length; index++) {
            InputStream is = null;
            String json = null;

            Lg.e("   " + localWMPath[index] + "   " + s.getLocales());

            try {
                is = s.open(localWMPath[index]);
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                json = new String(buffer, "utf-8");
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            WaterMarkFolderBean WMFBean = null;
            WMFBean = JSON.parseObject(json, WaterMarkFolderBean.class);
            String wmfName = WMFBean.getTypeName();
            tabList.add(wmfName);
            list.add(WMFBean);
        }
    }

    public void clear() {
        folderName = null;

        if (thumbnailBmp != null) {
            thumbnailBmp.recycle();
            thumbnailBmp = null;
        }

        if (icon != null) {
            icon.clear();
            icon = null;
        }

        if (mLocaltabList != null) {
            mLocaltabList.clear();
            mLocaltabList = null;
        }

        if (mFolderlist != null) {
            mFolderlist.clear();
            mFolderlist = null;
        }

        if (mLocalFolderlist != null) {
            mLocalFolderlist.clear();
            mLocalFolderlist = null;
        }
    }

    public int getLocalWMNum() {
        if (mLocalFolderlist == null || mLocalFolderlist.isEmpty()) {
            return 0;
        }
        return mLocalFolderlist.size();
    }

}
