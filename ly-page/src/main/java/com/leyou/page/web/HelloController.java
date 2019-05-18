package com.leyou.page.web;

import com.leyou.page.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;

@Controller
public class HelloController {
    @GetMapping("hello")
    public String hello(Model model){
        User user = new User();
        user.setAge(21);
        user.setName("Jack Chen");
        User user1 = new User("李小龙", 30, null);
//        model.addAttribute("user",user);
        model.addAttribute("users", Arrays.asList(user,user1));
        return "hello";
    }
}
