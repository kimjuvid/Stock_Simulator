package com.stocksim.simulator.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

// MyUserDetailService 클래스에서 사용됨
// 오브젝트를 커스터마이징하기 위해서 User클래스를 상속받아서 추가 구현
public class CustomUser extends User {

    public String displayName;
    public Long id;
    public CustomUser(
            String username,
            String password,
            Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }
}