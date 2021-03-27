package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    Playlist getPlaylistById(int id);
}