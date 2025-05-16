package com.cineproj.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class TokenService {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 86400000;

    public static String generateToken(String userId, boolean isAdmin, boolean isCinema) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("admin", isAdmin)
                .claim("cinema", isCinema)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public static boolean verifyTokenIsAdmin(String token, boolean mustBeAdmin) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        boolean isAdmin = claims.get("admin", Boolean.class);
        
        if (mustBeAdmin && !isAdmin) {
            return false;
        }
        
        return true;
    }
    
    public static boolean verifyTokenIsCinema(String token, boolean mustBeCinema) {
    	Claims claims = Jwts.parserBuilder()
    			.setSigningKey(key)
    			.build()
    			.parseClaimsJws(token)
    			.getBody();
    	
    	boolean isCinema = claims.get("cinema", Boolean.class);
    	
    	if (mustBeCinema && !isCinema) {
    		return false;
    	}
    	
    	return true;
    }

    public static String getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        return claims.getSubject();
    }
}