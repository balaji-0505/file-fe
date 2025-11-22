package com.file.system.springboot.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "passhare_session_files")
public class PasshareSessionFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PasshareSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", nullable = false)
    private FileItem fileItem;

    @Column(name = "shared_by_user_id", nullable = false)
    private Long sharedByUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shared_by_user_id", insertable = false, updatable = false)
    private User sharedByUser;

    @Column(nullable = false)
    private Instant sharedAt = Instant.now();

    @Column(nullable = false)
    private long downloadCount = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PasshareSession getSession() {
        return session;
    }

    public void setSession(PasshareSession session) {
        this.session = session;
    }

    public FileItem getFileItem() {
        return fileItem;
    }

    public void setFileItem(FileItem fileItem) {
        this.fileItem = fileItem;
    }

    public Long getSharedByUserId() {
        return sharedByUserId;
    }

    public void setSharedByUserId(Long sharedByUserId) {
        this.sharedByUserId = sharedByUserId;
    }

    public User getSharedByUser() {
        return sharedByUser;
    }

    public void setSharedByUser(User sharedByUser) {
        this.sharedByUser = sharedByUser;
    }

    public Instant getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(Instant sharedAt) {
        this.sharedAt = sharedAt;
    }

    public long getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(long downloadCount) {
        this.downloadCount = downloadCount;
    }
}

