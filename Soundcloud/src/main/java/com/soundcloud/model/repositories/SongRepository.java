package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    Song getSongById(int id);

    Song getSongByTitle(String title);

    List<Song> getAllByOwner(User owner);
}