package com.soundcloud.model.POJOs;

import com.soundcloud.model.DTOs.RegisterRequestUserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.beans.factory.annotation.Autowired;

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
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "comments")
    private List<Comment> comments;
    @OneToMany(mappedBy = "songs")
    private List<Song> songs;

    @ManyToMany
    @JoinTable(name="users_have_followers",
    joinColumns = {@JoinColumn(name = "followed_id")},
    inverseJoinColumns = {@JoinColumn(name = "follower_id")})
    private List<User> followed = new ArrayList<>();

    @ManyToMany(mappedBy = "followed")
    private List<User> followers = new ArrayList<>();

    public User(RegisterRequestUserDTO userDTO) {
        this.username = userDTO.getUsername();
        this.password = userDTO.getPassword();
        this.email = userDTO.getEmail();
        this.age = userDTO.getAge();
        this.createdAt = LocalDateTime.now();
    }
}