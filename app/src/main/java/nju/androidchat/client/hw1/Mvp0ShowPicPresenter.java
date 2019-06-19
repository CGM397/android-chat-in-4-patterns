package nju.androidchat.client.hw1;

import android.os.Handler;
import android.os.Message;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@Log
@AllArgsConstructor
public class Mvp0ShowPicPresenter implements Mvp0Contract.ShowPicPresenter {

    @Override
    public void downloadPic(String filePath, String url, UUID msgId,
                            Handler handler, boolean isMyMsg) {
        final String picPath = filePath + "/" + msgId.toString() + ".jpg";
        new Thread(() -> {
            try {
                String targetUrl = url;
                if(!url.contains("https") && url.contains("http")) {
                    targetUrl = url.replace("http", "https");
                }
                URL targetURL = new URL(targetUrl);
                HttpURLConnection con = (HttpURLConnection) targetURL.openConnection();
                con.setRequestMethod("GET");
                InputStream inputStream = con.getInputStream();
                File file = new File(filePath);
                if(!file.exists())
                    file.mkdir();
                OutputStream outputStream = new FileOutputStream(new File(picPath));
                int n;
                byte[] b = new byte[1024];
                while ((n = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, n);
                }
                outputStream.flush();
                con.disconnect();
                inputStream.close();
                outputStream.close();
                if(isMyMsg) {
                    Message message = handler.obtainMessage(1);
                    message.obj = picPath;
                    handler.sendMessage(message);
                } else {
                    Message message = handler.obtainMessage(2);
                    message.obj = picPath;
                    handler.sendMessage(message);
                }
            } catch (Exception e) {
                Message message = handler.obtainMessage(3);
                message.obj = msgId;
                handler.sendMessage(message);
                log.info("downloadPic wrong.");
            }
        }).start();
    }

    @Override
    public void start() {

    }
}
