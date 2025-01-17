package com.lld.problems.stackoverflow;

import com.lld.problems.stackoverflow.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Driver {
    public static void main(String[] args) {
        Map<Integer,User> map = new HashMap<>();
        User user = new User(UUID.randomUUID().toString(),"Raj",1);
        map.put(1,user);
        user.setName("Simran");
        System.out.println(map.get(1).getName());
    }
}
