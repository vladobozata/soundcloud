package com.soundcloud.model.DAOs;

import com.soundcloud.model.DTOs.Song.SongFilterResponseDTO;
import jdk.jfr.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Component
public class SongDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SongDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<SongFilterResponseDTO> filterSongs(String queryTitle, String sort, String order, int page, int resultsPerPage) throws SQLException {
        List<SongFilterResponseDTO> songs = new LinkedList<>();

        String sql = "SELECT sng.title, " +
                     "usr.username AS uploadedBy, " +
                     "sng.id AS songId, " +
                     "sng.views, " +
                     "COUNT(DISTINCT cmt.id) AS comments, " +
                     "COUNT(DISTINCT uls.user_id) AS likes, " +
                     "COUNT(DISTINCT uds.user_id) AS dislikes, " +
                     "COUNT(DISTINCT phs.playlist_id) AS inPlaylists, " +
                     "sng.created_at AS date " +

                     "FROM songs sng " +
                     "LEFT JOIN users usr ON sng.owner_id = usr.id " +
                     "LEFT JOIN comments cmt ON sng.id = cmt.song_id " +
                     "LEFT JOIN users_like_songs uls ON sng.id = uls.song_id " +
                     "LEFT JOIN users_dislike_songs uds ON sng.id = uds.song_id " +
                     "LEFT JOIN playlists_have_songs phs ON sng.id = phs.song_id " +

                     "WHERE sng.title LIKE \"%%%s%%\" " +

                     "GROUP BY sng.id " +

                     "ORDER BY %s %s " +

                     "LIMIT %d OFFSET %d";

        sql = String.format(sql, queryTitle, sort, order, resultsPerPage, (resultsPerPage * (page - 1)));
        PreparedStatement statement = jdbcTemplate.getDataSource().getConnection().prepareStatement(sql);
        ResultSet results = statement.executeQuery();

        while(results.next()) {
            String title = results.getString("title");
            String uploadedBy = results.getString("uploadedBy");
            LocalDateTime uploadDate = results.getTimestamp("date").toLocalDateTime();
            int songId = results.getInt("songId");
            int views = results.getInt("views");
            int comments = results.getInt("comments");
            int likes = results.getInt("likes");
            int dislikes = results.getInt("dislikes");
            int inPlaylists = results.getInt("inPlaylists");
            songs.add(new SongFilterResponseDTO(title, uploadedBy, uploadDate, songId, views, comments, likes, dislikes, inPlaylists));
        }

        return songs;
    }
}
