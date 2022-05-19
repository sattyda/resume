package Resume.Resume.controllers;

import Resume.Resume.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/")
    public String index( Model model ){
        model.addAttribute("MyName" , "Sattyda");

        return "index";
    }

    @GetMapping("/register")
    public String register( Model model ){
        return "register";
    }

    @GetMapping("/login")
    public String login( Model model ){
        return "login";
    }

    @GetMapping("/home")
    public String home( Model model ){
        return "home";
    }

}
