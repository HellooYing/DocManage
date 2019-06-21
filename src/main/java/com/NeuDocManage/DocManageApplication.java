package com.NeuDocManage;

import java.io.IOException;
import java.util.Scanner;

import static com.NeuDocManage.service.DiskService.*;

public class DocManageApplication {
    public static void main(String[] args) throws IOException {
        Scanner scanner=new Scanner(System.in);
        String input;
        System.out.print("欢迎进入本模拟文件系统，");
        while(true){
            System.out.println("是否需要格式化磁盘？(y/n)");
            input=scanner.nextLine().toLowerCase();
            if(input.equals("y")){
                System.out.println("正在格式化磁盘……");
                formatDisk();
                System.out.println("格式化成功！");
                break;
            }
            else if(input.equals("n")){
                break;
            }
            else{
                System.out.println("无法识别您的输入，请重试。");
            }
        }
    }
}
