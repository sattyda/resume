package Resume.Resume.controllers;

import Resume.Resume.entity.User;
import Resume.Resume.services.UserService;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

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

    @PostMapping("/api/secured/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response ) throws IOException {
        String authHeader = request.getHeader(AUTHORIZATION);

        if( authHeader == null ){
            response.setHeader("error" , "Not Token");
            response.sendError( 403 );
        } else {
            if( !authHeader.substring(0 ,7 ).equals("Bearer ") ){
                response.setHeader("Error", "Invalid Token Type");
                response.sendError( 403 );
            } else {
                try {
                    String token = authHeader.substring( "Bearer ".length() );
                    Algorithm algorithm = Algorithm.HMAC512( "sattyda".getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    List<String> list = new ArrayList<>();
                    list.add("ROLE_USER");
                    String access_token = JWT.create()
                            .withSubject(username)
                            .withIssuer( request.getRequestURL().toString() )
                            .withClaim("roles" , list)
                            .withExpiresAt( new Date( System.currentTimeMillis() + 10*60*60*1000))
                            .sign(algorithm);

                    Map<String, String> map = new HashMap<>();
                    map.put( "access_token" , access_token);
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), map);
                } catch (Exception e){
                    response.setHeader("error", "Token Invalid");
                    response.setHeader("cachedError", e.getMessage());
                    response.sendError( 403 );
                }
            }
        }
    }
}
