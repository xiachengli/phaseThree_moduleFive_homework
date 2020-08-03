package com.xcl.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("server-code")
public interface CodeFeign {

    @RequestMapping("/code/validateCode/{email}/{code}")
    public String  validateCode(@PathVariable String email, @PathVariable String code);
}
