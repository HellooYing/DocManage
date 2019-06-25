package com.NeuDocManage;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.IndexNode;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.service.BlockService.readBlock;
import static com.NeuDocManage.service.DirService.*;
import static com.NeuDocManage.service.DiskService.initDisk;
import static com.NeuDocManage.service.DiskService.releaseDisk;
import static com.NeuDocManage.service.FileService.*;
import static org.junit.Assert.assertEquals;

public class FileServiceTest {
    /**
     * 测试ls createFile
     * @throws IOException
     */
    @Test
    public void test1() throws IOException{
        initDisk();//初始化磁盘
        int id = mkdir("name");
        //System.out.println(id);
        IndexNode inode= JSON.parseObject(readBlock(id).trim(),IndexNode.class);
        //System.out.println("bbb"+readBlock(id));
        HostHolder.setCurDir(inode); //设置当前目录是name
        int id2 = mkdir("aaa");
        IndexNode inode2= JSON.parseObject(readBlock(id2).trim(),IndexNode.class);
        //HostHolder.setCurDir(inode2); //设置当前目录是aaa
        int id3 = mkdir("bbb");
        HostHolder.setCurDir(inode); //设置当前目录是name
        int id4 = createFile("hhh");
        for(String s : listDir()){
            System.out.println(s+" ");
        }
        releaseDisk();
    }
    /**
     * 测试读写文件
     * @throws IOException
     */
    @Test
    public void test4()throws IOException{
        initDisk();//初始化磁盘
        int id = createFile("hhh");
        System.out.println(id);
        readFile("hhh");
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < 1000; ++i){
            res.append("s");
        }
        String content = res.toString();
        writeFile("hhh",content);
        //writeFile("hhh","12");
        showInfo(id);
        assertEquals(content,readFile("hhh"));
        releaseDisk();
    }
}