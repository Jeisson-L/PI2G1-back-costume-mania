package com.costumemania.msusers.configuration.security.jwt;

import com.costumemania.msusers.model.entity.UserEntity;
import com.costumemania.msusers.repository.IUserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtils {

    private String secretKey = "2a12dQNObNyF/E4NC8ZM/ktoVU31KSpZNMhTNHDEjvh/2z7zrfkoN6";
    private String timeExpiration = "86480000";

    @Autowired
    private IUserRepository userRepository;


    public String generateAccessToken(String username) {

        UserEntity foundUser = userRepository.findOneByUsername(username).get();

        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("role", foundUser.getRole().name());
        customClaims.put("sub", username);
        customClaims.put("iat", new Date(System.currentTimeMillis()));
        customClaims.put("exp", new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)));

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                .setClaims(customClaims)
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {

            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public Key getSignatureKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction) {

        Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public String getUsernameFromToken(String token) {

        return getClaim(token, Claims::getSubject);
    }


    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
