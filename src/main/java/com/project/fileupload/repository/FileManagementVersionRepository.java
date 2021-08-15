package com.project.fileupload.repository;

import com.project.fileupload.model.DBFile;
import com.project.fileupload.model.FileManagementId;
import com.project.fileupload.model.FileManagementVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileManagementVersionRepository  extends JpaRepository<FileManagementVersion, FileManagementId> {
    @Query("SELECT fileManagementVersion FROM FileManagementVersion fileManagementVersion WHERE fileManagementVersion.fileManagement.fileId = :fileId and fileManagementVersion.latest = :latest")
    List<FileManagementVersion> findLatestFile(@Param("fileId") String fileId, @Param("latest") Boolean latest);

    @Query("SELECT fileManagementVersion FROM FileManagementVersion fileManagementVersion WHERE fileManagementVersion.fileManagement.fileId = :fileId")
    List<FileManagementVersion> findAllFilesByFileId(@Param("fileId") String fileId);

    @Modifying
    @Query("DELETE FROM FileManagementVersion fileManagementVersion WHERE fileManagementVersion.fileManagement.fileId = :fileId")
    Integer deleteAllByFileId(@Param("fileId") String fileId);

    @Query("SELECT fileManagementVersion FROM FileManagementVersion fileManagementVersion WHERE fileManagementVersion.fileManagement.fileId = :fileId and fileManagementVersion.fileManagement.fileVersion = :fileVersion")
    List<FileManagementVersion> findAllFilesByFileIdByVersion(@Param("fileId") String fileId,@Param("fileVersion") Integer fileVersion);
}
