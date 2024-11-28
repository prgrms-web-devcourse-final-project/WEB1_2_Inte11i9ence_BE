package com.prgrmsfinal.skypedia.post.entity.key;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PostLikesId implements Serializable {
    private Long postId;

    private Long memberId;
}
