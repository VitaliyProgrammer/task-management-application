package org.example.service.impl;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.example.exception.DropBoxDownloadException;
import org.example.exception.DropBoxUploadException;
import org.example.service.DropBoxService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DropBoxServiceImpl implements DropBoxService {

    private final DbxClientV2 dbxClient;

    @Override
    public String uploadFile(MultipartFile file) {

        try (InputStream inputStream = file.getInputStream()) {

            String dropBoxPath = "/attachments/" + UUID.randomUUID() + "_"
                    + file.getOriginalFilename();

            FileMetadata metaData = dbxClient.files().uploadBuilder(dropBoxPath)
                    .withMode(WriteMode.ADD).uploadAndFinish(inputStream);

            return metaData.getId();
        } catch (Exception exception) {
            throw new DropBoxUploadException("DropBox upload failed!: " + exception);
        }
    }

    @Override
    public byte[] downloadFile(String dropBoxField) {

        try (DbxDownloader<FileMetadata> downloader =
                     dbxClient.files().download(dropBoxField)) {

            return downloader.getInputStream().readAllBytes();
        } catch (IOException | DbxException exception) {
            throw new DropBoxDownloadException("Failed to download file from DropBox!: "
                    + exception);
        }
    }
}
