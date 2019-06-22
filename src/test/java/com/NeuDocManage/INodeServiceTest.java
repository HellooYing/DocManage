package com.NeuDocManage;

import com.NeuDocManage.service.DiskService;
import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.service.INodeServie.getIndexBlock;

public class INodeServiceTest {

    @Test
    public void test1() throws IOException {
        DiskService.initDisk();
        int blockId=getIndexBlock();
        System.out.println(blockId);
        for (int i = 0; i <64 ; i++) {
            blockId=getIndexBlock();
            if(i%5==0) System.out.println(blockId);
        }
    }
}
