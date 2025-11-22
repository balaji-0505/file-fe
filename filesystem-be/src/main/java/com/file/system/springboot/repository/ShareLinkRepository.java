package com.file.system.springboot.repository;

import com.file.system.springboot.model.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareLinkRepository extends JpaRepository<ShareLink, Long> {
    Optional<ShareLink> findByLinkId(String linkId);
}


