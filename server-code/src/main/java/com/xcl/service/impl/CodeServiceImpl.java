package com.xcl.service.impl;

import com.xcl.mapper.CodeMapper;
import com.xcl.pojo.Code;
import com.xcl.service.CodeService;
import com.xcl.service.EmailService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.UUID;

@Service
public class CodeServiceImpl implements CodeService {

    @Reference
    EmailService emailService;

    @Autowired
    CodeMapper codeMapper;

    @Override
    public boolean saveAndSend(String email, String code) {
        Long id =  UUID.randomUUID().getLeastSignificantBits();
        emailService.sendEmail(email,code);
        codeMapper.save(id,email,code);
        return true;
    }

    @Override
    public List<Code> queryByEmail(String email) {
        return codeMapper.queryByEmail(email);
    }
}
