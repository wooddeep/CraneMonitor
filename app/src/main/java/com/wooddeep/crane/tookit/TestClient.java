package com.wooddeep.crane.tookit;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * Created by niuto on 2019/11/26.
 */

class TestObjectRequest {
    public TestObjectRequest() {
    }
}

class TestObject {
    private int number = 10;
    private String text;

    public TestObject() {
    }

    public TestObject(String text) {
        this.text = text;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

class TestObjectResponse {

    private TestObject test;

    public TestObjectResponse() {
    }

    public TestObjectResponse(TestObject test) {
        this.test = test;
    }

    public TestObject getTest() {
        return test;
    }

    public void setTest(TestObject test) {
        this.test = test;
    }
}

class KryoUtil {

    public static final int TCP_PORT = 55223;
    public static final int UDP_PORT = 55224;

    public static void registerServerClasses(Server server) {
        register(server.getKryo());
    }

    public static void registerClientClass(Client client) {
        register(client.getKryo());
    }

    private static void register(Kryo kryo) {
        kryo.register(TestObject.class);
        kryo.register(int.class);
        kryo.register(String.class);

        // network messages
        kryo.register(TestObjectResponse.class);
        kryo.register(TestObjectRequest.class);
    }
}

class TestServer {

    public TestServer() {

        Server server = new Server();
        KryoUtil.registerServerClasses(server);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                System.out.println("connected");
            }

            @Override
            public void disconnected(Connection connection) {
                System.out.println("disconnected");
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof TestObjectRequest) {
                    TestObject test = new TestObject("Hello Client!");
                    connection.sendUDP(new TestObjectResponse(test));
                }
            }
        });

        try {
            server.bind(KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        server.start();
    }

}

public class TestClient {

    Client client;

    public TestClient() {

        client = new Client();
        KryoUtil.registerClientClass(client);

        /* Kryonet > 2.12 uses Daemon threads ? */
        new Thread(client).start();

        client.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                TestObjectRequest test = new TestObjectRequest();
                client.sendTCP(test);
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof TestObjectResponse) {
                    TestObjectResponse resp = (TestObjectResponse) object;
                    System.out.println(resp.getTest().getText());
                }
            }

            @Override
            public void disconnected(Connection connection) {
            }
        });

        try {
            /* Make sure to connect using both tcp and udp port */
            client.connect(5000, "127.0.0.1", KryoUtil.TCP_PORT, KryoUtil.UDP_PORT);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public static void main(String[] args) {
        new TestClient();
    }

}
