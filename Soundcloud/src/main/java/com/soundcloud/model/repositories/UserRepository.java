package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findUserByEmail(String email);

    List<User> findUserByUsernameContains(String username);

    User findUserByUsername(String username);

    User findUserById(int id);

    void deleteUserById(int id);
}