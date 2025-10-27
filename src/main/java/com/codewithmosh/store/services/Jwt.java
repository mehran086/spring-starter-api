package com.codewithmosh.store.services;

import com.codewithmosh.store.entities.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;

public class Jwt {
    private final Claims claims;
    private final SecretKey key;

    public Jwt(Claims claims, SecretKey key) {
        this.claims = claims;
        this.key = key;
    }

    public boolean isValid()
    {

            return claims.getExpiration().after(new Date());

    }
    public boolean isExpired()
    {
        return claims.getExpiration().before(new Date());
//        return ;
    }
    public Long getUserId(){
        return Long.valueOf(claims.getSubject());
    }
    public Role getRole(){

        return  Role.valueOf( claims.get("role",String.class));
    }
    public Long getExpiration(){
            return claims.getExpiration().getTime();
    }
    @Override
    public String toString() {
        return Jwts.builder().claims(claims).signWith(key).compact();
    }
}
