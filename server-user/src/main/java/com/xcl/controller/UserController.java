package com.xcl.controller;

import com.xcl.feign.CodeFeign;
import com.xcl.pojo.User;
import com.xcl.service.UserService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    CodeFeign codeFeign;

    @RequestMapping("/test")
    public int test(){
        return 1;
    }

    /**
     * 登录接口
     * @param email
     * @param password
     * @return
     */
    @RequestMapping("/login/{email}/{password}")
    public Boolean login(HttpServletResponse response,@PathVariable String email, @PathVariable String password){
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
            return false;
        }
        try{
            User user = userService.login(email,password);
            if (user == null){
                return false;
            }else {
                //保存token
                String token = UUID.randomUUID().toString();
                Cookie cookie = new Cookie("token",token);
                response.addCookie(cookie);

                Cookie emailCookie = new Cookie("email",email);
                emailCookie.setPath("/");
                response.addCookie(emailCookie);

                userService.saveToken(email,token);
                return true;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 注册接口
     * @param email 邮箱
     * @param password 密码
     * @param code 验证码
     * @return
     */
    @RequestMapping("/register/{email}/{password}/{code}")
    public String register(HttpServletResponse response,@PathVariable String email,@PathVariable String password, @PathVariable String code){
        if (StringUtils.isBlank(email) || StringUtils.isBlank(password) || StringUtils.isBlank(code)) {
            return "必填参数不允许为空";
        }
        try{
            //判断该邮箱是否已被注册
            User user = userService.queryByEmail(email);
            if (user != null) {
                return "该邮箱已被注册";
            }
            //判断验证码是否正确
            String result = codeFeign.validateCode(email,code);
            if (!"true".equals(result)) {
                return result;
            }
            String token = UUID.randomUUID().toString();
            Cookie cookie = new Cookie("token",token);
            response.addCookie(cookie);
            userService.save(email,password,token);

            Cookie emailCookie = new Cookie("email",email);
            emailCookie.setPath("/");
            response.addCookie(emailCookie);
            return "true";
        }catch (Exception e) {
            e.printStackTrace();
            return "注册异常";
        }

    }
}
