package com.NeuDocManage;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.User;
import com.NeuDocManage.service.DiskService;
import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.config.MainConfig.SUPERBLOCKNUM;
import static com.NeuDocManage.config.MainConfig.SUPERBLOCKSTART;
import static com.NeuDocManage.service.DirService.*;
import static org.junit.Assert.assertEquals;

public class DirServiceTest {
    /**
     * 测试mkdir
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        DiskService.initDisk();//初始化磁盘
        HostHolder user = new HostHolder(); //初始化用户
        User user1 = new User();
        user1.setUserName("mcq");
        user.setUsers(user1);
        setCurDir(SUPERBLOCKSTART+SUPERBLOCKNUM); //设置当前目录是root
        System.out.println(mkdir("name"));
    }

    /**
     * 测试cd
     * @throws IOException
     */
    @Test
    public void test2() throws IOException{
        DiskService.initDisk();//初始化磁盘
        HostHolder user = new HostHolder(); //初始化用户
        User user1 = new User();
        user1.setUserName("mcq");
        user.setUsers(user1);
        setCurDir(SUPERBLOCKSTART+SUPERBLOCKNUM); //设置当前目录是root
        int id = mkdir("name");
        setCurDir(id); //设置当前目录是name
        int id2 = mkdir("fuck");
        assertEquals(id,changeDir("./fuck"));
    }
}
