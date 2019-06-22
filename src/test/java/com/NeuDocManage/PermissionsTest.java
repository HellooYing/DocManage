package com.NeuDocManage;

import com.NeuDocManage.model.Permissions;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PermissionsTest {
    /**
     * 测试Permissions类的功能，
     * 主要有
     * 构造函数：Permissions(7)
     * 修改权限值：chmod(int mode)/chmod("xwr")
     * 修改单个权限：setExecute(true)/setRead(false)/setWrite(true)
     * 查询是否能执行、读、写：canExecute()/canRead()/canWrite()
     */
    @Test
    public void test1(){
        Permissions permissions=new Permissions(7);
        assertEquals(true,permissions.canExecute());
        assertEquals(true,permissions.canRead());
        assertEquals(true,permissions.canWrite());
        permissions.chmod(6);
        assertEquals(false,permissions.canExecute());
        assertEquals(true,permissions.canRead());
        assertEquals(true,permissions.canWrite());
        permissions.chmod(5);
        assertEquals(true,permissions.canExecute());
        assertEquals(true,permissions.canRead());
        assertEquals(false,permissions.canWrite());
        permissions.chmod(1);
        assertEquals(true,permissions.canExecute());
        assertEquals(false,permissions.canRead());
        assertEquals(false,permissions.canWrite());
        permissions.chmod("x");
        assertEquals(true,permissions.canExecute());
        assertEquals(false,permissions.canRead());
        assertEquals(false,permissions.canWrite());
        permissions.chmod("xr");
        assertEquals(true,permissions.canExecute());
        assertEquals(true,permissions.canRead());
        assertEquals(false,permissions.canWrite());
        permissions.chmod("rx");
        assertEquals(true,permissions.canExecute());
        assertEquals(true,permissions.canRead());
        assertEquals(false,permissions.canWrite());
        permissions.chmod("wrx");
        assertEquals(true,permissions.canExecute());
        assertEquals(true,permissions.canRead());
        assertEquals(true,permissions.canWrite());
        permissions.setExecute(false);
        assertEquals(6,permissions.getMode());
        permissions.setRead(false);
        assertEquals(2,permissions.getMode());
    }
}
