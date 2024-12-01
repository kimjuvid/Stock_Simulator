package com.stocksim.simulator.service;

import com.stocksim.simulator.entity.Member;
import com.stocksim.simulator.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    public void registerMember(Member request){

        Member member = new Member();
        member.setUsername(request.getUsername());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setDisplayName(request.getDisplayName());
        member.setRoles("ROLE_USER");

        memberRepository.save(member);
    }
    public boolean checkUsername(String username){
        boolean exists = memberRepository.existsByUsername(username);
        return exists;
    }
}
