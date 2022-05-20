package Resume.Resume.controllers;

import Resume.Resume.entity.LoginResponse;
import Resume.Resume.entity.MyError;
import Resume.Resume.entity.User;
import Resume.Resume.services.UserService;
import lombok.Value;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    private String port = "8181";

    @GetMapping("/")
    public String index( Model model ){
        model.addAttribute("MyName" , "Sattyda");
        return "index";
    }

    @GetMapping("/logout")
    public String logout( HttpSession session ){
        session.invalidate();
        return "redirect:/home";
    }

    @GetMapping("/register")
    public String register(Model model , MyError myError , HttpSession session){

        if(session.getAttribute("isLoggedIn") != null && session.getAttribute("isLoggedIn").equals("Yes") ){
            return "redirect:/home";
        }

        String[] arr = {};

        System.out.println( myError.getMessage() );
        if(!    myError.getMessage().equals("")){
            arr = myError.getMessage().split("___");
        }

        model.addAttribute("error" , arr);
        return "register";
    }

    @GetMapping("/login")
    public String adminlogin(Model model , MyError myError , HttpSession session ){

        if(session.getAttribute("isLoggedIn") != null && session.getAttribute("isLoggedIn").equals("Yes") ){
            return "redirect:/home";
        }

        String[] arr = {};

        System.out.println( myError.getMessage() );
        if(!    myError.getMessage().equals("")){
            arr = myError.getMessage().split("___");
        }

        model.addAttribute("error" , arr);

        return "login";
    }


    @GetMapping("/home")
    public String home(Model model , HttpSession session){
        if(session.getAttribute("isLoggedIn") == null || session.getAttribute("isLoggedIn").equals("No") ){
            return "redirect:/";
        }

        model.addAttribute("name" , session.getAttribute("name"));
        model.addAttribute("email" , session.getAttribute("email"));

        return "home";
    }


    @GetMapping("/upload")
    public String upload(Model model , HttpSession session){
        if(session.getAttribute("isLoggedIn") == null || session.getAttribute("isLoggedIn").equals("No") ){
            return "redirect:/";
        }

        return "upload";
    }

    @PostMapping("/submit")
    public String submit(Model model , @RequestParam("username") String username, @RequestParam("password") String password ){
        model.addAttribute("username" , username);
        model.addAttribute("password" , password);
        return "submit";
    }

    @PostMapping("/save")
    public String save( Model model , @Valid User user , BindingResult result , HttpSession session ){
        if(result.hasErrors()){
            List<ObjectError> ll =  result.getAllErrors();
            String err = "";

            for( int i =0; i < ll.size(); i++  ){

                err = err + ll.get(i).getDefaultMessage() + "___" ;
            }

            return "redirect:/register?message="+err;
        }

        if(userService.isExist(user.getEmail())){
            return "redirect:/register?message="+"Email already registered. Please login or use different mail";
        }

        if(!userService.save(user )){
            return "redirect:/register?message="+"Something went wrong. Maybe duplicate entry for user";
        } else {
            setupSession(session , user);
        }

        return "redirect:/home";
    }

    public void setupSession( HttpSession session , User user){
        session.setAttribute("isLoggedIn" , "Yes");
        session.setAttribute("name" , user.getName() );
        session.setAttribute("email" , user.getEmail() );
        session.setAttribute( "userId" , user.getId() );
    }

    @PostMapping("/loginsubmit")
    public String loginsubmit( Model model , @Valid User user , BindingResult result , HttpSession session ){
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
            setupSession(session , user);
            return "redirect:/home";
        } else {
            return "redirect:/login?message="+loginResponse.getMessage();
        }

    }


    @PostMapping("/uploadcv")
    public String uploadcv( Model model, HttpSession session , @RequestParam("resumefile") MultipartFile file ) throws IOException {

        String mylocation = System.getProperty("user.dir") + "/src/main/resources/static/";

        String filename = file.getOriginalFilename();

        File mySavedFile = new File( mylocation + filename);

        InputStream inputStream = file.getInputStream();

        OutputStream outputStream = new FileOutputStream(mySavedFile);

        int read = 0;
        byte[] bytes = new byte[1024];

        while ((read = inputStream.read(bytes)) != -1){
            outputStream.write(bytes , 0 , read);
        }

        String mylink = "http://localhost:"+ port + "/" + filename;

        User user = new User();

        Long id = (Long) session.getAttribute("userId");
        user.setId( id );

        userService.update(id , mylink);

        return "redirect:/home";
    }

}
