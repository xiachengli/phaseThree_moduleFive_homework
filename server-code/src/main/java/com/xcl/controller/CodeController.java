package com.xcl.controller;

import com.xcl.pojo.Code;
import com.xcl.service.CodeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("code")
public class CodeController {

    @Autowired
    CodeService codeService;

    /**
     * 获取验证码接口
     * @param email
     * @return
     */
    @RequestMapping("/getCode/{email}")
    public Boolean getCode(@PathVariable String email){
        if (StringUtils.isBlank(email)) {
            System.out.println("获取验证码接口，邮箱不允许为空"+email);
        }
        try{
            String code = generatorCode();
            codeService.saveAndSend(email,code);
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证验证码
     * @param email
     * @param code
     * @return
     */
    @RequestMapping("/validateCode/{email}/{code}")
    public String  validateCode(@PathVariable String email,@PathVariable String code){
        if (StringUtils.isBlank(email) || StringUtils.isBlank(code)) {
            System.out.println("校验验证码接口，验证码不允许为空"+email+":"+code);
            return "必填参数不允许为空";
        }
        List<Code> validateCode = codeService.queryByEmail(email);
        if (validateCode == null || validateCode.size() == 0) {
            return "无有效验证码，请先获取验证码";
        }
        //判断是否过期
        Date now = new Date();
        if (code.equals(validateCode.get(0).getCode())) {
            if (now.compareTo(validateCode.get(0).getExpireTime()) > 0) {
                return "验证码已超时";
            }else {
                return "true";
            }
        }else {
            return "验证码不正确";
        }
    }

    /**
     * 生成6位随机数
     * @return
     */
    private String generatorCode() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i=0; i<6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
