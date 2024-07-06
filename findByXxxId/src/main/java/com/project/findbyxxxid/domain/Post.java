package com.project.findbyxxxid.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Post() {
    }

    public Post(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
