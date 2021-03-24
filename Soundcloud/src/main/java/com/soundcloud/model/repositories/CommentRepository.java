package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.Comment;
import com.soundcloud.model.POJOs.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    ArrayList<Comment> findByUserOrderByCreatedAtAsc(User user);

}