package com.cartesian.domain.v1;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class PostV1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "post")
    private List<CommentV1> comments;

    public PostV1(String title) {
        this(null, title, new ArrayList<>());
    }

    public PostV1(Long id, String title, List<CommentV1> comments) {
        this.id = id;
        this.title = title;
        this.comments = comments;
    }

    public void addComment(CommentV1 comment) {
        comments.add(comment);
        comment.alignPost(this);
    }
}
