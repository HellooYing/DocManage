package com.NeuDocManage;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.User;
import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.service.DiskService.initDisk;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HostHolderTest {
    @Test
    public void test1(){
        User user=new User();
        user.setUserName("mcq");
        HostHolder.setUser(user);
        assertEquals("mcq",HostHolder.getUser().getUserName());
        User user2=new User();
        user2.setUserName("mcq2");
        HostHolder.setUser(user2);
        assertEquals("mcq2",HostHolder.getUser().getUserName());
        HostHolder.clearUser();
        assertNull(HostHolder.getUser());
    }

    @Test
    public void test2() throws IOException {
        initDisk();
        assertEquals(1,HostHolder.getCurDir().getId());
        assertEquals("root",HostHolder.getUser().getUserName());
    }

}
