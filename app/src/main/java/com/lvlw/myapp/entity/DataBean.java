package com.lvlw.myapp.entity;

/**
 * Created by w9859 on 2017/3/14.
 */

public class DataBean{


    /**
     * name : Tony老师聊shell——环境变量配置文件
     * picSmall : http://img.mukewang.com/55237dcc0001128c06000338-300-170.jpg
     */

    private String name;
    private String picSmall;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicSmall() {
        return picSmall;
    }

    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    @Override
    public String toString() {
        return "DataBean{" +
                "name='" + name + '\'' +
                ", picSmall='" + picSmall + '\'' +
                '}';
    }


}
