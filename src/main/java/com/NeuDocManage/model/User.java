package com.NeuDocManage.model;

public class User {
    //用来存储用户信息的块
    private String userName; //用户名称，长度<20
    private String pwd; //密码,长度<20
    private int mod; //权限
    private int inode; //用户根目录i节点

    public User() {
    }

    public User(String userName, String pwd) {
        this.userName = userName;
        this.pwd = pwd;
    }

    public User(String userName, String pwd, int mod, int inode) {
        this.userName = userName;
        this.pwd = pwd;
        this.mod = mod;
        this.inode = inode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public int getMod() {
        return mod;
    }

    public void setMod(int mod) {
        this.mod = mod;
    }

    public int getInode() {
        return inode;
    }

    public void setInode(int inode) {
        this.inode = inode;
    }
}
