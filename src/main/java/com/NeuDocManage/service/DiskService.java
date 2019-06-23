package com.NeuDocManage.service;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.IndexNode;
import com.NeuDocManage.model.SuperBlock;
import com.NeuDocManage.model.User;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Date;

import static com.NeuDocManage.config.MainConfig.*;
import static com.NeuDocManage.service.BlockService.*;

public class DiskService {
    public static MappedByteBuffer disk;//磁盘
    public static SuperBlock superBlock;//超级块
    /**
     * 初始化磁盘，如果没有disk.txt就创建，有则打开
     * @throws IOException
     */
    public static void initDisk() throws IOException {
        //创建或打开disk.txt文件，模拟磁盘
        disk=new RandomAccessFile("disk.txt","rw").
                getChannel().map(FileChannel.MapMode.READ_WRITE,0,DISKSIZE);

        //从0号盘块中读取上次的信息
        String lastSuperBlockMessage=readBlock(0).trim();

        if(lastSuperBlockMessage.length()==0){//如果没有上次的信息,也就是说这是第一次启动文件系统
            //第一次启动要给磁盘数据区每隔100个盘块、索引区第一个盘块加上别的盘块的地址
            for (int i = DATABLOCKSTART; i < BLOCKNUM; i=i+100) {
                StringBuilder otherDataBlock=new StringBuilder();

                for (int j = i+1; j <i+100&&j<BLOCKNUM; j++) {
                    otherDataBlock.append(j+",");
                }
                if(i+100<BLOCKNUM) otherDataBlock.append(i+100);
                else otherDataBlock.deleteCharAt(otherDataBlock.length()-1);
                writeBlock(i,otherDataBlock.toString());
            }
            writeBlock(BLOCKNUM-1,"0");
            StringBuilder otherInodeBlock=new StringBuilder();
            for (int i = INODEBLOCKSTART+2; i <INODEBLOCKSTART+INODEBLOCKNUM-1 ; i++) {//i节点从第二块才开始分配，第一块留给root目录
                otherInodeBlock.append(i+",");
            }
            otherInodeBlock.append(INODEBLOCKSTART+INODEBLOCKNUM-1);
            writeBlock(INODEBLOCKSTART+1,otherInodeBlock.toString());
            writeBlock(INODEBLOCKSTART+INODEBLOCKNUM-1,"0");

            //初始化超级块，赋默认初值
            superBlock =new SuperBlock(INODEBLOCKSTART,USERBLOCKNUM,DATABLOCKSTART,INODEBLOCKSTART+1);
            writeBlock(SUPERBLOCKSTART,JSON.toJSONString(superBlock));
        }
        else{//不是第一次，就把上次的信息存到superblock类中
            superBlock =JSON.parseObject(lastSuperBlockMessage, SuperBlock.class);
        }
        //初始化root用户及默认的/root目录
        User root=new User("root","root",7,INODEBLOCKSTART);
        writeBlock(USERBLOCKSTART,JSON.toJSONString(root));
        HostHolder.setUser(root);
        IndexNode rootNode=new IndexNode(INODEBLOCKSTART,1,false,0,"root","root",new Date(),new Date(),0);
        writeBlock(INODEBLOCKSTART,JSON.toJSONString(rootNode));
        HostHolder.setCurDir(rootNode);
    }

    public static void releaseDisk(){
        int emptyFileBlock=DataService.saveStack();
        if(emptyFileBlock!=0) superBlock.setEmptyFileBlock(emptyFileBlock);
        int emptyIndexBlock=INodeServie.saveStack();
        if(emptyIndexBlock!=0) superBlock.setEmptyIndexBlock(emptyIndexBlock);
        overwriteBlock(SUPERBLOCKSTART,JSON.toJSONString(superBlock));
    }



    /**
     * 格式化磁盘
     * @throws IOException
     */
    public static void formatDisk() throws IOException {
        try{
            File file = new File("disk.txt");
            file.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
        initDisk();
    }
}
