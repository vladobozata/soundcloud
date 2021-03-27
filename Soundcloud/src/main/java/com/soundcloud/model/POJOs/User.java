package com.soundcloud.model.POJOs;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.soundcloud.model.DTOs.RegisterRequestUserDTO;
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
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private int age;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Comment> comments;
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Song> songs;
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Playlist> playlists;

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

//    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
//    @JoinTable(name="users_follow_users",
//            joinColumns={@JoinColumn(name="followed_id")},
//            inverseJoinColumns={@JoinColumn(name="follower_id")})
//    private List<User> followed;
//
//    @ManyToMany(mappedBy = "followers")
//    private List<User> followers;

    public User(RegisterRequestUserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.email = userDTO.getEmail();
        this.age = userDTO.getAge();
        this.createdAt = LocalDateTime.now();
    }
}