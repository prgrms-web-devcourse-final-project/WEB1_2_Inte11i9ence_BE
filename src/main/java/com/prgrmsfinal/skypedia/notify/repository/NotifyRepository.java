package com.prgrmsfinal.skypedia.notify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prgrmsfinal.skypedia.notify.entity.Notify;

@Repository
public interface NotifyRepository extends JpaRepository<Notify, Long> {
	@Query("SELECT n FROM Notify n WHERE n.member.id = :memberId AND n.viewed = :read ORDER BY n.id DESC")
	List<Notify> findByMemberIdAndViewed(@Param("memberId") Long memberId, @Param("read") boolean viewed);

	@Query("SELECT n FROM Notify n WHERE n.id = :notifyId AND n.member.id = :memberId AND n.viewed = :viewed")
	Optional<Notify> findByNotifyIdAndMemberId(@Param("notifyId") Long notifyId, @Param("memberId") Long memberId,
		@Param("viewed") boolean viewed);
}
