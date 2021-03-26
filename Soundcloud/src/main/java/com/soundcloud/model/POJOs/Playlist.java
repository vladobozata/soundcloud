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
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    @ManyToOne
    @JoinColumn(name="owner_id")
    @JsonBackReference
    private User owner;
    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "playlists_have_songs",
            joinColumns = {@JoinColumn(name="playlist_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")}
    )
    @JsonManagedReference
    private List<Song> songs;

    public Playlist(String name, User owner){
        this.name = name;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
    }
}