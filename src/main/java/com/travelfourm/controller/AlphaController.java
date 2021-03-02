package com.travelfourm.controller;

import com.travelfourm.Util.CommunityUtil;
import com.travelfourm.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.rmi.server.RMIClassLoader;
import java.util.Enumeration;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/hello")
    @ResponseBody
    public String SayHello(){
        return "hello,springboot";
    }

    @RequestMapping("/data")
    @ResponseBody
    public String getData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        //获取请求数据的来源
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration=request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name=enumeration.nextElement();
            String value=request.getHeader(name);
            System.out.println(name+":"+value);
        }
        System.out.println(request.getParameter("code"));

        //返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try{
            PrintWriter writer=response.getWriter();
            writer.write("<h1>测试</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * GET请求
     * students?current=1&limit=20*/

    @RequestMapping(path = "/students",method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current",required = false,defaultValue = "1")int current,
            @RequestParam(name = "limit",required = false,defaultValue = "10")int limit
    ){
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    @RequestMapping(path = "/students/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id")int id){
        System.out.println(id);
        return "a student";
    }

    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    @ResponseBody
    public String testAjax(String name,int age){
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJsonString(0,"操作成功");
    }
}
