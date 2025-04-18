package club.shengsheng.bio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class BioClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        Thread tThread = startClient("Tom");
        Thread jThread = startClient("jerry");
        tThread.join();
        jThread.join();
    }


    static Thread startClient(String name) {
        Thread clientThread = new Thread(() -> {
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("localhost", 8080));
                OutputStream outputStream = socket.getOutputStream();
                for (int i = 10; i > 0; i--) {
                    String message = String.format("Hello, 我是%s %d", Thread.currentThread().getName(), i);
                    outputStream.write(message.getBytes());
                    outputStream.flush();
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, name);
        clientThread.start();
        return clientThread;

    }
}
