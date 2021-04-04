package com.soundcloud.service.email;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.POJOs.VerificationToken;
import com.soundcloud.model.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
public class TokenService {
    private final VerificationTokenRepository tokenRepository;

    @Autowired
    public TokenService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public void confirmToken(String userToken, User user) {
        VerificationToken token = this.tokenRepository.findByToken(userToken);
        if(token == null){
            throw new BadRequestException("Confirmation details are wrong!");
        }
        if(token.getUser().isEnabled()){
            throw new BadRequestException("Your email has already been confirmed!");
        }
        if(user != null){
            throw new BadRequestException("You do not have a permission to validate foreign email!");
        }
        if(token.getExpiresAt().compareTo(LocalDateTime.now()) < 0){
            throw new NotFoundException("Verification token expired...");
        }
        token.getUser().setEnabled(true);
        token.setConfirmedAt(LocalDateTime.now());
    }
}