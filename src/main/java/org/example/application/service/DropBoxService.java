package org.example.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface DropBoxService {

    String uploadFile(MultipartFile file);

    byte[] downloadFile(String dropBoxField);

    /**
     * Deletes a file by its Dropbox id. Returns {@code false} instead of throwing when the file
     * could not be deleted, so callers doing bulk cleanup can continue with the next file.
     */
    boolean deleteFile(String dropBoxField);
}
