package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByOauthId(String oauthId);
    Optional<Member> findByUsername(String username);
    List<Member> findByWithdrawnTrueAndWithdrawnAtBefore(LocalDateTime dateTime);
    Member findByEmail(String email);
}
