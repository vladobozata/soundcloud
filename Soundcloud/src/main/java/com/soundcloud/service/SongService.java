package com.soundcloud.service;

import com.soundcloud.exceptions.BadRequestException;
import com.soundcloud.exceptions.FileWriteException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.DTOs.Song.SongGetResponseDTO;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.SongRepository;
import com.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongService {
    private static final String FILE_SAVE_DIR = "Soundcloud/src/main/java/com/soundcloud/assets";
    private static final String FILE_SAVE_FORMAT = ".mp3";
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    @Autowired
    public SongService(SongRepository songRepository, UserRepository userRepository) {
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

    public List<SongGetResponseDTO> getByUsername(String username) {
        User owner = userRepository.findUserByUsername(username);
        if(owner == null) throw new NotFoundException("Could not find user " + username);

        List<Song> songs = songRepository.getAllByOwner(owner);

        if(songs == null || songs.isEmpty()) throw new BadRequestException("This user doesn't have any songs.");

        List<SongGetResponseDTO> response = songs.stream().map(SongGetResponseDTO::new).collect(Collectors.toList());
        return response;
    }

    public List<SongGetResponseDTO> getLikedByUser(User likedUser) {
        List<Song> songs = songRepository.getAllByLikersContaining(likedUser);
        return songs.stream().map(SongGetResponseDTO::new).collect(Collectors.toList());
    }
}