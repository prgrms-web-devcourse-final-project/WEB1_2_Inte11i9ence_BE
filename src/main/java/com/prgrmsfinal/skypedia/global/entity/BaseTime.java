package com.prgrmsfinal.skypedia.global.entity;

import java.time.LocalDateTime;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseTime {
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
}
