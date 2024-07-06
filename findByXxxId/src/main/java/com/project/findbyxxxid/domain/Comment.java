package com.project.findbyxxxid.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Post post;

    protected Comment() {
    }

    public Comment(Post post) {
        this(null, post);
    }

    public Comment(Long id, Post post) {
        this.id = id;
        this.post = post;
    }

    public Long getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }
}
