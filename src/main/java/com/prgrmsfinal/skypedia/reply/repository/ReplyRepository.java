package com.prgrmsfinal.skypedia.reply.repository;

import com.prgrmsfinal.skypedia.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
