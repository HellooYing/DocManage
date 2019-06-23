package com.NeuDocManage;

import java.io.IOException;
import java.util.Date;
import java.util.Scanner;

import static com.NeuDocManage.model.HostHolder.getCurDir;
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
                System.out.print("无法识别您的输入，请重试。");
            }
        }
        System.out.println("您好，用户root。现在时间是"+new Date()+",输入help可获取指令列表。");
        while (true){
            System.out.print("root@docManage:"+getCurDir().getFileName()+"#");
            input=scanner.nextLine().toLowerCase();
            if(input.equals("help")){
                System.out.println("指令列表：");
                System.out.println("ls [<目录>] : 显示当前目录或给定目录下的文件列表");
                System.out.println("cd <目录> : 进入给定目录");
                System.out.println("create <文件名> [<目录>]: 在当前目录或给定目录下创建文件");
                System.out.println("mkdir <目录名> [<目录>] : 在当前目录或给定目录下创建目录");
                System.out.println("read <文件名> : 读取文件");
                System.out.println("rm [-r] <文件名或目录名> : 删除文件或目录，-r代表递归删除目录下所有子目录和文件");
                System.out.println("write <文件名> <写入内容> : 向文件中写入内容");
                System.out.println("su <用户名> : 登录其他用户");
                System.out.println("exit : 退出文件系统");
            }
        }
    }
}
