package cn.jarlen.photoedit.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import cn.jarlen.photoedit.R;

/**
 * Created by jarlen on 2015/8/19.
 */
public class PublishActivity extends FragmentActivity {


    /**
     * 分享取消，完成
     */
    private Button publish_back_btn, publish_done_btn;

    /**
     * 分享标题
     */
    private TextView publish_title;

    /**
     * 分享的内容
     */
    private EditText publish_content_et;

    /**
     * 分享的图片
     */
    private ImageView publish_pic_img;

    /**
     * 分享的图片路径
     */
    private String picturePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_publish);
        initViews();
        initDate();
    }


    private void initViews() {

        publish_back_btn = (Button) findViewById(R.id.publish_back);
        publish_done_btn = (Button) findViewById(R.id.publish_done);
        publish_title = (TextView) findViewById(R.id.publish_title);

        publish_content_et = (EditText) findViewById(R.id.publish_content_et);
        publish_pic_img = (ImageView) findViewById(R.id.publish_pic_img);

    }

    private void initDate() {

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        picturePath = (String) bundle.get("picturePath");

        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        if (publish_pic_img != null) {
            publish_pic_img.setImageBitmap(bitmap);
        }

    }


}
