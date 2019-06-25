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
        //System.out.println("bbb"+readBlock(id));
        HostHolder.setCurDir(inode); //设置当前目录是name
        int id2 = mkdir("aaa");
        IndexNode inode2= JSON.parseObject(readBlock(id2).trim(),IndexNode.class);
        HostHolder.setCurDir(inode2); //设置当前目录是aaa
        int id3 = mkdir("bbb");
        HostHolder.setCurDir(inode); //设置当前目录是name
        assertEquals(id3,changeDir("./aaa/bbb"));
        releaseDisk();
    }

}
