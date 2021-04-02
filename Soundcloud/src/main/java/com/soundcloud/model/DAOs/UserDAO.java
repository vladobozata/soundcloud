package com.soundcloud.model.DAOs;

import com.soundcloud.model.DTOs.User.FilterRequestUserDTO;
import com.soundcloud.model.DTOs.User.FilterResponseUserWithoutPlaylistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<FilterResponseUserWithoutPlaylistDTO> getFilteredUsers(FilterRequestUserDTO filterUserDTO) throws SQLException {
        List<FilterResponseUserWithoutPlaylistDTO> filteredUsers = new ArrayList<>();
        String filterQuery = "SELECT u.id, u.username, " +
                "COUNT(ufu.followed_id) AS followers, " +
                "COUNT(c.owner_id) AS comments, " +
                "COUNT(s.owner_id) AS songs " +
                "FROM users u Left JOIN users_follow_users ufu " +
                "ON u.id = ufu.followed_id " +
                "LEFT JOIN comments c " +
                "ON u.id = c.owner_id " +
                "LEFT JOIN songs s " +
                "ON u.id = s.owner_id " +
                "GROUP BY u.id " +
                "ORDER BY " + filterUserDTO.getSortBy() + " " + filterUserDTO.getOrderBy() + " " +
                "LIMIT " + filterUserDTO.getItemsPerPage() + " OFFSET " + (filterUserDTO.getItemsPerPage() * (filterUserDTO.getPage() - 1)); // starts from 0
        // take 4 rows per query depends on page given by user
        // e.g. page 4 -> LIMIT 4 OFFSET 4 * (4-1) -> rows 12, 13, 14, 15
        // page 1 - 0,1,2,3; page 2 - 4,5,6,7; page 3 - 8,9,10,11; ....

        DataSource dataSource = this.jdbcTemplate.getDataSource();
        if (dataSource != null) {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement pr = connection.prepareStatement(filterQuery))
            {
                ResultSet set = pr.executeQuery();
                while (set.next()) {
                    FilterResponseUserWithoutPlaylistDTO filteredUser = new FilterResponseUserWithoutPlaylistDTO(
                            set.getInt("id"),
                            set.getString("username"),
                            set.getInt("songs"),
                            set.getInt("comments"),
                            set.getInt("followers"));
                    filteredUsers.add(filteredUser);
                }
            }
            return filteredUsers;
        }
        throw new SQLException("Connection to DB failed!");
    }
}