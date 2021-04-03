package com.soundcloud.service;

import java.util.*;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.Playlist.SongToPlaylistRequestDTO;
import com.soundcloud.model.DTOs.Playlist.UpdatePlaylistNameDTO;
import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.PlaylistRepository;
import com.soundcloud.model.repositories.SongRepository;
import com.soundcloud.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
    }

    public void validatePlaylist(Playlist playlist){
        if (playlist == null) {
            throw new NotFoundException("Playlist not found!");
        }
    }

    public Playlist addPlaylist(String name, User user) {
        if (!Validator.validateName(name)) {
            throw new BadRequestException("Playlist name format is not correct!");
        }
        if (this.playlistRepository.getPlaylistByName(name) != null) {
            throw new BadRequestException("Playlist name already exists!");
        }
        return this.playlistRepository.save(new Playlist(name, user));
    }

    @Transactional
    public void removePlaylist(int playlistID, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(playlistID);
        validatePlaylist(playlist);
        if (playlist.getOwner().getId() != user.getId()) {
            throw new AuthenticationException("You can`t remove foreign playlist!");
        }
        this.playlistRepository.deleteById(playlistID);
    }

    @Transactional
    public Playlist removeSongFromPlaylist(SongToPlaylistRequestDTO removeSongDTO, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(removeSongDTO.getPlaylistID());
        validatePlaylist(playlist);
        if (playlist.getOwner().getId() != user.getId()) {
            throw new AuthenticationException("You can`t remove a song from foreign playlist!");
        }
        Song song = this.songRepository.getSongById(removeSongDTO.getSongID());
        if (!playlist.getSongs().contains(song)) {
            throw new NotFoundException("The song you are trying to remove not found!");
        }
        playlist.getSongs().remove(song);
        song.getPlaylists().remove(playlist);
        return this.playlistRepository.save(playlist);
    }

    public Playlist addSongToPlaylist(SongToPlaylistRequestDTO addSongDTO, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(addSongDTO.getPlaylistID());
        validatePlaylist(playlist);
        if (playlist.getOwner().getId() != user.getId()) {
            throw new AuthenticationException("You can`t add a song to foreign playlist!");
        }
        Song song = this.songRepository.getSongById(addSongDTO.getSongID());
        if (song == null) {
            throw new NotFoundException("The song you are trying to add was not found!");
        }
        if (playlist.getSongs().contains(song)) {
            throw new BadRequestException("The song is already added to this playlist!");
        }
        playlist.getSongs().add(song);
        song.getPlaylists().add(playlist);
        return this.playlistRepository.save(playlist);
    }

    public Playlist getPlaylistSongs(int playlistID) {
        Playlist playlist = this.playlistRepository.getPlaylistById(playlistID);
        validatePlaylist(playlist);
        return playlist;
    }

    public List<Playlist> getUserPlaylists(String username) {
        List<Playlist> userPlaylists = this.playlistRepository.getPlaylistsByOwner_Username(username);
        if (userPlaylists.isEmpty()) {
            throw new NotFoundException("No playlists found for " + username + "!");
        }
        return userPlaylists;
    }

    public Playlist updatePlaylistName(UpdatePlaylistNameDTO updateNameDTO, User loggedUser) {
        Playlist playlist = this.playlistRepository.getPlaylistById(updateNameDTO.getPlaylistID());
        validatePlaylist(playlist);
        if (playlist.getOwner().getId() != loggedUser.getId()) {
            throw new AuthenticationException("You can`t update foreign playlist!");
        }
        if (!Validator.validateName(updateNameDTO.getName())) {
            throw new BadRequestException("Playlist name format is not correct!");
        }
        if (this.playlistRepository.getPlaylistByName(updateNameDTO.getName()) != null) {
            throw new BadRequestException("Playlist name already exists!");
        }
        playlist.setName(updateNameDTO.getName());
        return this.playlistRepository.save(playlist);
    }
}