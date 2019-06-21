package com.NeuDocManage;

import com.NeuDocManage.service.DiskService;
import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.service.BlockService.*;
import static org.junit.Assert.assertEquals;

public class BlockServiceTest {
    /**
     * 测试formatBlock,writeBlock,overwriteBlock,readBlock四个底层io函数
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        DiskService.initDisk();//初始化磁盘
        formatBlock(3);
        assertEquals(0,writeBlock(3,"12345"));
        assertEquals(0,overwriteBlock(3,"12345"));
        assertEquals("12345",readBlock(3).trim());
    }
}
