package Resume.Resume.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class ApiSecurity extends UsernamePasswordAuthenticationFilter {

    AuthenticationManager authenticationManager;

    ApiSecurity(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username , password);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();

        Algorithm algorithm = Algorithm.HMAC512( "sattyda".getBytes());

        String access_token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer( request.getRequestURL().toString() )
                .withExpiresAt( new Date( System.currentTimeMillis() + 1*60*60*1000 ))
                .sign(algorithm);

        String refresh_token = JWT.create()
                .withSubject(userDetails.getUsername())
                .withIssuer( request.getRequestURL().toString() )
                .withExpiresAt( new Date( System.currentTimeMillis() + 1*60*60*1000 ))
                .sign(algorithm);


        response.setHeader("access_token" , access_token);
        response.setHeader("refresh_token" , refresh_token);

    }
}
