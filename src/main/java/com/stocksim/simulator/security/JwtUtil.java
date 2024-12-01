package com.stocksim.simulator.security;

import com.stocksim.simulator.domain.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final SecretKey key;

    // SecretKey를 생성자에서 생성
    @Autowired
    public JwtUtil(@Value("${jwt.secret.key}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        System.out.println("JwtUtil initialized with SecretKey: " + key);
    }

    // JWT 생성
    public String createToken(Authentication auth) {
        // auth 변수는 타입캐스팅을 해야됨 여기 담긴 유저의 정보를 가져와서 사용
        var user = (CustomUser) auth.getPrincipal();

        // 권한 문자열 생성
        var authorities = auth.getAuthorities().stream()
                .map(a -> a.getAuthority()) // 안에들어가 있는 데이터들을 문자타입으로 변경 tostring사용해도 가능
                .collect(Collectors.joining(","));

        String jwt = Jwts.builder()
                .claim("username",user.getUsername()) // 사용자 ID
                .claim("displayName", user.displayName) // 사용자 표시 이름
                .claim("authorities", authorities) // 권한 정보
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 만료 시간 (1시간)
                .signWith(key) // 서명 키
                .compact(); // JWT 생성
        return jwt;
    }

    // JWT 검증 및 클레임 추출
    public Claims extractToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
                .parseSignedClaims(token).getPayload();
        return claims;
    }
}
