package com.soundcloud.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.FileReadWriteException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DAOs.SongDAO;
import com.soundcloud.model.DTOs.LikeDislikeResponseDTO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.DTOs.Song.SongFilterRequestDTO;
import com.soundcloud.model.DTOs.Song.SongFilterResponseDTO;
import com.soundcloud.model.DTOs.Song.SongGetResponseDTO;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.SongRepository;
import com.soundcloud.model.repositories.UserRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {
    private static final String STORAGE_BUCKET_NAME = "amazon-storage-soundcloud";
    private static final String SORT_BY_LIKES = "likes";
    private static final String SORT_BY_DISLIKES = "dislikes";
    private static final String SORT_BY_COMMENTS = "comments";
    private static final String SORT_BY_VIEWS = "views";
    private static final String SORT_BY_DATE = "date";
    private static final String SORT_BY_PLAYLISTS = "playlists";
    private static final int FILTER_RESULTS_PER_PAGE = 5;
    private static final long MAXIMUM_FILESIZE_BYTES = 10*1024*1024; // 10 mb

    private final SongDAO songDAO;
    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AmazonS3 storageClient;

    @Autowired
    public SongService(SongDAO songDAO, SongRepository songRepository, UserRepository userRepository, AmazonS3 storageClient) {
        this.songDAO = songDAO;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.storageClient = storageClient;
    }

    public Song uploadSong(String title, MultipartFile receivedFile, User loggedUser) {
        String originalName = receivedFile.getOriginalFilename();
        String extension, fileName, fullName;

        if (receivedFile.getSize() > MAXIMUM_FILESIZE_BYTES) {
            throw new BadRequestException("File size too large.");
        }
        if (originalName == null || originalName.isBlank()) {
            throw new BadRequestException("File name is empty.");
        }
        if (!originalName.contains(".")) {
            throw new BadRequestException("Uploaded file does not have an extension.");
        } else {
            extension = originalName.substring(originalName.indexOf('.'));
        }
        if (title == null || this.songRepository.getSongByTitle(title) != null) {
            throw new BadRequestException("A track with this title already exists.");
        }
        if (!extension.equals(".mp3")) {
            throw new BadRequestException("Unrecognized file extension. Please select an mp3 file.");
        }

        fileName = String.valueOf(System.nanoTime());
        fullName = fileName + extension;

        Song song;
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentType("audio/mpeg");
        meta.setContentLength(receivedFile.getSize());
        try {
            this.storageClient.putObject(STORAGE_BUCKET_NAME, fullName, receivedFile.getInputStream(), meta);
            song = new Song(title, fullName, loggedUser);
            this.songRepository.save(song);
        } catch (AmazonServiceException | IOException e) {
            throw new FileReadWriteException("Could not save song to server - " + e.getMessage());
        }

        return song;
    }

    public Song getById(int id) {
        Song s = this.songRepository.getSongById(id);
        if (s == null) {
            throw new NotFoundException("Song with id " + id + " not found.");
        } else {
            return s;
        }
    }

    public byte[] playSong(int id) {
        Song s = getById(id);
        if (s == null) throw new NotFoundException("The song you are trying to play was not found.");

        try {
            S3Object downloadedFile = this.storageClient.getObject(STORAGE_BUCKET_NAME, s.getUrl());
            S3ObjectInputStream stream = downloadedFile.getObjectContent();

            s.setViews(s.getViews() + 1);
            this.songRepository.save(s);

            return IOUtils.toByteArray(stream);
        } catch (AmazonServiceException | IOException e) {
            throw new FileReadWriteException("Could not download the song from the server. Details: " + e.getMessage());
        }
    }

    public User getOwnerForSongId(int songId) {
        Song song = getById(songId);
        return song.getOwner();
    }

    public void deleteSong(int songId) {
        SongGetResponseDTO dto = new SongGetResponseDTO(getById(songId));
        try {
            this.storageClient.deleteObject(STORAGE_BUCKET_NAME, dto.getUrl());
        } catch (SdkClientException e) {
            throw new FileReadWriteException("Failed to delete song file from the server. Details: " + e.getMessage());
        }
        this.songRepository.deleteById(songId);
    }

    public List<SongFilterResponseDTO> getByUsername(String username) {
        User owner = this.userRepository.findUserByUsername(username);
        if (owner == null) {
            throw new NotFoundException("Could not find user " + username);
        }

        List<Song> songs = this.songRepository.getAllByOwner(owner);

        return songs.stream().map(SongFilterResponseDTO::new).collect(Collectors.toList());
    }

    public List<SongFilterResponseDTO> getLikedSongsByUsername(String username) {
        User user = this.userRepository.findUserByUsername(username);
        if (user == null) {
            throw new NotFoundException("Could not find user " + username);
        }

        List<Song> songs = user.getLikedSongs();

        return songs.stream().map(SongFilterResponseDTO::new).collect(Collectors.toList());
    }

    private int getLikesSumFor (Song song) {
        return song.getLikers().size() - song.getDislikers().size();
    }

    @Transactional
    public LikeDislikeResponseDTO setLike(int songId, int likeValue, User loggedUser) {
        Song targetSong = this.songRepository.getSongById(songId);
        String action;

        if (targetSong == null) {
            throw new NotFoundException("The song you are trying to like or dislike was not found.");
        }

        switch (likeValue) {
            case 1:
                if (loggedUser.getLikedSongs().contains(targetSong)) return new LikeDislikeResponseDTO("Song left liked.", getLikesSumFor(targetSong));
                if (loggedUser.getDislikedSongs().contains(targetSong)) setLike(songId, 0, loggedUser);
                loggedUser.getLikedSongs().add(targetSong);
                targetSong.getLikers().add(loggedUser);
                action = "liked";
                break;
            case 0:
                if (loggedUser.getLikedSongs().contains(targetSong)) {
                    // If song was previously liked
                    loggedUser.getLikedSongs().remove(targetSong);
                    targetSong.getLikers().remove(loggedUser);
                    action = "unliked";
                } else if (loggedUser.getDislikedSongs().contains(targetSong)) {
                    // If song was previously disliked
                    loggedUser.getDislikedSongs().remove(targetSong);
                    targetSong.getDislikers().remove(loggedUser);
                    action = "undisliked";
                } else {
                    // If song was previously neutral
                    return new LikeDislikeResponseDTO("Song status left at neutral.", getLikesSumFor(targetSong));
                }
                break;
            case -1:
                if (loggedUser.getDislikedSongs().contains(targetSong)) return new LikeDislikeResponseDTO("Song left disliked.", getLikesSumFor(targetSong));
                if (loggedUser.getLikedSongs().contains(targetSong)) setLike(songId, 0, loggedUser);
                loggedUser.getDislikedSongs().add(targetSong);
                targetSong.getDislikers().add(loggedUser);
                action = "disliked";
                break;
            default:
                throw new BadRequestException("Invalid like status passed.");
        }

        this.userRepository.save(loggedUser);
        this.songRepository.save(targetSong);
        return new LikeDislikeResponseDTO("You successfully " + action + " song id#" + songId, getLikesSumFor(targetSong));
    }

    @SneakyThrows
    public List<SongFilterResponseDTO> filterSongs(SongFilterRequestDTO searchRequest) {
        String title = searchRequest.getTitle();
        Integer page = searchRequest.getPage();

        String sort = searchRequest.getSortBy();
        if (sort == null) sort = "";
        else sort = sort.toLowerCase().trim();

        String order = searchRequest.getOrderBy();
        if (order == null) order = "";
        else order = order.toUpperCase().trim();

        if (title == null) {
            throw new BadRequestException("Trying to search without title.");
        } else if (order.equals("")) {
            order = "ASC";
        } else if (!order.equals("ASC") && !order.equals("DESC")) {
            throw new BadRequestException("Search order not recognized");
        }

        if (sort.trim().equals("")) sort = "date";
        if (page == null) page = 1;

        switch (sort) {
            case SORT_BY_PLAYLISTS:
                sort = "inPlaylists";
            case SORT_BY_LIKES:
            case SORT_BY_DISLIKES:
            case SORT_BY_VIEWS:
            case SORT_BY_DATE:
            case SORT_BY_COMMENTS:
                return this.songDAO.filterSongs(title, sort, order, page, FILTER_RESULTS_PER_PAGE);
            default:
                throw new BadRequestException("Search method not supported.");
        }
    }
}