package com.soundcloud.service;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.FileWriteException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DAOs.SongDAO;
import com.soundcloud.model.DTOs.MessageDTO;
import com.soundcloud.model.DTOs.Song.SongFilterRequestDTO;
import com.soundcloud.model.DTOs.Song.SongFilterResponseDTO;
import com.soundcloud.model.DTOs.Song.SongGetResponseDTO;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.SongRepository;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class SongService {
    private static final String FILE_SAVE_DIR = "Soundcloud/src/main/java/com/soundcloud/assets";
    private static final String FILE_SAVE_FORMAT = ".mp3";
    private static final String SORT_BY_LIKES = "likes";
    private static final String SORT_BY_DISLIKES = "dislikes";
    private static final String SORT_BY_COMMENTS = "comments";
    private static final String SORT_BY_VIEWS = "views";
    private static final String SORT_BY_DATE = "date";
    private static final String SORT_BY_PLAYLISTS = "playlists";
    private static final int RESULTS_PER_PAGE = 3;

    private final SongDAO songDAO;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    @Autowired
    public SongService(SongDAO songDAO, SongRepository songRepository, UserRepository userRepository) {
        this.songDAO = songDAO;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    public Song uploadSong(String name, MultipartFile receivedFile, User loggedUser) {
        File localFile = new File(FILE_SAVE_DIR,  System.nanoTime() + FILE_SAVE_FORMAT);
        System.out.println(localFile.getAbsolutePath());

        try {
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }
            localFile.createNewFile();
            OutputStream stream = new FileOutputStream(localFile);
            stream.write(receivedFile.getBytes());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            throw new FileWriteException("Could not save song to server - " + e.getMessage());
        }
        Song song = new Song(name, localFile.getPath(), loggedUser);
        songRepository.save(song);
        return song;
    }

    public Song getById(int id) {
        Song s = songRepository.getSongById(id);
        if (s == null)  {
            throw new NotFoundException("Song with id " + id + " not found.");
        } else {
            return s;
        }
    }

    public byte[] playSong(int id) {
        Song s = getById(id);
        s.setViews(s.getViews()+1);
        songRepository.save(s);

        File songFile = new File(s.getUrl());
        byte[] array = new byte[(int) songFile.length()];
        try {
            FileInputStream stream = new FileInputStream(songFile);
            stream.read(array);
            stream.close();
        } catch (IOException e) {
            throw new NotFoundException("Error parsing file at " + s.getUrl());
        }
        return array;
    }

    public User getOwnerForSongId(int songId) {
        Song song = getById(songId);
        return song.getOwner();
    }

    public void deleteSong(int songId) {
        SongGetResponseDTO dto = new SongGetResponseDTO(getById(songId));
        songRepository.deleteById(songId);
    }

    public List<SongFilterResponseDTO> getByUsername(String username) {
        User owner = userRepository.findUserByUsername(username);
        if(owner == null) throw new NotFoundException("Could not find user " + username);

        List<Song> songs = songRepository.getAllByOwner(owner);

        if(songs == null || songs.isEmpty()) throw new BadRequestException("This user doesn't have any songs.");

        List<SongFilterResponseDTO> response = songs.stream().map(SongFilterResponseDTO::new).collect(Collectors.toList());
        return response;
    }

    public List<SongFilterResponseDTO> getLikedSongsByUsername(String username) {
        List<Song> songs = userRepository.findUserByUsername(username).getLikedSongs();
        return songs.stream().map(SongFilterResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional
    public MessageDTO setLike(int songId, int likeValue, User loggedUser) {
        Song targetSong = songRepository.getSongById(songId);
        String action = new String("");

        if(targetSong == null) {
            throw new NotFoundException("The song you are trying to like or dislike was not found.");
        }

        switch (likeValue) {
            case 1:
                if (loggedUser.getLikedSongs().contains(targetSong)) return new MessageDTO("Song left liked.");
                if (loggedUser.getDislikedSongs().contains(targetSong)) setLike(songId, 0, loggedUser);
                loggedUser.getLikedSongs().add(targetSong);
                targetSong.getLikers().add(loggedUser);
                action = "liked";
                break;
            case 0:
                if(loggedUser.getLikedSongs().contains(targetSong)) {
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
                    return new MessageDTO("Song status left at neutral.");
                }
                break;
            case -1:
                if (loggedUser.getDislikedSongs().contains(targetSong)) return new MessageDTO("Song left disliked.");
                if (loggedUser.getLikedSongs().contains(targetSong)) setLike(songId, 0, loggedUser);
                loggedUser.getDislikedSongs().add(targetSong);
                targetSong.getDislikers().add(loggedUser);
                action = "disliked";
                break;
            default:
                throw new BadRequestException("Invalid like status passed.");
        }

        userRepository.save(loggedUser);
        songRepository.save(targetSong);
        return new MessageDTO("You successfully " +action+ " song id#" + songId);
    }

    public List<SongFilterResponseDTO> filterSongs(SongFilterRequestDTO searchRequest) throws SQLException {
        String title = searchRequest.getTitle();
        String sort = searchRequest.getSortBy().toLowerCase();
        String order = searchRequest.getOrderBy().toUpperCase();
        Integer page = searchRequest.getPage();

        if (title == null) {
            throw new BadRequestException("Trying to search without title.");
        } else if (order == null) {
            order = "ASC";
        } else if (!order.equals("ASC") && !order.equals("DESC")){
            System.out.println(order);
            throw new BadRequestException("Search order not recognized");
        }

        if (sort == null) sort = "date";
        if (page == null) page = 1;

        switch (sort) {
            case SORT_BY_PLAYLISTS:
                sort = "inPlaylists";
            case SORT_BY_LIKES:
            case SORT_BY_DISLIKES:
            case SORT_BY_VIEWS:
            case SORT_BY_DATE:
            case SORT_BY_COMMENTS:
                return songDAO.filterSongs(title, sort, order, page, RESULTS_PER_PAGE);
            default:
                throw new BadRequestException("Search method not supported.");
        }
    }
}