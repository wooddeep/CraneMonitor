package com.wooddeep.crane.tookit;

import com.wooddeep.crane.persist.dao.SysParaDao;
import com.wooddeep.crane.persist.entity.SysPara;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {

    private Socket socket = null;
    private OutputStream outputStream = null;
    private InputStream inputStream = null;

    private SysParaDao paraDao = null;

    public TcpClient(SysParaDao sysParaDao) {
        paraDao = sysParaDao;
    }

    public void run() {

        String remoteAddr = "192.168.140.94";
        SysPara ra = paraDao.queryParaByName("remoteAddr");
        if (ra == null) {
            ra = new SysPara("remoteAddr", remoteAddr);
            paraDao.insert(ra);
        } else {
            remoteAddr = ra.getParaValue();
        }

        int remotePort = 1733;
        SysPara rp = paraDao.queryParaByName("remotePort");
        if (rp == null) {
            rp = new SysPara("remotePort", String.valueOf(remotePort));
            paraDao.insert(rp);
        } else {
            remotePort = Integer.parseInt(rp.getParaValue());
        }

        try {
            socket = new Socket(remoteAddr, remotePort);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //获取输出流
        try {
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            while (true) {
                final byte[] buffer = new byte[1024];//创建接收缓冲区

                outputStream.write("hello".getBytes());
                final int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度

                System.out.println("## len = " + len);

                Thread.sleep(20000);

            }
        } catch (Exception e) {

        }

    }

}
