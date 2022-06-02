package Resume.Resume.controllers;

import Resume.Resume.entity.User;
import Resume.Resume.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ApiController {

    @Autowired
    UserService userService;


    @PostMapping("/api/register")
    public ResponseEntity<User> register( @Valid User user , BindingResult result){

        if(result.hasErrors()){
            List<ObjectError> ll =  result.getAllErrors();
            String err = "";

            for( int i =0; i < ll.size(); i++  ){

                err = err + ll.get(i).getDefaultMessage() + "___" ;
            }

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if(userService.isExist(user.getEmail())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String hashKey = bCryptPasswordEncoder.encode( user.getPassword() );
        user.setPassword( hashKey );
        boolean checker = userService.save(user);

        if( !checker ){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            user.setPassword(null);
            return new ResponseEntity<>( user  , HttpStatus.OK);
        }

    }

    @PostMapping("/api/secured/resume")
    public ResponseEntity<String> resume(@RequestParam String username){

        String resume = userService.getResume(username);

        return new ResponseEntity<>( resume , HttpStatus.OK);
    }

    @CircuitBreaker(name = "resumeService" , fallbackMethod = "resumeFail")
    @GetMapping("/api/profile")
    public ResponseEntity<String> profile( User user){
        String ren = new RestTemplate().exchange("https://google.com", HttpMethod.GET , new HttpEntity<>( new HttpHeaders()) , String.class).toString();
        return new ResponseEntity<>(ren,  HttpStatus.OK);
    }

    public ResponseEntity<String> resumeFail( Exception e){
        return new ResponseEntity<>("I was Called" , HttpStatus.OK);
    }

}
