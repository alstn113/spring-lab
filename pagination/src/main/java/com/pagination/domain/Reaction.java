package com.pagination.domain;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Reaction {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @OneToMany(mappedBy = "reaction")
    private List<Log> logs;

    public Reaction(String action, Comment comment) {
        this(null, action, comment, new ArrayList<>());
    }

    public Reaction(Long id, String action, Comment comment, List<Log> logs) {
        this.id = id;
        this.action = action;
        this.comment = comment;
        this.logs = logs;
    }
}
