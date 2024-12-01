package com.stocksim.simulator.security;

import com.stocksim.simulator.domain.CustomUser;
import com.stocksim.simulator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 로그인페이지에서 유저가 입력한 아이디가 username으로 전달됨
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var result = memberRepository.findByUsername(username);
        // Optional 형태로 리턴되기때문에 아래와 같이 확인하는 코드가 필요함 ?????
        if ((result.isEmpty())){
            throw new UsernameNotFoundException("아이디를 찾을 수 없습니다.");
        }
        var user = result.get();

        // 권한설정 (roles 필드에서 권한을 가져옴) --------- 회원가입 후 테스트 요망 -----------
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String role : user.getRoles().split(",")) {
            authorities.add(new SimpleGrantedAuthority(role));
        }


        //authorities.add(new SimpleGrantedAuthority("일반유저"));

        //return new User(user.getUsername(), user.getPassword(), authorities);

        var a = new CustomUser(user.getUsername(), user.getPassword(), authorities);
        a.displayName = user.getDisplayName();
        a.id = user.getId();
        return a;
    }
}