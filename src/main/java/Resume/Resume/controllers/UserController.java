package Resume.Resume.controllers;

import Resume.Resume.entity.LoginResponse;
import Resume.Resume.entity.MyError;
import Resume.Resume.entity.User;
import Resume.Resume.services.UserService;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.io.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/register")
    public String register(Model model , MyError myError , HttpSession session){

        String[] arr = {};

        System.out.println( myError.getMessage() );

        if(!    myError.getMessage().equals("")){
            arr = myError.getMessage().split("___");
        }

        String[] inputErrors = new String[arr.length];

        for( int i = 0; i < arr.length ; i++ ){
            String[] errorExplode = arr[i].split(" ");
            inputErrors[i] = errorExplode[0].toLowerCase();
        }



        model.addAttribute("error" , arr);

        model.addAttribute("inputError" , inputErrors);
        return "register";
    }

    @GetMapping("/login")
    public String adminlogin(Model model , @RequestParam("error") Optional<String> err ){

        if(err.isPresent()){
            model.addAttribute("error" , "Invalid Credentials!");
        }

        return "login";
    }


    @GetMapping("/home")
    public String home(Model model){

        return "home";
    }


    @GetMapping("/upload")
    public String upload(Model model ){

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
            return "redirect:/register?message="+"Email Error! already registered. Please login or use different mail";
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        String hashKey = bCryptPasswordEncoder.encode( user.getPassword() );

        user.setPassword( hashKey );

        boolean checker = userService.save( user );

        System.out.println(checker);

        if( !checker ){
            return "redirect:/register?message="+"Generic Error! Something went wrong. Maybe duplicate entry for user";
        } else {

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
    public String uploadcv(Model model, Principal principal, HttpSession session , @RequestParam("resumefile") MultipartFile file ) throws IOException {

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


        userService.update( principal.getName() , mylink);

        return "redirect:/home";
    }


    @RequestMapping(value = "/admin" )
    public String admin(Model model ){

        List<User> users = userService.getall();

        model.addAttribute( "users" , users );
        return "admin";
    }


}
