package com.NeuDocManage;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class HostHolderTest {
    @Test
    public void test1(){
        User user=new User();
        user.setUserName("mcq");
        HostHolder.setUsers(user);
        assertEquals("mcq",HostHolder.getUser().getUserName());
        User user2=new User();
        user2.setUserName("mcq2");
        HostHolder.setUsers(user2);
        assertEquals("mcq2",HostHolder.getUser().getUserName());
        HostHolder.clear();
        assertNull(HostHolder.getUser());
    }
}
