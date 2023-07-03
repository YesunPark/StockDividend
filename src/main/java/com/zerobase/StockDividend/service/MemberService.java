package com.zerobase.StockDividend.service;

import com.zerobase.StockDividend.model.Auth;
import com.zerobase.StockDividend.model.MemberEntity;
import com.zerobase.StockDividend.persist.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MemberService implements UserDetailsService {
    // 회원가입 관련 클래스는 UserDetailsService 상속받음

    private final PasswordEncoder passwordEncoder;
    // 회원가입 시 비밀번호 암호화
    // 실제 구현체(어떤 빈을 쓸지)는 AppConfig 클래스에서 정의
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("=== 회원 정보를 찾을 수 없습니다 ==="));
    }

    public MemberEntity register(Auth.SignUp member) {
        // 회원가입 기능
        boolean exists = this.memberRepository.existsByUsername(member.getUsername());
        if (exists) {
            throw new RuntimeException("=== 이미 사용 중인 아이디입니다 ===");
        }

        member.setPassword(this.passwordEncoder.encode(member.getPassword()));
        // 비밀번호를 암호화해서 저장

        return this.memberRepository.save(member.toEntity());
    }

    public MemberEntity authenticate(Auth.SignIn member) {
        // 로그인 시 검증하기 위한 메소드
    }
}
