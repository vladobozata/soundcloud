package com.soundcloud.model.POJOs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(name="comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String text;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonManagedReference
    private User owner;
    @ManyToOne
    @JoinColumn(name = "song_id")
    @JsonBackReference
    private Song song;
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name="users_like_comments",
            joinColumns = {@JoinColumn(name = "comment_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")}
    )
    @JsonManagedReference
    private List<User> likers;

    @ManyToMany
    @JoinTable(
            name="users_dislike_comments",
            joinColumns = {@JoinColumn(name = "comment_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")}
    )
    @JsonManagedReference
    private List<User> dislikers;
}