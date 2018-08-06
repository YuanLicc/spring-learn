package com.yl.learn.mvc.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yl.learn.entity.User;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void printApiInfo() {
        System.out.println("name: 'mvc', description: 'test mvc', path: 'mvc'");
    }

    @ResponseBody
    @RequestMapping(value = "hello", method = RequestMethod.GET)
    public String hello() {
        return "hello";
    }

    @ResponseBody
    @RequestMapping(value = "getUser", method = RequestMethod.GET)
    public String getUser() {
        User user = sessionFactory.openSession().get(User.class, 1);
        return JSONObject.toJSONString(user);
    }

}
