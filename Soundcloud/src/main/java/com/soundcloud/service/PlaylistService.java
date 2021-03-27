package com.soundcloud.service;

import java.util.*;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.model.DTOs.PlaylistResponseDTO;
import com.soundcloud.model.DTOs.SongToPlaylistDTO;
import com.soundcloud.model.DTOs.UserMessageDTO;
import com.soundcloud.model.POJOs.Playlist;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.PlaylistRepository;
import com.soundcloud.model.repositories.SongRepository;
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
        Playlist playlist = this.playlistRepository.save(new Playlist(name, user));
        return new PlaylistResponseDTO(playlist);
    }

    @Transactional
    public UserMessageDTO removePlaylist(int playlistID, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(playlistID);
        if(playlist == null){
            throw new BadRequestException("Playlist does not exist!");
        }
        if(playlist.getOwner().getId() != user.getId()){
            throw new BadRequestException("You can`t remove foreign playlist!");
        }
        this.playlistRepository.deleteById(playlistID);
        return new UserMessageDTO("The playlist was successfully removed!");
    }

    public UserMessageDTO removeSongFromPlaylist(SongToPlaylistDTO removeSongDTO, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(removeSongDTO.getPlaylistID());
        if(playlist == null){
            throw new BadRequestException("Playlist does not exist!");
        }
        if(playlist.getOwner().getId() != user.getId()){
            throw new BadRequestException("You can`t remove a song from foreign playlist!");
        }
        //TODO
        List<Song> songs = this.songRepository.findAll();
        for(Song song : songs){
            if(song.getId() == removeSongDTO.getSongID()){
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    if(playlist.getSongs().get(i).getId() == removeSongDTO.getSongID()){
                        playlist.getSongs().remove(song);
                        this.playlistRepository.save(playlist);
                        return new UserMessageDTO("The song was successfully removed from your playlist!");
                    }
                }
                throw new BadRequestException("The song is already removed from this playlist!");
            }
        }
        throw new BadRequestException("The song is not in this playlist!");
    }

    public UserMessageDTO addSongToPlaylist(SongToPlaylistDTO addSongDTO, User user) {
        Playlist playlist = this.playlistRepository.getPlaylistById(addSongDTO.getPlaylistID());
        if(playlist == null){
            throw new BadRequestException("The playlist does not exist!");
        }
        if(playlist.getOwner().getId() != user.getId()){
            throw new BadRequestException("You can`t add a song to foreign playlist!");
        }
        //TODO
        List<Song> songs = this.songRepository.findAll();
        for(Song song : songs){
            if(song.getId() == addSongDTO.getSongID()){
                for (int i = 0; i < playlist.getSongs().size(); i++) {
                    if(playlist.getSongs().get(i).getId() == addSongDTO.getSongID()){
                        throw new BadRequestException("The song is already added to this playlist!");
                    }
                }
                playlist.getSongs().add(song);
                this.playlistRepository.save(playlist);
                return new UserMessageDTO("The song was successfully added to your playlist!");
            }
        }
        throw new BadRequestException("The song you are trying to add does not exist!");
    }

    public SongToPlaylistDTO getPlaylistSongs(int playlistID) {
        return null;
    }

    public PlaylistResponseDTO getAllPlaylists() {
        return null;
    }

    public PlaylistResponseDTO getUserPlaylist(String username) {
        return null;
    }
}