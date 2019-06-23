package com.NeuDocManage;

import org.junit.Test;

import java.io.IOException;
import java.util.Stack;

import static com.NeuDocManage.service.DataService.getDataBlock;
import static com.NeuDocManage.service.DataService.recoverDataBlock;
import static com.NeuDocManage.service.DiskService.initDisk;
import static com.NeuDocManage.service.DiskService.releaseDisk;
import static org.junit.Assert.assertEquals;

public class DataServiceTest {
    /**
     * 测试getDataBlock()，可以将盘块全部正常分配出去，直到磁盘存满
     * 运行该测试会导致磁盘存满，产生问题，所以不运行
     * @throws IOException
     */
//    @Test
//    public void test1() throws IOException {
//        initDisk();
//        int blockId=getDataBlock();
//        System.out.println(blockId);
//        for (int i = 0; i <426 ; i++) {
//            blockId=getDataBlock();
//            if(i%5==0) System.out.println(blockId);
//        }
//
//        releaseDisk();
//    }

    /**
     * 测试recoverDataBlock(blockId)，先分配再回收，下次分配的与上次回收的相同，多次分配回收盘块结果也相同。
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {
        initDisk();
        int blockId=getDataBlock();
        recoverDataBlock(blockId);
        assertEquals(blockId,getDataBlock());
        blockId=getDataBlock();
        recoverDataBlock(blockId);
        Stack<Integer> s=new Stack<Integer>();
        for (int i = 0; i <150 ; i++) {
            s.push(getDataBlock());
        }
        for (int i = 0; i <150 ; i++) {
            recoverDataBlock(s.pop());
        }
        assertEquals(blockId,getDataBlock());
        releaseDisk();
    }
}
