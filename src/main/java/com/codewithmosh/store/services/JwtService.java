package com.codewithmosh.store.services;

import com.codewithmosh.store.config.JwtConfig;
import com.codewithmosh.store.entities.Role;
import com.codewithmosh.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Autowired
    private TokenBlackListService tokenBlackListService;

//    @Value("${spring.jwt.secret}")
//    private String secret;
    private JwtConfig jwtConfig;

    public JwtService(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public Jwt generateAccessToken(User user){

            // if you want to use email and id as claim
//            final long tokenExpiration = 300; // tokens valid for 5min.
            final long tokenExpiration = jwtConfig.getAccessTokenExpiration();

            return generateToken(user, tokenExpiration);
        }
    public Jwt generateRefreshToken(User user){

        // if you want to use email and id as claim
        final long tokenExpiration = jwtConfig.getRefreshTokenExpiration(); // tokens valid for 7days.
        return generateToken(user, tokenExpiration);
    }

    /**
     * setting user id as subject.
     * @param user
     * @param tokenExpiration
     * @return
     */
    private Jwt generateToken(User user, long tokenExpiration) {

        var claims= Jwts.claims().subject(user.getId().toString())
                .add("email",user.getEmail())
                .add("name",user.getName())
                .add("role",user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * tokenExpiration))
                .build();

        return new Jwt(claims, jwtConfig.getSecretKey());
    }

    public boolean validateToken(String token)
    {
        if(tokenBlackListService.isBlacklisted(token)) return false;
         try
         {
             var claims = getClaims(token);
             return claims.getExpiration().after(new Date());

         }
         catch (JwtException e){
             return false;
         }
    }

    private Claims getClaims(String token) {
        var claims = Jwts
                .parser()
                .verifyWith(jwtConfig.getSecretKey()).build()
                .parseSignedClaims(token).getPayload();
        return claims;
    }

//    public String getEmailFromToken(String token){
//         return  getClaims(token).getSubject();
//        }

//    public Long getUserIdFromToken(String token){
//        return Long.valueOf(getClaims(token).getSubject());
//    }

//    public Role getRoleFromToken(String token){
//        return  Role.valueOf( getClaims(token).get("role",String.class));
//    }
    public Jwt parseToken(String token){
        try{
            var claims = getClaims(token);
            return new Jwt(claims, jwtConfig.getSecretKey());
        } catch (Exception e) {

            return null;
        }
    }

}
