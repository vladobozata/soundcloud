package com.soundcloud.email;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.MessageDTO;
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

    public VerificationToken findByToken(String token) {
        return this.tokenRepository.findByToken(token);
    }

    public void save(VerificationToken verificationToken) {
        this.tokenRepository.save(verificationToken);
    }

    @Transactional
    public MessageDTO confirmToken(String userToken, User user) {
        VerificationToken token = findByToken(userToken);
        if(token == null){
            throw new BadRequestException("Confirmation details are wrong!.");
        }
        if(token.getUser().isEnabled()){
            throw new BadRequestException("You already confirmed your email!");
        }
        token.getUser().setEnabled(true);
        token.setConfirmedAt(LocalDateTime.now());
        return new MessageDTO("Email confirmed!");
    }
}