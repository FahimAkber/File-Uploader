package com.example.fileuploader.util;

import com.example.fileuploader.model.UserRole;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import netscape.security.ForbiddenTargetException;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Component
public class JwtUtil {
    @Value("${spring.jwt.secret}")
    private String secret;
    @Value("${spring.jwt.expiration}")
    private String expirationTime;
    public static final String CLAIM_KEY = "role";

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public UUID getAccountIdFromToken(String token) {
        return UUID.fromString(getAllClaimsFromToken(token).getSubject());
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(Long id, String email, UserRole role) {
        if (Objects.nonNull(id) && Objects.nonNull((email)) && Objects.nonNull(role)) {
            return doGenerateToken(id, email, role);
        } else {
            throw new ForbiddenTargetException("Account ID Should not be NULL");
        }
    }

    private String doGenerateToken(Long id, String email, UserRole role) {
        long expirationTimeLong = Long.parseLong(expirationTime); //in second
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
        return Jwts.builder()
                .setSubject("UserInfo")
                .claim(CLAIM_KEY, Collections.emptyList())
                .claim("userId", id)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}
