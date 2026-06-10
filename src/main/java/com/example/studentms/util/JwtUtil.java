package com.example.studentms.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    private SecretKey secretKey;

    @PostConstruct
    public void init(){
        this.secretKey=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Integer userId,String username){
        Date now=new Date();
        Date expiration=new Date(now.getTime()+expire);

        return Jwts.builder().
        subject(username).
        claim("userId",userId).
        claim("username",username).
        issuedAt(now).
        expiration(expiration).
        signWith(secretKey).compact();
    }

    public Claims parseToken(String token){
        return Jwts.parser().
        verifyWith(secretKey).
        build().
        parseSignedClaims(token).getPayload();
    }
}
