package com.soundcloud.controller;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.LikeDislikeResponseDTO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.DTOs.ResourceRequestDTO;
import com.soundcloud.model.DTOs.Song.SongFilterRequestDTO;
import com.soundcloud.model.DTOs.Song.SongFilterResponseDTO;
import com.soundcloud.model.DTOs.Song.SongGetResponseDTO;
import com.soundcloud.model.DTOs.Song.SongUploadResponseDTO;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class SongController extends AbstractController {
    private final SessionManager sessionManager;
    private final SongService songService;

    @Autowired
    public SongController(SongService songService, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.songService = songService;
    }

    // POST //
    @PostMapping("/songs")
    public SongUploadResponseDTO upload (@RequestPart MultipartFile file, @RequestPart String name, HttpSession session) {
        User loggedUser = this.sessionManager.validateUser(session, "You must login to upload a song.");
        Song uploadedSong = this.songService.uploadSong(name, file, loggedUser);
        return new SongUploadResponseDTO(uploadedSong);
    }

    @PostMapping("songs/filter")
    public List<SongFilterResponseDTO> filterSongs(@RequestBody SongFilterRequestDTO searchRequest) {
        return this.songService.filterSongs(searchRequest);
    }

    // DELETE //
    @DeleteMapping("/songs")
    public MessageDTO delete(@RequestBody ResourceRequestDTO requestJson, HttpSession session) {
        User loggedUser = this.sessionManager.getLoggedUser(session);
        Integer songId = requestJson.getResourceId();

        if(loggedUser == null) {
            throw new AuthenticationException("You must login to delete a song.");
        } else if(songId == null) {
            throw new BadRequestException("You must choose a song to delete.");
        } else if(this.songService.getOwnerForSongId(songId).getId() != loggedUser.getId() ) {
            throw new AuthenticationException("You cannot delete songs uploaded by other users.");
        } else{
            this.songService.deleteSong(songId);
            return new MessageDTO("You have successfully deleted song id#" + songId);
        }
    }

    // GET //
    @GetMapping("/songs/{id}/info")
    public SongGetResponseDTO getById (@PathVariable int id) {
        return new SongGetResponseDTO(this.songService.getById(id));
    }

    @GetMapping(value = "/songs/{id}", produces = "audio/mpeg")
    public byte[] playSong (@PathVariable int id) {
        return this.songService.playSong(id);
    }

    @GetMapping("songs/by-user/{username}")
    public List<SongFilterResponseDTO> getByUsername(@PathVariable String username) {
        return this.songService.getByUsername(username);
    }

    @GetMapping("songs/by-user/{username}/liked")
    public List<SongFilterResponseDTO> getLikedSongs(@PathVariable String username) {
        return this.songService.getLikedSongsByUsername(username);
    }

    // PUT //
    @PutMapping("/songs/{songId}/set-like-status")
    public LikeDislikeResponseDTO setSongLikeStatus(HttpSession session, @RequestParam(name = "value") int likeValue, @PathVariable int songId) {
        User loggedUser = this.sessionManager.validateUser(session, "You must login to like/dislike a song.");
        return this.songService.setLike(songId, likeValue, loggedUser);
    }
}