package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);
    User findUserByUsername(String username);
    User findUserById(int id);
    void deleteById(int id);
}