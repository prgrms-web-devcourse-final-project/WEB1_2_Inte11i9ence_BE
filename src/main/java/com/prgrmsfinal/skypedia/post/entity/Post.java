package com.prgrmsfinal.skypedia.post.entity;

import com.prgrmsfinal.skypedia.global.entity.BaseTime;
import com.prgrmsfinal.skypedia.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(indexes = {
        @Index(name = "idx__views", columnList = "views"),
        @Index(name = "idx__likes", columnList = "likes")
})
public class Post extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PostCategory category;

    private String title;

    private String content;

    @Builder.Default
    private Long views = 0L;

    @Builder.Default
    private Long likes = 0L;

    private Float rating;

    @Builder.Default
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    public void modify(String title, String content) {
        this.title = title;

        this.content = content;
    }

    public void delete() {
        this.deleted = true;

        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;

        this.deletedAt = null;
    }
}
