package com.soundcloud.service;

import com.soundcloud.exceptions.FileWriteException;
import com.soundcloud.exceptions.NotFoundException;
import com.soundcloud.model.POJOs.Song;
import com.soundcloud.model.POJOs.User;
import com.soundcloud.model.repositories.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.LocalDateTime;

@Service
public class SongService {
    @Autowired
    private SongRepository repo;

    public Song uploadSong(String name, MultipartFile receivedFile, User loggedUser) {
        File localFile = new File("Soundcloud/src/main/java/com/soundcloud/assets",  System.currentTimeMillis() + ".mp3");
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

        repo.save(song);

        return song;
    }

    public Song getById(int id) {
        Song s = repo.findById(id);
        if (s == null)  {
            throw new NotFoundException("User with id " + id + " not found.");
        } else {
            return s;
        }
    }

    public byte[] playSong(int id) {
        Song s = getById(id);
        s.setViews(s.getViews()+1);
        repo.save(s);
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
}
