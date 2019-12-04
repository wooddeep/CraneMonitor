package com.wooddeep.crane.net.network;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

// https://www.jianshu.com/p/02e16989f3a4 NIO demo

/**
 * client 端
 *
 * @author xiezhengchao
 * @since 2018/4/7 15:10
 */
public class ClientDemo {

    private final ByteBuffer sendBuffer = ByteBuffer.allocate(10240);
    private final ByteBuffer receiveBuffer = ByteBuffer.allocate(10240);
    private Selector selector;

    public ClientDemo() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 1733));
        socketChannel.configureBlocking(false);
        System.out.println("与服务器的连接建立成功");
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    private void talk() throws IOException {
        while (selector.select() > 0) {

            Iterator<SelectionKey> it = selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (key.isReadable()) {
                    receive(key);
                }
                // 实际上只要注册了关心写操作，这个操作就一直被激活
                if (key.isWritable()) {
                    send(key);
                }
            }
        }
    }

    private void send(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        synchronized (sendBuffer) {
            sendBuffer.flip(); //设置写
            while (sendBuffer.hasRemaining()) {
                socketChannel.write(sendBuffer);
            }
            sendBuffer.compact();
        }
    }

    private void receive(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        socketChannel.read(receiveBuffer);
        receiveBuffer.flip();

        System.out.println(receiveBuffer.limit());

        for (int i = 0; i < receiveBuffer.array().length; i++) {
            System.out.printf(" 0x%02x ", receiveBuffer.array()[i]);
        }
        System.out.println("");


        String receiveData = Charset.forName("UTF-8").decode(receiveBuffer).toString();

        System.out.println("receive server message:" + receiveData);
        receiveBuffer.clear();
    }

    private Protocol protocol = new Protocol();

    private void receiveFromUser() {

        try {

            while (true) {
                Thread.sleep(5000);
                synchronized (sendBuffer) {
                    byte[] body = protocol.getSession();
                    int beWriteN = protocol.doPack(body);
                    System.out.println("## beWriteN = " + beWriteN);
                    sendBuffer.put(protocol.getPack(), 0, beWriteN);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        final ClientDemo client = new ClientDemo();
        Thread receiver = new Thread(client::receiveFromUser);
        receiver.start();
        client.talk();

    }
}
