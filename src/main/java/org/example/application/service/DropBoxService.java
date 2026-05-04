package org.example.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface DropBoxService {

    String uploadFile(MultipartFile file);

    byte[] downloadFile(String dropBoxField);
}
