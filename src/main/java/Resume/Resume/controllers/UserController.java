package Resume.Resume.controllers;

import Resume.Resume.entity.LoginResponse;
import Resume.Resume.entity.MyError;
import Resume.Resume.entity.User;
import Resume.Resume.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;

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
    public String register(Model model , MyError myError){

        String[] arr = {};

        System.out.println( myError.getMessage() );
        if(!    myError.getMessage().equals("")){
            arr = myError.getMessage().split("___");
        }


        model.addAttribute("error" , arr);

        return "register";
    }

    @GetMapping("/login")
    public String adminlogin(Model model , MyError myError ){
        String[] arr = {};

        System.out.println( myError.getMessage() );
        if(!    myError.getMessage().equals("")){
            arr = myError.getMessage().split("___");
        }

        model.addAttribute("error" , arr);

        return "login";

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
    public String save( Model model , @Valid User user , BindingResult result ){
        if(result.hasErrors()){
            List<ObjectError> ll =  result.getAllErrors();
            String err = "";

            for( int i =0; i < ll.size(); i++  ){

                err = err + ll.get(i).getDefaultMessage() + "___" ;
            }

            return "redirect:/register?message="+err;
        }
        userService.save(user);
        model.addAttribute("id" , user.getId()+"hi" );
        return "save";
    }


    @PostMapping("/loginsubmit")
    public String loginsubmit( Model model , @Valid User user , BindingResult result ){
        if(result.hasErrors()){
            List<ObjectError> ll =  result.getAllErrors();
            String err = "";

            for( int i =0; i < ll.size(); i++  ){

                err = err + ll.get(i).getDefaultMessage() + "___" ;
            }

            return "redirect:/login?message="+err;
        }

        LoginResponse loginResponse = userService.login(user);

        if(loginResponse.getStatus().equals("success")){
            return "redirect:/home";
        } else {
            return "redirect:/login?message="+loginResponse.getMessage();
        }

    }


}
