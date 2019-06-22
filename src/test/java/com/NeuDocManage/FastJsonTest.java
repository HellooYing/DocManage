package com.NeuDocManage;

import com.NeuDocManage.model.Block;
import com.NeuDocManage.model.DataBlock;
import com.alibaba.fastjson.JSON;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FastJsonTest {
    /**
     * 测试FastJson是否能正常序列化和反序列化类
     * 序列化：String jsonObject = JSON.toJSONString(Class);
     * 反序列化：Class newClass = JSON.parseObject(jsonObject, Class.class);
     */
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

    /**
     * 测试FastJson可以将子类序列化后的字符串反序列化为父类而不报错
     */
    @Test
    public void test2(){
        DataBlock dataBlock=new DataBlock("123",2,false);
        String jsonObject = JSON.toJSONString(dataBlock);
        Block block = JSON.parseObject(jsonObject, Block.class);
        assertEquals("DataBlock",block.getName());
    }
}

