package com.wooddeep.crane.tookit;

import com.wooddeep.crane.SuperAdmin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {

    private Socket socket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private String savedAddr = "192.168.141.195";
    private int savedPort = 1733;

    private void reconnect() {
        System.out.printf("## reconnect to %s:%d\n", savedAddr, savedPort);
        try {
            socket = new Socket(savedAddr, savedPort);
        } catch (UnknownHostException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        } catch (IOException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }

        if (socket == null) return;

        //获取输出流
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }
    }

    public void run() {

        reconnect();
        final byte[] buffer = new byte[1024];//创建接收缓冲区

        try {
            while (true) {

                Object msg = SuperAdmin.mq.poll();
                if (msg != null) { // 重建socket
                    String[] remote = ((String) msg).split(":");
                    String addr = remote[0];
                    int port = Integer.parseInt(remote[1]);
                    savedAddr = addr;
                    savedPort = port;
                    reconnect(); // 重连接
                }

                if (socket == null) {
                    reconnect();
                    continue;
                }

                outputStream.write("hello".getBytes());
                final int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度

                System.out.println("## len = " + len);

                if (len == -1) reconnect();

                Thread.sleep(5000);
            }
        } catch (Exception e) {
            System.out.printf("cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
        }

    }

}
