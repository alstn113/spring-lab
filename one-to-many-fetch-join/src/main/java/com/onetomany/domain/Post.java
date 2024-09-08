package com.onetomany.domain;

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
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    public Post(String title) {
        this(null, title, new ArrayList<>());
    }

    public Post(Long id, String title, List<Comment> comments) {
        this.id = id;
        this.title = title;
        this.comments = comments;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.updatePost(this);
    }
}
