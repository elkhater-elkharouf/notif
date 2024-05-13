package com.example.userservice.Security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class JWTUtil {
    public  static final String SECRET="mySecret1234";
    public  static final String AUTH_HEADER="Authorization";
    public  static final String PREFIX_HEADER="Bearer ";
    public static final long EXP_ACCESS_TOKEN = 150L * 60 * 1000;
    public static final long EXP_REFRESH_TOKEN = 15L * 60 * 1000;



    public static boolean hastokenExpired2(String token) {
        DecodedJWT jwt = JWT.decode(token);
        if( jwt.getExpiresAt().before(new Date())) {
            return true;
        }
        return false;
    }
}
