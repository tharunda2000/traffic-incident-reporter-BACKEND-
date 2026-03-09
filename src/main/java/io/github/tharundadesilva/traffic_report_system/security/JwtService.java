package io.github.tharundadesilva.traffic_report_system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

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

    public String generateToken(Long userId , String username , String role){
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .claim("uid",userId)
                .claim("role",role)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(expirationSeconds)))
                .signWith(key)
                .compact();

    }


    public String extractUseName(String token){
        return extractClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims =  extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith((SecretKey)key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean  validateToken(String token, UserDetails userDetails){
        final String userName = extractUseName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token){
        return extractClaim(token , Claims::getExpiration);
    }


}
