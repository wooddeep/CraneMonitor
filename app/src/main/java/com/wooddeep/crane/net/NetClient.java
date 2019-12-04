package com.wooddeep.crane.net;

import com.wooddeep.crane.net.network.Protocol;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class NetClient {

    private volatile Socket socket = null;
    private volatile OutputStream outputStream = null;
    private volatile InputStream inputStream = null;
    private volatile AtomicBoolean reconnFlag = new AtomicBoolean(true);

    private String savedAddr = "192.168.141.195"; //"192.168.141.43";
    private int savedPort = 1733;

    private Lock lock = new ReentrantLock();

    private final byte[] buffer = new byte[10240];//创建接收缓冲区
    private Protocol protocol = new Protocol();

    private void reconnect() {
        System.out.printf("## reconnect to %s:%d\n", savedAddr, savedPort);
        try {
            socket = new Socket(savedAddr, savedPort);
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
            reconnFlag.set(false);

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
        while (true) {

            if (reconnFlag.get()) {
                reconnect();
            }

            try {
                Thread.sleep(3000);

                if (socket == null) {
                    continue;
                }

                byte[] body = protocol.getSession();
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
    }

    public void runRead() {
        while (true) {
            try {

                if (inputStream == null || reconnFlag.get()) {
                    Thread.sleep(2000);
                    continue;
                }

                int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度

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
                System.out.println("## now reconnFlag: " + reconnFlag.get());
            }
        }
    }


    private class NetThread extends Thread {

        private boolean flag;

        public NetThread(boolean flag) {
            this.flag = flag;
        }

        @Override
        public void run() {
            if (flag) {
                runWrite();
            } else {
                runRead();
            }
        }
    }

    public static void run() {
        NetClient client = new NetClient();
        client.new NetThread(true).start();
        client.new NetThread(false).start();
    }
}

