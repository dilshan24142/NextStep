package com.securitygateway.nextstep.security;

import com.securitygateway.nextstep.constants.ApplicationConstants;
import com.securitygateway.nextstep.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtHelper {

    public <T> T extractClaims(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    // Extract username (email)
    public String extractUsername(String jwt) {
        return extractClaims(jwt, Claims::getSubject);
    }

    // Generate ACCESS token
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", ((User) userDetails).getRole().name());

        return doGenerateAccessToken(claims, userDetails.getUsername());
    }

    // Generate REFRESH token
    public String generateRefreshToken(UserDetails userDetails) {
        return doGenerateRefreshToken(userDetails.getUsername());
    }

    private String doGenerateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject("#refresh" + username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()
                        + ApplicationConstants.REFRESH_TOKEN_VALIDITY_SECONDS * 1000))
                .setId(UUID.randomUUID().toString())
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String doGenerateAccessToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()
                        + ApplicationConstants.ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .setId(UUID.randomUUID().toString())
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean isTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt);
        return username.equals(userDetails.getUsername()) && !isJwtExpired(jwt);
    }

    public Boolean isRefreshTokenValid(String jwt, UserDetails userDetails) {
        final String username = extractUsername(jwt).substring(8);
        return username.equals(userDetails.getUsername()) && !isJwtExpired(jwt);
    }

    private boolean isJwtExpired(String jwt) {
        return extractExpiration(jwt).before(new Date());
    }

    private Date extractExpiration(String jwt) {
        return extractClaims(jwt, Claims::getExpiration);
    }

    private Claims extractAllClaims(String jwt) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(jwt)
                .getBody();
    }

    // ✅ FIXED SECRET KEY HANDLING (NO BASE64 BUG)
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(
                ApplicationConstants.SECRET_KEY.getBytes(StandardCharsets.UTF_8)
        );
    }
}
