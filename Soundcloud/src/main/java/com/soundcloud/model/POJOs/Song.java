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
@Table(name = "songs")
public class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String url;
    private int views;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private User owner;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "song")
    @JsonManagedReference
    private List<Comment> comments;
    @ManyToMany(mappedBy = "songs")
    @JsonBackReference
    private List<Playlist> playlists;

    @ManyToMany
    @JoinTable(
            name = "users_like_songs",
            joinColumns = {@JoinColumn(name="song_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")}
    )
    @JsonManagedReference
    List<User> likers;

    @ManyToMany
    @JoinTable(
            name = "users_dislike_songs",
            joinColumns = {@JoinColumn(name="song_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")}
    )
    @JsonManagedReference
    List<User> dislikers;

    public Song(String title, String url, User owner) {
        this.title = title;
        this.url = url;
        this.views = 0;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
    }
}