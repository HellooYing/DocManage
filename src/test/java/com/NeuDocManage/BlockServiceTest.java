package com.NeuDocManage;

import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.config.MainConfig.BLOCKNUM;
import static com.NeuDocManage.service.BlockService.*;
import static com.NeuDocManage.service.DiskService.initDisk;
import static com.NeuDocManage.service.DiskService.releaseDisk;
import static org.junit.Assert.assertEquals;

public class BlockServiceTest {
    /**
     * 测试formatBlock,writeBlock,overwriteBlock,readBlock四个底层io函数
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        initDisk();//初始化磁盘
        formatBlock(BLOCKNUM-3);
        assertEquals(0,writeBlock(BLOCKNUM-3,"12345"));
        assertEquals(0,overwriteBlock(BLOCKNUM-3,"12345"));
        assertEquals("12345",readBlock(BLOCKNUM-3).trim());
        formatBlock(BLOCKNUM-3);
        releaseDisk();
    }
}
