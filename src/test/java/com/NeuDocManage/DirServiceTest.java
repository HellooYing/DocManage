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
import static org.junit.Assert.assertEquals;

public class DirServiceTest {
    /**
     * 测试mkdir
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {

        initDisk();//初始化磁盘
        System.out.println(mkdir("name"));
        releaseDisk();
    }

    /**
     * 测试cd
     * @throws IOException
     */
    @Test
    public void test2() throws IOException{

        initDisk();//初始化磁盘
        int id = mkdir("name");
        //System.out.println(id);
        IndexNode inode= JSON.parseObject(readBlock(id).trim(),IndexNode.class);
        //System.out.println("shit"+readBlock(id));
        HostHolder.setCurDir(inode); //设置当前目录是name
        int id2 = mkdir("fuck");
        IndexNode inode2= JSON.parseObject(readBlock(id2).trim(),IndexNode.class);
        HostHolder.setCurDir(inode2); //设置当前目录是fuck
        int id3 = mkdir("shit");
        HostHolder.setCurDir(inode); //设置当前目录是name
        assertEquals(id3,changeDir("./fuck/shit"));
        releaseDisk();
    }
    /**
     * 测试ls createFile
     * @throws IOException
     */
    @Test
    public void test3() throws IOException{
        initDisk();//初始化磁盘
        int id = mkdir("name");
        //System.out.println(id);
        IndexNode inode= JSON.parseObject(readBlock(id).trim(),IndexNode.class);
        //System.out.println("shit"+readBlock(id));
        HostHolder.setCurDir(inode); //设置当前目录是name
        int id2 = mkdir("fuck");
        IndexNode inode2= JSON.parseObject(readBlock(id2).trim(),IndexNode.class);
        //HostHolder.setCurDir(inode2); //设置当前目录是fuck
        int id3 = mkdir("shit");
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
        writeFile("hhh","123456");
        writeFile("hhh","12");
        assertEquals("123456 12",readFile("hhh"));
        releaseDisk();
    }
}
