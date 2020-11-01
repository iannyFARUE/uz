package com.firdian.uz.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firdian.uz.controllers.response.FileUploadResponse;
import com.firdian.uz.models.Person;
import com.firdian.uz.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
public class HomeController {


    private FileStorageService fileStorage;

    public HomeController(FileStorageService fileStorageService){
        this.fileStorage = fileStorageService;
    }

    @GetMapping("/")
    public List<Person> home(){
        List<Person> names = new ArrayList<>();
        names.add(new Person(11,"Ian"));
        names.add(new Person(23,"Farai"));
     



        return names;
    }

    @PostMapping("/upload/single")
    public ResponseEntity<FileUploadResponse> upload(@RequestParam("file") MultipartFile image){
         
    String fileName =  fileStorage.storeFile(image);

    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloads/")
                .path(fileName)
                .toUriString();

    String contentType = image.getContentType();

    FileUploadResponse response = new FileUploadResponse(fileName,contentType,url);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS,"*");

    return ResponseEntity.ok().body(response);

    } 

    @GetMapping("/downloads/{filename}")
    public ResponseEntity<Resource> downloadSingleFile(@PathVariable String filename, HttpServletRequest request){


        String mimeType;
        Resource resource  = fileStorage.downloadFile(filename);

        try{
        mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }
        catch(IOException exc){
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

        }
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(mimeType)).header(HttpHeaders.CONTENT_DISPOSITION,"inline;filename="+resource.getFilename()).body(resource);
    }

    @PostMapping("/multiple/uploads")
    public List<FileUploadResponse> multiUpload(@RequestParam("files") MultipartFile[] files){
        List<FileUploadResponse> responses = new ArrayList<>();
        Arrays.asList(files)
            .stream().forEach(image ->{
                         
        String fileName =  fileStorage.storeFile(image);

        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/downloads/")
                .path(fileName)
                .toUriString();

        String contentType = image.getContentType();

        FileUploadResponse response = new FileUploadResponse(fileName,contentType,url);
        responses.add(response);

    });
    return responses;
    }

    @GetMapping("zipDownloads")
    public void zipDownloads(@RequestParam("filename") String[] files, HttpServletResponse response)
            throws IOException {
        try(ZipOutputStream zout = new ZipOutputStream(response.getOutputStream())){

            Arrays.asList(files)
                    .stream()
                    .forEach(file->{

                    Resource resource = fileStorage.downloadFile(file);

                    ZipEntry entry = new ZipEntry(resource.getFilename());
                        try {
                            entry.setSize(resource.contentLength());
                            zout.putNextEntry(entry);
                            StreamUtils.copy(resource.getInputStream(), zout);
                           zout.closeEntry();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }  
                    });
                    zout.finish();

        }

    }
   
}
