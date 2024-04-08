package com.example.userservice.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.userservice.Services.User.IUserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class  JwtAuthentificationFilter extends UsernamePasswordAuthenticationFilter {
    private AuthenticationManager authenticationManager;
    private IUserService userService;
    public JwtAuthentificationFilter(AuthenticationManager authenticationManager, IUserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws  AuthenticationException {
        System.out.println("attemptAuthentication");
        /*
        String email= request.getParameter("email");
        String password = request.getParameter("password");
         */
        // Convertir les données de la requête en un objet Java en utilisant la bibliothèque Jackson
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(request.getInputStream());

            // Extraire l'email et le mot de passe de l'objet Java
            String email = rootNode.get("email").asText();
            String password = rootNode.get("password").asText();
            // Vérifier si l'utilisateur est désactivé
            com.example.userservice.Entities.User user = userService.laodUserByUserName(email);
            if (user == null){
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{ \"message\": \"" + "Verfier l'email et le mot de passe !" + "\" }");
                return null;
            }

            String errorMessage = "Le compte est encore désactivé";
            if (!user.isEnabled()) {
                /**/


                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{ \"message\": \"" + errorMessage + "\" }");
                return null;

            }

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
            System.out.println(authenticationToken);


            return authenticationManager.authenticate(authenticationToken);

        }catch (IOException e) {
                // Si une erreur se produit lors de la lecture des données de la requête, lancer une exception d'authentification
                throw new BadCredentialsException("Unable to read request body");
            }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");
        //Objet user de springSec de type object
        //  getPrincipal retourner user authentifier
        User user =(User) authResult.getPrincipal();
        //j'ai l'ajouter car j'ai besoin aussi le role d'aprés simple objet user
        com.example.userservice.Entities.User user1 = userService.laodUserByUserName(user.getUsername());
        //géner jwt
        //mysecret est un clé privé avec lui on calcul la signature
        Algorithm algorithm = Algorithm.HMAC256(JWTUtil.SECRET);
        String jwtAccessToken = JWT.create()
                                .withSubject(user.getUsername())
                                //sexpire dans 5min en millissecondes
                                .withExpiresAt(new Date(System.currentTimeMillis()+ JWTUtil.EXP_ACCESS_TOKEN))
                                //nom de l'app qui a gérer le token (on va mettre url de la requete)
                                .withIssuer(request.getRequestURL().toString())
                                //convertit la liste des authoritys de lobjet user spring en liste de priviléges
                                .withClaim("privileges",user.getAuthorities().stream().map(ga->ga.getAuthority()).collect(Collectors.toList()))
                                .withClaim("role",user1.getRole().getRoleName())
                                .sign(algorithm);
        // envoyer jwt au client dans l header  appelé Authorization



        String jwtRefreshToken = JWT.create()
                .withSubject(user.getUsername())
                //sexpire dans 15 min en millissecondes plus long que acces
                .withExpiresAt(new Date(System.currentTimeMillis()+JWTUtil.EXP_REFRESH_TOKEN))
                //nom de l'app qui a gérer le token (on va mettre url de la requete)
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithm);

        Map<String , Object> idToken = new HashMap<>();
        idToken.put("accessToken",jwtAccessToken);
        idToken.put("refreshToken",jwtRefreshToken);
        idToken.put("user",user1);

        response.setHeader(JWTUtil.AUTH_HEADER,jwtAccessToken);

        response.setContentType("application/json");
        //pour serialiser un objet en json avec entrer et sortie
        new ObjectMapper().writeValue(response.getOutputStream(),idToken);
        //renvoie dans un objet json dans le body
       // String jsonResponse = "{\"token\":\"" + jwtAccessToken + "\"}";
        //response.getOutputStream().write(jsonResponse.getBytes());


    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {


        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", "Verfier l'email et le mot de passe !");

        ObjectMapper objectMapper = new ObjectMapper();
        String errorJson = objectMapper.writeValueAsString(errorMap);

        response.getWriter().write(errorJson);
    }
}
