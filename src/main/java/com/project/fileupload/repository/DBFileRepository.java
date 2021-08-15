package com.project.fileupload.repository;

import com.project.fileupload.model.DBFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBFileRepository extends JpaRepository<DBFile, String> {

    @Modifying
    @Query("DELETE FROM DBFile dbFile WHERE dbFile.fileId = :fileId")
    Integer deleteAllByFileId(@Param("fileId") String fileId);

    @Query("SELECT dbFile FROM DBFile dbFile WHERE dbFile.fileId = :fileId")
    List<DBFile> selectAllByFileId(@Param("fileId") String fileId);
}
