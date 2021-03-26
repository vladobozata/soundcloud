package com.soundcloud.controller;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.AddPlaylistDTO;
import com.soundcloud.model.DTOs.PlaylistResponseDTO;
import com.soundcloud.model.DTOs.SongToPlaylistDTO;
import com.soundcloud.model.DTOs.UserMessageDTO;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
public class PlaylistController extends AbstractController{
    private final PlaylistService playlistService;
    private final SessionManager sessionManager;

    @Autowired
    public PlaylistController(PlaylistService userService, SessionManager sessionManager) {
        this.playlistService = userService;
        this.sessionManager = sessionManager;
    }

    @PostMapping("/playlists")
    public PlaylistResponseDTO addPlaylist(@RequestBody AddPlaylistDTO playlistDTO, HttpSession session){
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if(loggedUser == null){
            throw new BadRequestException("You have to login and then add a playlist!");
        }
        return this.playlistService.addPlaylist(playlistDTO.getName(), loggedUser);
    }

    @DeleteMapping("/playlists/{playlistID}")
    public UserMessageDTO removePlaylist(@PathVariable int playlistID, HttpSession session){
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if(loggedUser == null){
            throw new BadRequestException("You have to login and then remove a playlist!");
        }
        return this.playlistService.removePlaylist(playlistID, loggedUser);
    }

    @DeleteMapping("/playlists/songs")
    public UserMessageDTO removeSongFromPlaylist(@RequestBody SongToPlaylistDTO removeSongDTO, HttpSession session){
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if(loggedUser == null){
            throw new BadRequestException("You have to login and then remove a song from playlist!");
        }
        return this.playlistService.removeSongFromPlaylist(removeSongDTO, loggedUser);
    }

    @PutMapping("/playlists/songs")
    public UserMessageDTO addSongToPlaylist(@RequestBody SongToPlaylistDTO addSongDTO, HttpSession session){
        User loggedUser = this.sessionManager.getLoggedUser(session);
        if(loggedUser == null){
            throw new BadRequestException("You have to login and then add a song to playlist!");
        }
        return this.playlistService.addSongToPlaylist(addSongDTO, loggedUser);
    }

    @GetMapping("/playlists/{playlistID}/songs")
    public SongToPlaylistDTO getPlaylistSongs(@PathVariable int playlistID){
        return this.playlistService.getPlaylistSongs(playlistID);
    }

    @GetMapping("/users/{username}/playlists")
    public PlaylistResponseDTO getUserPlaylists(@PathVariable String username){
        return this.playlistService.getUserPlaylist(username);
    }

    @GetMapping("/playlists")
    public PlaylistResponseDTO getAllPlaylists(){
        return this.playlistService.getAllPlaylists();
    }
}