package org.example.service;

import java.io.IOException;
import org.example.dto.AttachmentResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

    AttachmentResponseDto uploadFile(Long taskId, MultipartFile file) throws IOException;

    Page<AttachmentResponseDto> findAttachmentsByTaskId(Long taskId, Pageable pageable);
}
