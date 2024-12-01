package com.stocksim.simulator.controller;

import com.stocksim.simulator.security.JwtUtil;
import com.stocksim.simulator.service.MemberService;
import com.stocksim.simulator.entity.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @GetMapping("/login")
    String login(){
        return "login.html";
    }

    @PostMapping("/login/jwt")
    @ResponseBody
    public String loginJWT(@RequestBody Map<String, String> data,
                                      HttpServletResponse response) {

        // 인증 처리 (로그인)
        var authToken = new UsernamePasswordAuthenticationToken(data.get("username"), data.get("password"));
        var auth = authenticationManagerBuilder.getObject().authenticate(authToken);

        // auth 변수를 사용하기 위해 별도로 넣어줘야됨
        SecurityContextHolder.getContext().setAuthentication(auth);

        // JWT 생성
        var jwt = jwtUtil.createToken(SecurityContextHolder.getContext().getAuthentication());

        // JWT를 쿠키에 저장
        var cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(3600); // 1시간
        cookie.setPath("/");
        response.addCookie(cookie);

        // 성공 응답
        return jwt;
    }

    // 회원 가입
    @GetMapping("/register")
    String register(){
        return "register.html";
    }

    // 아이디 중복체크
    @PostMapping("/register/check-username")
    public ResponseEntity checkUsername(@RequestBody Map<String, String> requestBody){

        boolean exists = memberService.checkUsername(requestBody.get("username"));

        if (exists) {
            return ResponseEntity.status(400).build();
        } else {
            return ResponseEntity.ok().build();
        }
    }


    @PostMapping("/register")
    public String registerMember (@ModelAttribute Member member, Model model) {

        try {
            // 회원가입 처리
            memberService.registerMember(member);
            return "redirect:/login";
        } catch (Exception e) {
            // 실패 처리 시 실패 메시지 설정하고 동일 페이지로 리다이렉트
            model.addAttribute("message", "회원가입 실패: " + e.getMessage());
            return "register";  // 실패 시 등록 페이지로 리다이렉트
        }
    }

    @GetMapping("/api/home")
    public String home(){
        // 유저가 제출한 jwt가 이상이 없는지 확인하는 코드 구현 -> filter에 구현
        return "register";
    }
}
