package com.NeuDocManage.service;

import com.NeuDocManage.model.Superblock;
import com.alibaba.fastjson.JSON;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import static com.NeuDocManage.config.MainConfig.*;
import static com.NeuDocManage.service.BlockService.readBlock;
import static com.NeuDocManage.service.BlockService.writeBlock;

public class DiskService {
    public static MappedByteBuffer disk;//磁盘
    public static Superblock superblock;//超级块
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
                for (int j = i+1; j <i+100; j++) {
                    otherDataBlock.append(j+" ");
                }
                otherDataBlock.append(i+100);
                writeBlock(i,otherDataBlock.toString());
            }

            StringBuilder otherInodeBlock=new StringBuilder();
            for (int i = INODEBLOCKSTART+2; i <INODEBLOCKSTART+INODEBLOCKNUM-1 ; i++) {//i节点从第二块才开始分配，第一块留给root目录
                otherInodeBlock.append(i+",");
            }
            otherInodeBlock.append(INODEBLOCKSTART+INODEBLOCKNUM-1);
            writeBlock(INODEBLOCKSTART+1,otherInodeBlock.toString());

            //初始化超级块，赋默认初值
            superblock=new Superblock(INODEBLOCKSTART,USERBLOCKNUM,DATABLOCKSTART,INODEBLOCKSTART+1);
        }
        else{//不是第一次，就把上次的信息存到superblock类中
            superblock=JSON.parseObject(lastSuperBlockMessage, Superblock.class);
        }
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
