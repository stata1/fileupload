package com.project.fileupload.service;

import com.project.fileupload.exception.FileManagementException;
import com.project.fileupload.model.DBFile;
import com.project.fileupload.model.FileManagementId;
import com.project.fileupload.model.FileManagementVersion;
import com.project.fileupload.repository.DBFileRepository;
import com.project.fileupload.repository.FileManagementVersionRepository;
import com.project.fileupload.resource.FileDTO;
import com.project.fileupload.resource.FileDownloadDTO;
import com.project.fileupload.resource.MessageType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class FileManagementService {

    public static final Integer START_VERSION = 0;

    @Autowired
    private DBFileRepository dbFileRepository;

    @Autowired
    private FileManagementVersionRepository fileManagementVersionRepository;

    protected URI locationByEntity(String... paths){

        return ServletUriComponentsBuilder.fromCurrentContextPath().path(
                "{paths}").buildAndExpand(String.join("",paths)).toUri();
    }

    @Transactional
    public FileDTO uploadFile(String filename, String fileContentType, byte[] byteContent) {
        FileManagementVersion fileManagementVersion = buildFileManagementVersion(UUID.randomUUID().toString(),START_VERSION,filename,fileContentType,byteContent);
        log.info("Save FileManagementVersion | {}",fileManagementVersion);
        fileManagementVersion = fileManagementVersionRepository.save(fileManagementVersion);
        log.info("Saved FileManagementVersion | {}",fileManagementVersion);

        return FileDTO.builder()
                .fileId(fileManagementVersion.getFileManagement().getFileId())
                .version(fileManagementVersion.getFileManagement().getFileVersion())
                .fileContentType(fileManagementVersion.getDataFile().getFileContentType())
                .urls(buildUrls("Get File","/fileSystem/",fileManagementVersion.getFileManagement().getFileId()))
                .build();
    }

    private Map<String, URI> buildUrls(String key,String... paths) {
        Map<String, URI> urls = new HashMap<>();
        urls.put(key,locationByEntity(paths));
        return urls;
    }

    private FileManagementVersion buildFileManagementVersion(String fileId,Integer version,String filename, String fileContentType, byte[] byteContent) {
        return FileManagementVersion.builder()
                .latest(Boolean.TRUE)
                .fileManagement(buildFileManagementId(fileId,version))
                .dataFile(buildDataFile(null, fileId,filename,fileContentType,byteContent))
                .build();
    }

    private DBFile buildDataFile(String docId, String fileId, String filename, String fileContentType, byte[] byteContent) {
        return DBFile.builder()
                .docId(docId)
                .fileId(fileId)
                .fileContentType(fileContentType)
                .fileName(filename)
                .content(byteContent)
                .build();
    }

    private FileManagementId buildFileManagementId(String fileId,Integer version) {
        if(version == null) {
            version = START_VERSION;
        }
        return FileManagementId.builder()
                .fileId(fileId)
                .fileVersion(version)
                .build();
    }

    public FileManagementVersion getFile(String fileId,Integer version) throws FileManagementException {
        String message = null;
        if(version != null) {
            Optional<FileManagementVersion> fileManagementVersion = fileManagementVersionRepository.findById(FileManagementId.builder()
                            .fileId(fileId)
                            .fileVersion(version)
                    .build());
            if(fileManagementVersion.isPresent()) {
                return fileManagementVersion.get();
            }
            message= MessageFormat.format("File not found with fileId [{0}] and version [{1}]", fileId,version);
        } else {
            List<FileManagementVersion> fileManagementVersions = fileManagementVersionRepository.findLatestFile(fileId, Boolean.TRUE);
            if (fileManagementVersions != null && !fileManagementVersions.isEmpty()) {
                return fileManagementVersions.get(0);
            }
            message= MessageFormat.format("File not found with fileId [{0}]", fileId);
        }
        throw new FileManagementException(HttpStatus.NOT_FOUND, MessageType.ERROR,message);
    }

    @Transactional
    public FileDTO updateFile(String fileId, String filename, String fileContentType, byte[] byteContent, Boolean updateLatest) throws FileManagementException {
        FileManagementVersion fileManagementVersion = getFile(fileId,null);
        if(updateLatest) {
            fileManagementVersion.getDataFile().setFileName(filename);
            fileManagementVersion.getDataFile().setFileContentType(fileContentType);
            fileManagementVersion.getDataFile().setContent(byteContent);
        } else {
            fileManagementVersion.setLatest(false);
            fileManagementVersionRepository.save(fileManagementVersion);
            fileManagementVersion = FileManagementVersion.builder()
                    .latest(true)
                    .dataFile(buildDataFile(null, fileId,filename, fileContentType, byteContent))
                    .fileManagement(buildFileManagementId(fileId, fileManagementVersion.getFileManagement().getFileVersion() + 1))
                    .build();
        }
        fileManagementVersionRepository.save(fileManagementVersion);
        return FileDTO.builder()
                .fileId(fileManagementVersion.getFileManagement().getFileId())
                .version(fileManagementVersion.getFileManagement().getFileVersion())
                .fileContentType(fileManagementVersion.getDataFile().getFileContentType())
                .urls(buildUrls("Get File","/fileSystem/",fileManagementVersion.getFileManagement().getFileId()))
                .build();
    }

    @Transactional
    public Integer deleteFile(String fileId) throws FileManagementException {
        Integer count = fileManagementVersionRepository.deleteAllByFileId(fileId);
        Integer dbFileCount = dbFileRepository.deleteAllByFileId(fileId);
        log.info("Deleted count from fileManagementVersion table | {}", count);
        log.info("Deleted count from dbFile table | {}", dbFileCount);
        if(count <=0) {
            String message= MessageFormat.format("File fileId [{0}] not found/ deleted from File System", fileId);
            throw new FileManagementException(HttpStatus.NOT_FOUND, MessageType.ERROR,message);
        }
        return count;
    }

    public FileDownloadDTO downloadFiles(String fileId, Integer version) throws FileManagementException, IOException {
        List<FileManagementVersion> fileManagementVersions = null;
        if(StringUtils.isNotBlank(fileId)) {
            if(version == null) {
                fileManagementVersions = fileManagementVersionRepository.findAllFilesByFileId(fileId);
            } else {
                fileManagementVersions = fileManagementVersionRepository.findAllFilesByFileIdByVersion(fileId,version);
            }
        } else {
            Pageable pageable = PageRequest.ofSize(5);
            Page<FileManagementVersion> fileManagementVersionPage= fileManagementVersionRepository.findAll(pageable);
            fileManagementVersions = fileManagementVersionPage.stream().collect(Collectors.toList());
        }

        if(fileManagementVersions != null && fileManagementVersions.size() >0 ) {
            FileDownloadDTO fileDownloadDTO= null;
            if(fileManagementVersions.size() ==1) {
                FileManagementVersion fileManagementVersion = fileManagementVersions.get(0);
                fileDownloadDTO = FileDownloadDTO.builder()
                        .fileContentType(fileManagementVersion.getDataFile().getFileContentType())
                        .fileName(fileManagementVersion.getDataFile().getFileName())
                        .byteContent(fileManagementVersion.getDataFile().getContent())
                        .build();
            } else {
                ByteArrayOutputStream fos = new ByteArrayOutputStream();
                try (ZipOutputStream zipOut = new ZipOutputStream(fos)) {

                    for (FileManagementVersion fileManagementVersion : fileManagementVersions) {
                        ZipEntry zipEntry = new ZipEntry(System.currentTimeMillis()+"_"+fileManagementVersion.getDataFile().getFileName());
                        zipOut.putNextEntry(zipEntry);
                        zipOut.write(fileManagementVersion.getDataFile().getContent());
                    }
                }
                fileDownloadDTO = FileDownloadDTO.builder()
                        .fileContentType("application/octet-stream")
                        .fileName("download_"+System.currentTimeMillis()+".zip")
                        .byteContent(fos.toByteArray())
                        .build();
            }
            return fileDownloadDTO;
        }

        String message= MessageFormat.format("File(s) not found in File System",null);
        throw new FileManagementException(HttpStatus.NOT_FOUND, MessageType.ERROR,message);
    }
}
