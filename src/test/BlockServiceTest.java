package test;

import org.junit.Assert;
import org.junit.Test;
import service.DiskService;

import java.io.IOException;

import static service.BlockService.*;


public class BlockServiceTest {
    @Test
    public void test1() throws IOException {
        DiskService.initDisk();//初始化磁盘
        formatBlock(3);
        Assert.assertEquals(0,writeBlock(3,"12345"));
        Assert.assertEquals(0,overwriteBlock(3,"12345"));
        Assert.assertEquals("12345",readBlock(3).trim());
    }
}
