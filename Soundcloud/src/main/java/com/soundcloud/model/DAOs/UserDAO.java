package com.soundcloud.model.DAOs;

import com.soundcloud.model.DTOs.*;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@Component
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository repository;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate, UserRepository repository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
    }

    public FollowResponseUserDTO followUser(FollowRequestUserDTO followDTO, User loggedUser) throws SQLException {
        String followQuery = "INSERT INTO users_follow_users(followed_id, follower_id) " +
                "VALUES(?,?)";

        PreparedStatement pr = this.jdbcTemplate.getDataSource().getConnection().prepareStatement(followQuery);
        pr.setInt(1, followDTO.getID());
        pr.setInt(2, loggedUser.getId());
        pr.executeUpdate();
        pr.close();

        return new FollowResponseUserDTO("You successfully followed " + this.repository.findById(followDTO.getID()).getUsername());
    }

    public FollowResponseUserDTO unfollowUser(FollowRequestUserDTO unfollowDTO, User loggedUser) throws SQLException {
        String unfollowQuery = "DELETE FROM users_follow_users WHERE followed_id = ? AND follower_id = ?";
        PreparedStatement pr = this.jdbcTemplate.getDataSource().getConnection().prepareStatement(unfollowQuery);
        pr.setInt(1, unfollowDTO.getID());
        pr.setInt(2, loggedUser.getId());
        pr.executeUpdate();
        pr.close();
        return new FollowResponseUserDTO("You successfully unfollowed " + this.repository.findById(unfollowDTO.getID()).getUsername());
    }
}