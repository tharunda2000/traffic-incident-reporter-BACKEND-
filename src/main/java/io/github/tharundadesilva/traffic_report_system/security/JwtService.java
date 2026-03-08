package io.github.tharundadesilva.traffic_report_system.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long expirationSeconds;

    public JwtService(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expirationSeconds
    ) {
        byte[] secretBytes;
        try{
            secretBytes = Decoders.BASE64.decode(secret);

        }catch(IllegalArgumentException ex){
            secretBytes = secret.getBytes();
        }

        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(Long userId , String email){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim("uid",userId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();

    }
}
