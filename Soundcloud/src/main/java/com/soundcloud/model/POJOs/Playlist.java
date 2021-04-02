package com.soundcloud.model.POJOs;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @JsonManagedReference
    private User owner;
    private LocalDateTime createdAt;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(
            name = "playlists_have_songs",
            joinColumns = {@JoinColumn(name="playlist_id")},
            inverseJoinColumns = {@JoinColumn(name="song_id")}
    )
    @JsonManagedReference
    private List<Song> songs = new ArrayList<>();

    public Playlist(String name, User owner){
        this.name = name;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
    }
}