package com.codewithmosh.store.services;

import com.codewithmosh.store.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Value("${spring.jwt.secret}")
    private String secret;

        public String generateToken(User user){

            // if you want to use email and id as claim
            final long tokenExpiration = 86400; // tokens valid for 1day.
          return  Jwts.builder()
                    .subject(user.getId().toString()).
                    claim("email",user.getEmail()).
                    claim("name",user.getName()).
                    issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 1000*tokenExpiration))
                    .signWith(Keys.hmacShaKeyFor(secret.getBytes())).compact();
        }

        public boolean validateToken(String token){
         try {
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
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build()
                .parseSignedClaims(token).getPayload();
        return claims;
    }

    public String getEmailFromToken(String token){
         return  getClaims(token).getSubject();
        }

    public Long getUserIdFromToken(String token){
        return Long.valueOf(getClaims(token).getSubject());
    }
}
