package org.example.repository;

import org.example.entity.Attachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    Page<Attachment> findAllAttachmentsByTaskId(Long taskId, Pageable pageable);
}
