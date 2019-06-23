package com.NeuDocManage.service;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.User;
import com.alibaba.fastjson.JSON;

import java.util.Scanner;

import static com.NeuDocManage.config.MainConfig.*;
import static com.NeuDocManage.service.BlockService.readBlock;

public class UserService {
    /**
     * su xxx
     * 登录其他用户
     * @return
     */
    public static boolean userLogin(String userName){
        // 先查看该userName是否存在
        boolean userExist=false;
        User user=null;
        for (int i = USERBLOCKSTART; i <USERBLOCKSTART+USERBLOCKNUM ; i++) {
            String userBlockContent=readBlock(i).trim();
            if(userBlockContent.length()==0) continue;
            user= JSON.parseObject(userBlockContent,User.class);
            if(user.getUserName().equals(userName)){
                userExist=true;
                break;
            }
        }
        if(!userExist){
            System.out.println("用户"+userName+"不存在！");
            return false;
        }
        User now=HostHolder.getUser();
        if(now.getUserName().equals("root")){
            HostHolder.setUser(user);
            return true;
        }
        else{
            for (int i = 0; i <3 ; i++) {
                System.out.print("请输入密码：");
                Scanner scanner=new Scanner(System.in);
                String input=scanner.nextLine().toLowerCase();
                if(input.equals(user.getPwd())){
                    HostHolder.setUser(user);
                    return true;
                }
                else{
                    System.out.println("密码错误，请重试！"+(i+1)+"/3");
                    continue;
                }
            }
            System.out.println("密码错误三次，登录失败！");
            return false;
        }
    }
//
//    boolean create(String userName,String pwd){
//        //创建
//    }
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
