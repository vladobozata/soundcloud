package com.soundcloud.controller;

import com.soundcloud.model.DTOs.Playlist.AddPlaylistRequestDTO;
import com.soundcloud.model.DTOs.Playlist.PlaylistResponseDTO;
import com.soundcloud.model.DTOs.Playlist.SongToPlaylistRequestDTO;
import com.soundcloud.model.DTOs.Playlist.UpdatePlaylistNameDTO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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
    public PlaylistResponseDTO addPlaylist(@RequestBody AddPlaylistRequestDTO playlistDTO, HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then add a playlist!");
        return new PlaylistResponseDTO(this.playlistService.addPlaylist(playlistDTO.getName(), loggedUser));
    }

    @DeleteMapping("/playlists/{playlistID}")
    public MessageDTO removePlaylist(@PathVariable int playlistID, HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then remove a playlist!");
        this.playlistService.removePlaylist(playlistID, loggedUser);
        return new MessageDTO("The playlist was successfully removed!");
    }

    @DeleteMapping("/playlists/songs")
    public PlaylistResponseDTO removeSongFromPlaylist(@RequestBody SongToPlaylistRequestDTO removeSongDTO, HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then remove a song from playlist!");
        return new PlaylistResponseDTO(this.playlistService.removeSongFromPlaylist(removeSongDTO, loggedUser));
    }

    @PutMapping("/playlists/songs")
    public PlaylistResponseDTO addSongToPlaylist(@RequestBody SongToPlaylistRequestDTO addSongDTO, HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then add a song to playlist!");
        return new PlaylistResponseDTO(this.playlistService.addSongToPlaylist(addSongDTO, loggedUser));
    }

    @PutMapping("/playlists/update-name")
    public PlaylistResponseDTO updatePlaylistName(@RequestBody UpdatePlaylistNameDTO updateNameDTO, HttpSession session){
        User loggedUser = this.sessionManager.validateUser(session, "You have to login and then update your playlist!");
        return new PlaylistResponseDTO(this.playlistService.updatePlaylistName(updateNameDTO, loggedUser));
    }

    @GetMapping("/playlists/{playlistID}/songs")
    public PlaylistResponseDTO getPlaylistSongs(@PathVariable int playlistID){
        return new PlaylistResponseDTO(this.playlistService.getPlaylistSongs(playlistID));
    }

    @GetMapping("/users/{username}/playlists")
    public List<PlaylistResponseDTO> getUserPlaylists(@PathVariable String username){
        List<PlaylistResponseDTO> responsePlaylist = new ArrayList<>();
        for (Playlist playlist : this.playlistService.getUserPlaylists(username)) {
            responsePlaylist.add(new PlaylistResponseDTO(playlist));
        }
        return responsePlaylist;
    }
}