package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {
    
}