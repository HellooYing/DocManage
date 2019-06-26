package com.NeuDocManage;

import com.NeuDocManage.service.DiskService;
import org.junit.Test;

import java.io.IOException;

public class AllTest {
    @Test
    public void allTest() throws IOException, InterruptedException {
        System.out.println("测试DataService");
        new DataServiceTest().test2();

        System.out.println("测试INodeService");
        new INodeServiceTest().test2();

        System.out.println("测试DirService");
        new DirServiceTest().test2();

        DiskService.formatDisk();

        System.out.println("测试UserService");
        new UserServiceTest().test1();

        DiskService.formatDisk();
        System.out.println("测试FileService");
        FileServiceTest test=new FileServiceTest();
        test.test1();
        test.test2();
        test.test3();
    }
}
