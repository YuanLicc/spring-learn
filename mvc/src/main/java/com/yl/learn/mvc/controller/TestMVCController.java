package com.yl.learn.mvc.controller;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Aspect
@Controller
@Component
@RequestMapping("mvc")
public class TestMVCController implements ControllerMark {

    @Override
    public void printApiInfo() {
        System.out.println("name: 'mvc', description: 'test mvc', path: 'mvc'");
    }

    public String kk(String aa) {
        return "ccc" + aa;
    }

    @ResponseBody
    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public String hello() {
        return "hello";
    }

}
