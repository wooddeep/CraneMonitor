package com.wooddeep.crane.net;

import com.wooddeep.crane.net.network.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TcpClient {

    private volatile Socket socket = null;
    private volatile OutputStream outputStream = null;
    private volatile InputStream inputStream = null;

    private String savedAddr = "127.0.0.1"; //"192.168.141.43";
    private int savedPort = 1733;

    private static ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    private AtomicBoolean reconnFlag = new AtomicBoolean(true);
    private Lock lock = new ReentrantLock();

    private final byte[] buffer = new byte[10240];//创建接收缓冲区
    private Protocol protocol = new Protocol();

    private void reconnect() {
        System.out.printf("## reconnect to %s:%d\n", savedAddr, savedPort);
        try {
            socket = new Socket(savedAddr, savedPort);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            System.out.println(inputStream);
            reconnFlag.set(false);
            map.put("key", "value");

        } catch (Exception e) {
            reconnFlag.set(true);
            System.out.printf("## reconnect[0] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e1) {
                System.out.printf("## reconnect[1] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
            }
        }
    }

    public void runWrite() {
        new Thread(() -> { // read thread
            while (true) {
                if (reconnFlag.get()) {
                    lock.lock();
                    reconnect();
                    lock.unlock();
                }

                try {
                    Thread.sleep(3000);

                    if (socket == null) {
                        continue;
                    }

                    byte[] body = protocol.getSession(null);
                    int beWriteN = protocol.doPack(body);

                    System.out.println("## beWriteN = " + beWriteN);

                    outputStream.write(protocol.getPack(), 0, beWriteN);
                    outputStream.flush();


                } catch (Exception e) {
                    System.out.printf("## runWrite[0] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        System.out.printf("## runWrite[1] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                    }
                    reconnFlag.set(true);
                }
            }
        }).start();
    }

    public void runRead() {
        new Thread(() -> { // read thread
            while (true) {
                try {
                    lock.lock();

                    System.out.println(inputStream);

                    if (inputStream == null) {
                        Thread.sleep(2000);
                        lock.unlock();
                        continue;
                    }


                    int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度

                    lock.unlock();

                    if (len == -1) {
                        reconnFlag.set(true);
                    }
                    System.out.println("## len = " + len);
                    protocol.unPack(buffer, len);

                } catch (Exception e) {
                    System.out.printf("## runRead[0] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                    if (e.getCause() == null) {
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e1) {
                            System.out.printf("## runRead[1] -> cause: %s, mesg: %s\n", e.getCause(), e.getMessage());
                        }
                    }
                    reconnFlag.set(true);
                }
            }
        }).start();

    }

    public static void main(String[] args) {
        new TcpClient().runRead();
        new TcpClient().runWrite();
    }

}

