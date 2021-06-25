package com.wooddeep.crane.persist.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "camera") // 指定数据表的名称
public class Camera {

    @DatabaseField(generatedId = true, columnName = "id", useGetSet = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // craneType & armLength & power 三个为联合键值
    @DatabaseField(columnName = "ip", useGetSet = true, defaultValue = "127.0.0.1")
    private String ip;


    @DatabaseField(columnName = "port", useGetSet = true, defaultValue = "8544") //臂长
    private String port;

    @DatabaseField(columnName = "url", useGetSet = true, defaultValue = "rtsp://127.0.0.1:8544/index") // 倍率
    private String url;

    @DatabaseField(columnName = "number", useGetSet = true, defaultValue = "1")
    private String number;

    @DatabaseField(columnName = "route", useGetSet = true, defaultValue = "1")
    private String route;


    @DatabaseField(columnName = "username", useGetSet = true, defaultValue = "1")
    private String username;

    @DatabaseField(columnName = "password", useGetSet = true, defaultValue = "1")
    private String password;


    public Camera() {
        super();
    }


    public Camera(String ip, String port, String url, String number, String route) {
        this.ip = ip;
        this.port = port;
        this.url = url;
        this.number = number;
        this.route = route;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static Camera getInitData() {
        return new Camera(
            "127.0.0.1",
            "8544",
            "rtsp://127.0.0.1:8544/index",
            "1",
            "camera"
        );
    }



}
