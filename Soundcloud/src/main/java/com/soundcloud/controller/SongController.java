package com.soundcloud.controller;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.DTOs.Song.SongGetResponseDTO;
import com.soundcloud.model.DTOs.Song.SongUploadResponseDTO;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.SongService;
import com.soundcloud.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class SongController extends AbstractController {
    private final SessionManager sessionManager;
    private final SongService songService;
    private final UserService userService;

    @Autowired
    public SongController(SongService songService, UserService userService, SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.songService = songService;
        this.userService = userService;
    }

    // POST //
    @PostMapping("/songs")
    public SongUploadResponseDTO upload (@RequestPart MultipartFile file, @RequestPart String name, HttpSession session) {
        User loggedUser = sessionManager.validateUser(session, "You must login to upload a song.");
        Song uploadedSong = songService.uploadSong(name, file, loggedUser);
        return new SongUploadResponseDTO(uploadedSong);
    }

    // DELETE //
    @DeleteMapping("/songs/{id}")
    public MessageDTO delete(@PathVariable int id, HttpSession session, HttpServletResponse res) {
        User loggedUser = sessionManager.getLoggedUser(session);
        if (loggedUser == null) {
            throw new AuthenticationException("You must login to delete a song.");
        } else if ( songService.getOwnerForSongId(id).getId() != loggedUser.getId() ) {
            throw new AuthenticationException("You cannot delete songs uploaded by other users.");
        } else {
            songService.deleteSong(id);
            return new MessageDTO(String.format("You have successfully deleted song id#%s", id));
        }
    }

    // GET //
    @GetMapping("/songs/{id}/info")
    public SongGetResponseDTO getById (@PathVariable int id) {
        return new SongGetResponseDTO(songService.getById(id));
    }

    @GetMapping(value = "/songs/{id}", produces = "audio/mpeg")
    public byte[] playSong (@PathVariable int id) {
        return songService.playSong(id);
    }

    @GetMapping("songs/{username}")
    public List<SongGetResponseDTO> getByUsername(@PathVariable String username) {
        return songService.getByUsername(username);
    }

    @GetMapping("songs/liked")
    public List<SongGetResponseDTO> getLikedSongs(HttpSession session) {
        User loggedUser = sessionManager.validateUser(session, "You must login to see your liked songs.");
        return songService.getLikedByUser(loggedUser);
    }

    // PUT //
    @PutMapping("/songs/{id}/set-like-status")
    public MessageDTO setLikeStatus(HttpSession session, @RequestParam(name = "value") int likeValue, @PathVariable(name = "id") int songId) {
        User loggedUser = sessionManager.validateUser(session, "You must login to like/dislike a song.");
        return songService.setLike(songId, likeValue, loggedUser);
    }

}