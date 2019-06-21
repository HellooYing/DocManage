package com.NeuDocManage;

import com.NeuDocManage.model.DataBlock;
import com.alibaba.fastjson.JSON;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FastJsonTest {
    @Test
    public void test1(){
        DataBlock dataBlock = new DataBlock();
        dataBlock.setData("123");
        dataBlock.setNextDataId(2);
        dataBlock.setUsed(false);
        String jsonObject = JSON.toJSONString(dataBlock);
        System.out.println(jsonObject);
        DataBlock newDataBlock = JSON.parseObject(jsonObject, DataBlock.class);
        assertEquals("123",newDataBlock.getData());
    }
}
