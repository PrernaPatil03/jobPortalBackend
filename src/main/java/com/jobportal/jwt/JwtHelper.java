package com.jobportal.jwt;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtHelper {
	// Secret key (Use a secure 256-bit key in real apps)
    private final String SECRET_KEY = "myjwtsecretkeymyjwtsecretkeymyjwtsecretkey!";

    // Token validity (e.g., 5 hours)
    private final long JWT_EXPIRATION_MS = 5 * 60 * 60 * 1000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Generate token
    public String generateToken(UserDetails userDetails) {
    	Map<String ,Object> claims =new HashMap<>();
    	CustomUserDetails customUser =(CustomUserDetails)userDetails;
    	claims.put("id", customUser.getId());
    	 claims.put("name", customUser.getName());
    	 claims.put("accountType", customUser.getAccountType());
    	 claims.put("profileId", customUser.getProfileId());
    	 
    	return doGenerateToken(claims,userDetails.getUsername());
    }	
    	
    	private String doGenerateToken(Map<String,Object> claims, String subject) {
        return Jwts.builder()
        		.setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract username
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> Long.valueOf(claims.get("id").toString()));
    }


    // Extract expiration date
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    // Extract any claim
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Boolean isTokenExpired(String token) {
    	final Date expiration =getExpirationDateFromToken(token);
    	return expiration.before(new Date());
    }

    
    // Validate token
    public boolean validateToken(String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }


    // Extract all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
