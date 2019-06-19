package nju.androidchat.client.hw1;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Log
public class Hw1TalkActivity extends AppCompatActivity implements Mvp0Contract.View,
                                        TextView.OnEditorActionListener, OnRecallMessageRequested {
    private Mvp0Contract.Presenter presenter;
    private Mvp0Contract.ShowPicPresenter showPicPresenter;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    addPicSend((String) msg.obj);
                    break;
                case 2:
                    addPicReceive((String) msg.obj);
                    break;
                case 3:
                    addTextSend("wrong url!",(UUID) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    private void addPicSend(String picPath) {
        LinearLayout content = findViewById(R.id.chat_content);
        content.addView(new ItemPicSend(this, picPath));
    }

    private void addPicReceive(String picPath) {
        LinearLayout content = findViewById(R.id.chat_content);
        content.addView(new ItemPicReceive(this, picPath));
    }

    private void addTextSend(String text, UUID uuid) {
        LinearLayout content = findViewById(R.id.chat_content);
        content.addView(new ItemTextSend(this, text, uuid, this));
    }

    private void addTextReceive(String text, UUID uuid) {
        LinearLayout content = findViewById(R.id.chat_content);
        content.addView(new ItemTextReceive(this, text, uuid));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        this.showPicPresenter = new Mvp0ShowPicPresenter();
        mvp0TalkModel.setIMvp0TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        // 如果是自己发的，增加ItemTextSend
                        boolean isMyMsg =
                                message.getSenderUsername().equals(this.presenter.getUsername());
                        if(isPic(text)) {
                            String url =
                                    text.substring(text.indexOf('(') + 1, text.lastIndexOf(')'));
                            String filePath = getFilesDir().getPath() + "/pic";
                            String picPath = filePath + "/" + message.getMessageId().toString() +
                                                ".jpg";
                            if(!isDownloaded(picPath))
                                this.showPicPresenter.downloadPic(filePath, url, message.getMessageId(),
                                                                    mHandler, isMyMsg);
                            else{
                                if(isMyMsg) addPicSend(picPath);
                                else        addPicReceive(picPath);
                            }
                        } else {
                            if (isMyMsg)    addTextSend(text, message.getMessageId());
                            else            addTextReceive(text, message.getMessageId());

                        }
                    }

                    Utils.scrollListToBottom(this);
                }
        );
    }

    private boolean isPic(String msg) {
        String imagePattern = "!\\[(.*)]\\((http.*)\\)$";
        return Pattern.matches(imagePattern, msg);
    }

    private boolean isDownloaded(String picPath){
        try {
            File file = new File(picPath);
            return file.exists();
        } catch (Exception e) {
            log.info("read pic failed.");
        }
        return false;
    }

    @Override
    public void setPresenter(Mvp0Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
