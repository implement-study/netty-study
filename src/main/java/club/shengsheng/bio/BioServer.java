package club.shengsheng.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            InputStream inputStream = clientSocket.getInputStream();
            int readLength;
            byte[] buffer = new byte[1024];
            while ((readLength = inputStream.read(buffer)) != -1) {
                System.out.println(new String(buffer, 0, readLength));
            }
        }
    }
}
