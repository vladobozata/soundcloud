package com.soundcloud.service;

import java.util.*;

import com.soundcloud.exceptions.AuthenticationException;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.Playlist.PlaylistResponseDTO;
import com.soundcloud.model.DTOs.Playlist.SongToPlaylistDTO;
import com.soundcloud.model.DTOs.Playlist.UpdatePlaylistNameDTO;
import com.soundcloud.model.DTOs.User.UserMessageDTO;
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

    public PlaylistResponseDTO addPlaylist(String name, User user) {
        if (!Validator.validateName(name)) {
            throw new BadRequestException("Playlist name format is not correct!");
        }
        if (this.playlistRepository.getPlaylistByName(name) != null) {
            throw new BadRequestException("Playlist name already exists!");
        }
        Playlist playlist = this.playlistRepository.save(new Playlist(name, user));
        return new PlaylistResponseDTO(playlist);
    }

    @Transactional
    public UserMessageDTO removePlaylist(int playlistID, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(playlistID);
        if (playlist == null) {
            throw new NotFoundException("Playlist not found!");
        }
        if (playlist.getOwner().getId() != user.getId()) {
            throw new AuthenticationException("You can`t remove foreign playlist!");
        }
        this.playlistRepository.deleteById(playlistID);
        return new UserMessageDTO("The playlist was successfully removed!");
    }

    public UserMessageDTO removeSongFromPlaylist(SongToPlaylistDTO removeSongDTO, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(removeSongDTO.getPlaylistID());
        if (playlist == null) {
            throw new NotFoundException("Playlist not found!");
        }
        if (playlist.getOwner().getId() != user.getId()) {
            throw new AuthenticationException("You can`t remove a song from foreign playlist!");
        }
        Song song = this.songRepository.getSongById(removeSongDTO.getSongID());
        if (song.getId() == removeSongDTO.getSongID()) {
            for (int i = 0; i < playlist.getSongs().size(); i++) {
                if (playlist.getSongs().get(i).getId() == removeSongDTO.getSongID()) {
                    playlist.getSongs().remove(song);
                    this.playlistRepository.save(playlist);
                    return new UserMessageDTO("The song was successfully removed from your playlist!");
                }
            }
        }
        throw new NotFoundException("The song you are trying to remove not found!");
    }

    public UserMessageDTO addSongToPlaylist(SongToPlaylistDTO addSongDTO, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(addSongDTO.getPlaylistID());
        if (playlist == null) {
            throw new NotFoundException("Playlist not found!");
        }
        if (playlist.getOwner().getId() != user.getId()) {
            throw new AuthenticationException("You can`t add a song to foreign playlist!");
        }
        Song song = this.songRepository.getSongById(addSongDTO.getSongID());
        if (song.getId() == addSongDTO.getSongID()) {
            for (int i = 0; i < playlist.getSongs().size(); i++) {
                if (playlist.getSongs().get(i).getId() == addSongDTO.getSongID()) {
                    throw new BadRequestException("The song is already added to this playlist!");
                }
            }
            playlist.getSongs().add(song);
            this.playlistRepository.save(playlist);
            return new UserMessageDTO("The song was successfully added to your playlist!");
        }
        throw new NotFoundException("The song you are trying to add not found!");
    }

    public PlaylistResponseDTO getPlaylistSongs(int playlistID) {
        Playlist playlist = this.playlistRepository.getPlaylistById(playlistID);
        if (playlist == null) {
            throw new NotFoundException("Playlist not found!");
        }
        return new PlaylistResponseDTO(playlist);
    }

    public List<PlaylistResponseDTO> getUserPlaylists(String username) {
        List<Playlist> userPlaylists = this.playlistRepository.getPlaylistsByOwner_Username(username);
        if (userPlaylists.isEmpty()) {
            throw new NotFoundException("No playlists found for " + username + "!");
        }
        List<PlaylistResponseDTO> responsePlaylist = new ArrayList<>();
        for (Playlist playlist : userPlaylists) {
            responsePlaylist.add(new PlaylistResponseDTO(playlist));
        }
        return responsePlaylist;
    }

    public UserMessageDTO updatePlaylistName(UpdatePlaylistNameDTO updateNameDTO, User loggedUser) {
        Playlist playlist = this.playlistRepository.getPlaylistById(updateNameDTO.getPlaylistID());
        if (playlist == null) {
            throw new NotFoundException("Playlist not found!");
        }
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
        this.playlistRepository.save(playlist);
        return new UserMessageDTO("You successfully updated playlist name!");
    }
}