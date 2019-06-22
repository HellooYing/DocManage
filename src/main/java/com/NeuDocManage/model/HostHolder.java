package com.NeuDocManage.model;

public class HostHolder {
    private static ThreadLocal<User> users=new ThreadLocal<User>();
    //线程本地变量
    public static User getUser(){
        return users.get();
    }
    public static void setUsers(User user){
        users.set(user);
    }
    public static void clear(){
        users.remove();
    }
}
