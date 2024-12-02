package com.prgrmsfinal.skypedia.reply.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.prgrmsfinal.skypedia.reply.entity.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
}
