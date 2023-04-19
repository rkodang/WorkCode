package com.gumdom.boot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @ResponseBody
    @RequestMapping("/on")
    public Map<String,Object> handle(){
        Map<String,Object> param = new HashMap<>(1);
        param.put("A","ABC");
        param.put("B","BBC");
        return param;
    }

}
