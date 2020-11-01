package com.firdian.uz.controllers.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
public class FileUploadResponse {

    private String fileName;

    private String contentType;

    private String url;

}
