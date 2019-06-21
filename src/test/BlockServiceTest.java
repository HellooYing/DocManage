package test;

import org.junit.Test;
import service.DiskService;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static service.BlockService.*;

public class BlockServiceTest {
    @Test
    public void test1() throws IOException {
        DiskService.initDisk();//初始化磁盘
        formatBlock(3);
        assertEquals(0,writeBlock(3,"12345"));
        assertEquals(0,overwriteBlock(3,"12345"));
        assertEquals("12345",readBlock(3).trim());
    }
}
