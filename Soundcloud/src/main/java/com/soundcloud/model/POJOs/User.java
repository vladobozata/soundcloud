package com.soundcloud.model.POJOs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.soundcloud.model.DTOs.User.RegisterRequestUserDTO;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private int age;
    private String verification;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Song> songs = new ArrayList<>();
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Playlist> playlists = new ArrayList<>();

    @ManyToMany(mappedBy = "likers")
    @JsonBackReference
    private List<Comment> likedComments;

    @ManyToMany(mappedBy = "dislikers")
    @JsonBackReference
    private List<Comment> dislikedComments;

    @ManyToMany(mappedBy = "likers")
    @JsonBackReference
    private List<Song> likedSongs;

    @ManyToMany(mappedBy = "dislikers")
    @JsonBackReference
    private List<Song> dislikedSongs;

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @JoinTable(name="users_follow_users",
            joinColumns={@JoinColumn(name="followed_id")},
            inverseJoinColumns={@JoinColumn(name="follower_id")})
    @JsonManagedReference
    private List<User> followers = new ArrayList<>();

    @ManyToMany(mappedBy = "followers")
    @JsonBackReference
    private List<User> followed;

    public User(RegisterRequestUserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.email = userDTO.getEmail();
        this.age = userDTO.getAge();
        this.createdAt = LocalDateTime.now();
    }
}