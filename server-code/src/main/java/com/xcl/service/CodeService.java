package com.xcl.service;

import com.xcl.pojo.Code;

import java.util.List;

public interface CodeService {

    boolean saveAndSend(String email,String code);

    List<Code> queryByEmail(String email);
}
