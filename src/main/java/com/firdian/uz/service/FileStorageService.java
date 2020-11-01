package com.firdian.uz.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private Path fileStoragePath;
    private String fileStorageLocation;

    public FileStorageService(@Value("${file.storage.location:temp}") String fileStoragePath) {
        this.fileStoragePath = Paths.get(fileStoragePath).toAbsolutePath().normalize();
        this.fileStorageLocation = fileStoragePath;

        try {
            Files.createDirectories(this.fileStoragePath);

        } catch (IOException exc) {
            throw new RuntimeException("File creation failed");
        }
    }

    public String storeFile(MultipartFile file) {

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Path filePath = Paths.get(fileStoragePath + "\\" + fileName);
        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException exc) {
            new RuntimeException("Error in copying a file");
        }
        return fileName;
    }

    public Resource downloadFile(String fileName) {
        Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
           throw new RuntimeException("Error in getting resources");
        }

        if(resource.exists() && resource.isReadable()){
            return resource;
        }
        else{
            throw new RuntimeException("The file doesn't exit or has been moved");
        }
    }
    
}
