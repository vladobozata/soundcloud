package com.soundcloud.model.repositories;

import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.POJOs.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);
    VerificationToken findByUserId(int userId);
}