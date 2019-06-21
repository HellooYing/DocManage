package com.NeuDocManage;

import com.NeuDocManage.service.DiskService;
import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.service.DataService.getDataBlock;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataServiceTest {
    @Test
    public void test1() throws IOException {
        DiskService.initDisk();
        int blockId=getDataBlock();
        System.out.println(blockId);
        for (int i = 0; i <426 ; i++) {
            blockId=getDataBlock();
            if(i%5==0) System.out.println(blockId);
        }
    }
}
