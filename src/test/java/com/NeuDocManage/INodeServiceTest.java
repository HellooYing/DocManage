package com.NeuDocManage;

import com.NeuDocManage.service.INodeServie;
import org.junit.Test;

import java.io.IOException;
import java.util.Stack;

import static com.NeuDocManage.service.DiskService.initDisk;
import static com.NeuDocManage.service.DiskService.releaseDisk;
import static com.NeuDocManage.service.INodeServie.getIndexBlock;
import static com.NeuDocManage.service.INodeServie.recoverIndexBlock;
import static org.junit.Assert.assertEquals;

public class INodeServiceTest {

//    @Test
//    public void test1() throws IOException {
//        initDisk();
//        int blockId=getIndexBlock();
//        System.out.println(blockId);
//        for (int i = 0; i <64 ; i++) {
//            blockId=getIndexBlock();
//            if(i%5==0) System.out.println(blockId);
//        }
//        releaseDisk();
//    }

    @Test
    public void test2() throws IOException {
        initDisk();
        int blockId=getIndexBlock();
        recoverIndexBlock(blockId);
        assertEquals(blockId,getIndexBlock());
        blockId=getIndexBlock();
        recoverIndexBlock(blockId);
        Stack<Integer> s=new Stack<Integer>();
        for (int i = 0; i <66 ; i++) {
            s.push(getIndexBlock());
        }
        for (int i = 0; i <66 ; i++) {
            recoverIndexBlock(s.pop());
        }
        assertEquals(blockId,getIndexBlock());
        releaseDisk();
    }
}
