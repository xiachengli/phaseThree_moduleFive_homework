package com.xcl.service.impl;

import com.xcl.mapper.UserMapper;
import com.xcl.pojo.User;
import com.xcl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public User login(String email, String password) {
        return userMapper.login(email,password);
    }

    @Override
    public void saveToken(String email, String token) {
        userMapper.saveToken(email,token);
    }

    @Override
    public User queryByEmail(String email) {
        return userMapper.queryByEmail(email);
    }

    @Override
    public void save(String email, String password,String token) {
        User user = new User();
        user.setId(UUID.randomUUID().getMostSignificantBits());
        user.setPassword(password);
        user.setEmail(email);
        user.setToken(token);
        userMapper.save(user);
    }
}
