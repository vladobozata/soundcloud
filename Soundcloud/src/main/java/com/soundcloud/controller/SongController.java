package com.soundcloud.controller;

import com.soundcloud.model.DTOs.Song.SongGetResponseDTO;
import com.soundcloud.model.DTOs.Song.SongUploadResponseDTO;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.SongService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.http.HttpResponse;

@RestController
public class SongController extends AbstractController {
    @Autowired
    private SessionManager sessionManager;
    @Autowired
    private SongService service;

    @PostMapping("/songs")
    @SneakyThrows
    public SongUploadResponseDTO upload (@RequestPart MultipartFile file, @RequestPart String name, HttpSession session) {
        User loggedUser = sessionManager.validateUser(session, "You must login to upload a song.");
        Song uploadedSong = service.uploadSong(name, file, loggedUser);
        return new SongUploadResponseDTO(uploadedSong);
    }

    @GetMapping("/songs/{id}/info")
    @SneakyThrows
    public SongUploadResponseDTO getById (@PathVariable int id) {
        return new SongGetResponseDTO(service.getById(id));
    }


    @GetMapping(value = "/songs/{id}", produces = "audio/mpeg")
    @SneakyThrows
    public byte[] playSong (@PathVariable int id) {
        return service.playSong(id);
    }
}