package nju.androidchat.client.component;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.LinearLayout;
import nju.androidchat.client.R;

import java.io.File;

public class ItemPicReceive extends LinearLayout {

    private ImageView imageView;
    public ItemPicReceive(Context context, String picPath) {
        super(context);
        inflate(context, R.layout.item_pic_receive, this);
        this.imageView = findViewById(R.id.chat_item_content_pic);
        Uri uri = Uri.fromFile(new File(picPath));
        setImageView(uri);
    }

    private void setImageView(Uri uri) {
        imageView.setImageURI(uri);
    }
}
