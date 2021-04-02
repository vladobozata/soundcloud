package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);

    User findUserByUsername(String username);

    User findUserById(int id);

//    Page<User> getDistinctByOrderByCommentsAsc(Pageable pageable);
//    Page<User> getDistinctByOrderByFollowersAsc(Pageable pageable);
//    Page<User> getDistinctByOrderBySongsAsc(Pageable pageable);
    Page<User> getDistinctByOrderByPlaylistsAsc(Pageable pageable);

    void deleteUserById(int id);
}