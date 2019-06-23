package com.NeuDocManage;


import com.NeuDocManage.model.DataBlock;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.io.IOException;

public class BlockSizeTest {
    /**
     * 测试磁盘块BlockSize的大小(文件为空的情况)
     * @throws IOException
     */
    @Test
    public void test1() throws IOException {
        DataBlock dataBlock = new DataBlock();
        System.out.println(JSON.toJSONString((dataBlock)).length());
    }

    /**
     * 测试Inode节点最大为多大
     * @throws IOException
     */
    @Test
    public void test2() throws IOException {

    }
}
