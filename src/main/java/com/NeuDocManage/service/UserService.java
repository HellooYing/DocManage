package com.NeuDocManage.service;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.User;
import com.alibaba.fastjson.JSON;
import com.sun.org.apache.regexp.internal.RE;

import java.util.Scanner;

import static com.NeuDocManage.config.MainConfig.*;
import static com.NeuDocManage.service.BlockService.*;

public class UserService {
    /**
     * su xxx
     * 登录其他用户
     * @return
     */
    public static boolean login(String userName,String pswd){
        // 先查看该userName是否存在
        User user=findUser(userName);
        if(user==null){//如果不存在这个用户，就返回false
            System.out.println("用户"+userName+"不存在！");
            return false;
        }
        User now=HostHolder.getUser();
        if(now.getUserName().equals("root")){//如果当前是root用户，直接就可以登录其他用户
            HostHolder.setUser(user);
            return true;
        }
        else{
            if(pswd.equals(user.getPwd())){
                HostHolder.setUser(user);
                return true;
            }
            System.out.println("密码错误，登录失败！");
            return false;
        }
    }

    public static boolean register(User user){
        User user2=findUser(user.getUserName());
        if(user2!=null){//如果已存在该用户名，注册失败
            System.out.println("用户"+user.getUserName()+"已存在！");
            return false;
        }
        int blockId=getUserBlock();
        if(blockId==-1){
            System.out.println("用户空间已满，无法创建新用户！");
            return false;
        }
        writeBlock(blockId,JSON.toJSONString(user));
        return true;
    }

    public static boolean deleteUser(String userName){
        if(userName.equals("root")){
            System.out.println("root用户无法被删除！");
            return false;
        }
        User user=HostHolder.getUser();
        if(!user.getUserName().equals("root")){
            System.out.println("非root用户无权删除其他用户！");
            return false;
        }
        for (int i = USERBLOCKSTART; i <USERBLOCKSTART+USERBLOCKNUM ; i++) {
            String userBlockContent=readBlock(i).trim();
            if(userBlockContent.length()==0) continue;
            user= JSON.parseObject(userBlockContent,User.class);
            if(user.getUserName().equals(userName)){
                recoverUserBlock(i);
                return true;
            }
        }
        return true;//没有这个用户也返回true
    }

    private static int getUserBlock(){
        for (int i = USERBLOCKSTART; i <USERBLOCKSTART+USERBLOCKNUM ; i++) {
            String userBlockContent = readBlock(i).trim();
            if (userBlockContent.length() == 0) return i;
        }
        return -1;
    }

    private static void recoverUserBlock(int blockId){
        formatBlock(blockId);
    }

    private static User findUser(String userName){
        boolean userExist=false;
        User user=null;
        for (int i = USERBLOCKSTART; i <USERBLOCKSTART+USERBLOCKNUM ; i++) {//在用户块中循环找到要登录的用户
            String userBlockContent=readBlock(i).trim();
            if(userBlockContent.length()==0) continue;
            user= JSON.parseObject(userBlockContent,User.class);
            if(user.getUserName().equals(userName)){
                userExist=true;
                break;
            }
        }
        if(userExist) return user;
        else return null;
    }


//
//    boolean TransUser(){
//
//    }
    /*
    User readUser(int blockId){
        //根据blockId查询用户信息
    }
    */
}
