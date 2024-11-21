package com.prgrmsfinal.skypedia.member.repository;

import com.prgrmsfinal.skypedia.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByOauthId(String oauthId);
}
