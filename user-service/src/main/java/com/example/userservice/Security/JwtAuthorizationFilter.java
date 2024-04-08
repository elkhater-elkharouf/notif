package com.example.userservice.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.userservice.Entities.User;
import com.example.userservice.Services.User.IUserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private IUserService userService;

    public JwtAuthorizationFilter(IUserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

          if (request.getServletPath().equals("/refreshToken")){
              filterChain.doFilter(request,response);
          }else {


            String authorizationToken = request.getHeader(JWTUtil.AUTH_HEADER);
            //apres l'interception d la requete en fait verification
            if(authorizationToken!=null && authorizationToken.startsWith(JWTUtil.PREFIX_HEADER)){
                try{
                    //ignorer Bearer
                    String jwt  = authorizationToken.substring(JWTUtil.PREFIX_HEADER.length());
                    //meme secret pour generate signature ici pour verifier token
                    Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
                    JWTVerifier jwtVerifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = jwtVerifier.verify(jwt);
                    String email = decodedJWT.getSubject();
                    String[] privileges = decodedJWT.getClaim("privileges").asArray(String.class);
                    Collection<GrantedAuthority> authorities = new ArrayList<>();
                    for(String p:privileges){
                        authorities.add(new SimpleGrantedAuthority(p));
                    }
                    User user = userService.laodUserByUserName(email);
                    String rolename = user.getRole().getRoleName();
                     UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(email,null,authorities);

            //authentifier ce user
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    //passage a autre filtre suivant
            filterChain.doFilter(request,response);
                }catch (Exception e){
                    response.setHeader("error-message",e.getMessage());
                    response.sendError(HttpServletResponse.SC_FORBIDDEN);
                }


            }else {
              filterChain.doFilter(request,response);
            } }
    }
}
