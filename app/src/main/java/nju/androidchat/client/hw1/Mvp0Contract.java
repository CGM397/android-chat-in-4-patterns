package nju.androidchat.client.hw1;

import android.os.Handler;
import nju.androidchat.client.ClientMessage;

import java.util.List;
import java.util.UUID;

public interface Mvp0Contract {
    interface View extends BaseView<Presenter> {
        void showMessageList(List<ClientMessage> messages);
    }

    interface Presenter extends BasePresenter {
        void sendMessage(String content);

        void receiveMessage(ClientMessage content);

        String getUsername();

        //撤回消息mvp0不实现
        void recallMessage(int index0);
    }

    interface ShowPicPresenter extends BasePresenter {
        void downloadPic(String filePath, String url, UUID msgId, Handler handler, boolean isMyMsg);
    }

    interface Model {
        ClientMessage sendInformation(String message);

        String getUsername();
    }
}
