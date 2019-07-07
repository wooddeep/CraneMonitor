package com.wooddeep.crane.persist.entity;




public class Student {
    //不能用int
    //@Id(autoincrement = true)
    private Long id;
    //姓名
    //@NotNull
    private String name;
    //学号
    //@Unique
    private String number;
    //性别
    private int sex;
}
