package Resume.Resume.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class CustomAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if( !request.getRequestURL().isEmpty() && request.getServletPath().equals("/login") ){
            filterChain.doFilter(request , response);
        } else {
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

                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(username , null    );

                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                        filterChain.doFilter(request , response);

                    } catch (Exception e){
                        response.setHeader("error", "Token Invalid");
                        response.setHeader("cachedError", e.getMessage());
                        response.sendError( 403 );
                    }
                }
            }


        }
    }


}
