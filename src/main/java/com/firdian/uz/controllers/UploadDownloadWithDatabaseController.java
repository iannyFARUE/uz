package com.firdian.uz.controllers;

import java.io.IOException;

import com.firdian.uz.controllers.response.FileUploadResponse;
import com.firdian.uz.controllers.response.Profiles;
import com.firdian.uz.service.ProfilesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class UploadDownloadWithDatabaseController {

    @Autowired
    private ProfilesRepository profilesRepo;

    @PostMapping("single/upload")
    public FileUploadResponse singleUpload(@RequestParam("file") MultipartFile file) throws IOException {

        String name = StringUtils.cleanPath(file.getOriginalFilename());
        Profiles profiles = new Profiles();
        profiles.setFile(file.getBytes());
        profiles.setName(name);

        profilesRepo.save(profiles);


        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/singleProfileUpload/")
                        .path(name)
                        .toUriString();
        
        String contentType = file.getContentType();

        FileUploadResponse response = new FileUploadResponse(name,contentType,url);

        return response;
    }
    
}
