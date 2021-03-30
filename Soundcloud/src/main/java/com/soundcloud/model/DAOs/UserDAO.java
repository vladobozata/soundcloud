package com.soundcloud.model.DAOs;

import com.soundcloud.model.DTOs.User.FilterRequestUserDTO;
import com.soundcloud.model.DTOs.User.FilterResponseUserDTO;
import com.soundcloud.model.DTOs.User.FollowRequestUserDTO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;
    private final UserRepository repository;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate, UserRepository repository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
    }

    public MessageDTO followUser(FollowRequestUserDTO followDTO, User loggedUser) throws SQLException {
        String followQuery = "INSERT INTO users_follow_users(followed_id, follower_id) " +
                "VALUES(?,?)";

        PreparedStatement pr = this.jdbcTemplate.getDataSource().getConnection().prepareStatement(followQuery);
        pr.setInt(1, followDTO.getUserID());
        pr.setInt(2, loggedUser.getId());
        pr.executeUpdate();
        pr.close();

        return new MessageDTO("You successfully followed " + this.repository.findUserById(followDTO.getUserID()).getUsername());
    }

    public MessageDTO unfollowUser(FollowRequestUserDTO unfollowDTO, User loggedUser) throws SQLException {
        String unfollowQuery = "DELETE FROM users_follow_users WHERE followed_id = ? AND follower_id = ?";
        PreparedStatement pr = this.jdbcTemplate.getDataSource().getConnection().prepareStatement(unfollowQuery);
        pr.setInt(1, unfollowDTO.getUserID());
        pr.setInt(2, loggedUser.getId());
        pr.executeUpdate();
        pr.close();
        return new MessageDTO("You successfully unfollowed " + this.repository.findUserById(unfollowDTO.getUserID()).getUsername());
    }

    public List<FilterResponseUserDTO> getFilteredUsers(FilterRequestUserDTO filterUserDTO) throws SQLException {
        List<FilterResponseUserDTO> filteredUsers = new ArrayList<>();
        String filterQuery = "SELECT u.id AS id, u.username AS username,\n" +
                "COALESCE(ufu.followers, 0) AS followers,\n" +
                "COALESCE(s.songs, 0) AS songs,\n" +
                "COALESCE(c.comments, 0) AS comments,\n" +
                "COALESCE(p.playlists, 0) AS playlists\n" +
                "FROM users u\n" +
                "LEFT JOIN\n" +
                "(SELECT followed_id, COUNT(followed_id) AS followers\n" +
                "FROM users_follow_users\n" +
                "GROUP BY followed_id\n" +
                ") AS ufu\n" +
                "ON u.id = ufu.followed_id\n" +
                "LEFT JOIN\n" +
                "(SELECT owner_id, COUNT(owner_id) AS songs\n" +
                "FROM songs\n" +
                "GROUP BY owner_id\n" +
                ") AS s\n" +
                "ON u.id = s.owner_id\n" +
                "LEFT JOIN\n" +
                "(SELECT owner_id, COUNT(owner_id) AS comments\n" +
                "FROM comments\n" +
                "GROUP BY owner_id\n" +
                ") AS c\n" +
                "ON u.id = c.owner_id\n" +
                "LEFT JOIN\n" +
                "(SELECT owner_id, COUNT(owner_id) AS playlists\n" +
                "FROM playlists\n" +
                "GROUP BY owner_id\n" +
                ") AS p\n" +
                "ON u.id = p.owner_id\n" +
                "ORDER BY " + filterUserDTO.getSortBy() + " " + filterUserDTO.getOrderBy();
        System.out.println(filterQuery);
        PreparedStatement pr = this.jdbcTemplate.getDataSource().getConnection().prepareStatement(filterQuery);
        ResultSet set = pr.executeQuery();
        while (set.next()){
            FilterResponseUserDTO filteredUser = new FilterResponseUserDTO(
                    set.getInt("id"),
                    set.getString("username"),
                    set.getInt("songs"),
                    set.getInt("comments"),
                    set.getInt("playlists"),
                    set.getInt("followers"));
            filteredUsers.add(filteredUser);
        }
        pr.close();
        return filteredUsers;
    }
}