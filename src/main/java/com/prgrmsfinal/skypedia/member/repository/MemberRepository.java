package com.prgrmsfinal.skypedia.member.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.prgrmsfinal.skypedia.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findByOauthId(String oauthId);

	Optional<Member> findByUsername(String username);

	List<Member> findByWithdrawnTrueAndWithdrawnAtBefore(LocalDateTime dateTime);

	@Query(value = "SELECT EXISTS (SELECT 1 FROM member WHERE username = :username)", nativeQuery = true)
	boolean existsByUsername(@Param("username") String username);

  	Member findByEmail(String email);
}
