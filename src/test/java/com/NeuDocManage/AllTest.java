package com.NeuDocManage;

import org.junit.Test;

import java.io.IOException;

public class AllTest {
    @Test
    public void allTest() throws IOException {
        System.out.println("测试DataService");
        new DataServiceTest().test2();
        System.out.println("测试INodeService");
        new INodeServiceTest().test2();
    }
}
