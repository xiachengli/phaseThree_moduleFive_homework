package com.xcl.service;

import com.xcl.pojo.User;

public interface UserService {

    User login(String email, String password);

    void saveToken(String email, String token);

    User queryByEmail(String email);

    void save(String email, String password,String token);
}
