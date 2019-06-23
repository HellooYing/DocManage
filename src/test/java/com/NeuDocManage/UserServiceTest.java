package com.NeuDocManage;

import com.NeuDocManage.model.HostHolder;
import com.NeuDocManage.model.User;
import org.junit.Test;

import java.io.IOException;

import static com.NeuDocManage.service.DiskService.initDisk;
import static com.NeuDocManage.service.UserService.*;
import static org.junit.Assert.assertEquals;

public class UserServiceTest {
    @Test
    public void test1() throws IOException {
        initDisk();
        User user=new User("mcq","mcq");
        register(user);
        register(user);
        for (int i = 0; i <10 ; i++) {
            user.setUserName("mcq"+i);
            register(user);
        }
        login("mcq","mcq");
        assertEquals("mcq", HostHolder.getUser().getUserName());
        login("root","root");
        deleteUser("mcq");
        login("mcq","mcq");
        for (int i = 0; i <10 ; i++) {
            deleteUser("mcq"+i);
        }
    }
}
