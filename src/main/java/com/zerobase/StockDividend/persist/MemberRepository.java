package com.zerobase.StockDividend.persist;

import com.zerobase.StockDividend.model.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUsername(String username);
    //  id 를 기준으로 회원정보 조회

    boolean existsByUsername(String username);
    // 회원가입 시 이미 존재하는 id 인지 확인
}
