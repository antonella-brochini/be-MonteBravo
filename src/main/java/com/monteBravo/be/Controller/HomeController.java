package com.monteBravo.be.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HomeController {

    @RequestMapping("/")
    public String home() {return "Welcome";}

    @RequestMapping("/admin/user")
    public Principal user(Principal user) {return user;}
}
