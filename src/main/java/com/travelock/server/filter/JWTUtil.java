//18.
package com.travelock.server.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private Key key;


    //yml파일에 등록된 임시키의 길이가 짧아서 에러발생. yml파일경로 수정함.
    public JWTUtil(@Value("${application.security.jwt.secret-key}")String secret) {
        byte[] byteSecretKey = Decoders.BASE64.decode(secret);
        key = Keys.hmacShaKeyFor(byteSecretKey);
    }

    public String getName(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("username", String.class);
    }

    public String getProvider(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("provider", String.class);
    }

    public String getRole(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("role", String.class);
    }

//    public Long getMemberId(String token) {
//        return  Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().get("memberId", Long.class);
//    }

    public Long getMemberId(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        System.out.println("Claims 내용: " + claims); // 로그로 확인
        return claims.get("memberId", Long.class); // memberId를 잘 가져오는지 확인
    }

    public Boolean isExpired(String token) {

        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }

    public String createJwt(String username, String provider, String role, Long expiredMs, Long memberId) {

        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("provider", provider);
        claims.put("role", role);
        claims.put("memberId", memberId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String username, String provider, Long expiredMs, Long memberId) {

        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("provider", provider);

        if (memberId == null) {
            throw new IllegalArgumentException("MemberId cannot be null when creating JWT");
        }
        claims.put("memberId", memberId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs)) // 리프레시 토큰은 만료 시간이 더 길게 설정됩니다.
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    } // 리프레쉬 토큰 생성

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    } // 리프레쉬 토큰 검증




}
