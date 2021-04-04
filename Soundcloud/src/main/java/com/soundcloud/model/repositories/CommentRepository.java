package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Comment findCommentById(int id);

    List<Comment> findCommentsBySong(Song song);
}