package Resume.Resume.controllers;

import Resume.Resume.entity.User;
import Resume.Resume.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    @GetMapping("/login/{logintype}")
    public String adminlogin(Model model , @PathVariable("logintype") String type ){
        if(type.equals("user")){
            return "userlogin";
        } else if(type.equals("admin")){
            return "adminlogin";
        } else {
            return "login";
        }
    }


    @GetMapping("/home")
    public String home( Model model ){
        return "home";
    }

    @PostMapping("/submit")
    public String submit(Model model , @RequestParam("username") String username, @RequestParam("password") String password ){
        model.addAttribute("username" , username);
        model.addAttribute("password" , password);
        return "submit";
    }

    @PostMapping("/save")
    public String save(Model model , @RequestParam("name") String username, @RequestParam("password") String password, @RequestParam("email") String email ){
        model.addAttribute("username" , username);
        model.addAttribute("password" , password);
        model.addAttribute("email" , email);

        User user = new User();

        user.setEmail(email);
        user.setPassword(password);
        user.setName(username);

        userService.save(user);

        model.addAttribute("id" , user.getId()+"hi" );


        return "save";
    }


}
