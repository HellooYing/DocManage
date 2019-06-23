package com.NeuDocManage.model;

public class HostHolder {
    private static ThreadLocal<User> users=new ThreadLocal<>();
    private static ThreadLocal<IndexNode> CurDirs=new ThreadLocal<>();

    public static IndexNode getCurDir() {
        return CurDirs.get();
    }

    public static void setCurDir(IndexNode curDir) {
        CurDirs.set(curDir);
    }

    public static User getUser(){
        return users.get();
    }
    public static void setUser(User user){
        users.set(user);
    }
    public static void clearUser(){
        users.remove();
    }
    public static void clearCurDir(){
        CurDirs.remove();
    }
}
