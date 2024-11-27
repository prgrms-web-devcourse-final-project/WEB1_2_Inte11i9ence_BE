package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByOauthId(String oauthId);

    List<Member> findByWithdrawnTrueAndWithdrawnAtBefore(LocalDateTime dateTime);
}
