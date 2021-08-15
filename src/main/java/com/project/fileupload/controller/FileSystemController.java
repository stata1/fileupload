package com.project.fileupload.controller;

import com.project.fileupload.exception.FileManagementException;
import com.project.fileupload.model.FileManagementVersion;
import com.project.fileupload.resource.FileDTO;
import com.project.fileupload.resource.FileDownloadDTO;
import com.project.fileupload.resource.Response;
import com.project.fileupload.service.FileManagementService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/fileSystem")
@ApiOperation(value = "File System Operations to manage files", notes = "This class is used to perform all file related operations")
@Slf4j
public class FileSystemController {

    @Autowired
    private FileManagementService fileManagementService;

    @PostMapping
    @ApiOperation(value = "Upload New File", notes = "This method creates a new file in file system")
    public Response uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileContentType= file.getContentType();
        String filename = file.getOriginalFilename();
        byte[] byteContent = file.getBytes();
        log.info("filename : {} | fileContentType : {} | size : {}",filename,fileContentType,byteContent.length);
        FileDTO fileDTO = fileManagementService.uploadFile(filename,fileContentType,byteContent);
        return Response.builder().status("SUCCESS")
                .data(fileDTO)
                .build();

    }

    @PostMapping("/uploadMultipleFiles")
    @ApiOperation(value = "Upload Multiple Files", notes = "This method to creates multiple files in file system")
    public List<Response> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) throws IOException {
        List<Response> responses = new ArrayList<>();
        for(MultipartFile file: files) {
            responses.add(uploadFile(file));
        }
        return responses;
    }

    @GetMapping("/{fileId}")
    @ApiOperation(value = "Get file from File System", notes = "This method is to retrieve file from file system")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileId, @RequestParam(required = false) Integer version) throws FileManagementException {
        // Load file from database
        FileManagementVersion fileManagementVersion = fileManagementService.getFile(fileId,version);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileManagementVersion.getDataFile().getFileContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileManagementVersion.getDataFile().getFileName() + "\"")
                .body(new ByteArrayResource(fileManagementVersion.getDataFile().getContent()));
    }

    @GetMapping
    @ApiOperation(value = "Get All files based on criteria from File System", notes = "This method is to retrieve files based on search criteria from file system")
    public ResponseEntity<Resource> downloadAllFile(@RequestParam(required = false) String fileId, @RequestParam(required = false)  Integer version) throws FileManagementException, IOException {
        FileDownloadDTO fileDownload = fileManagementService.downloadFiles(fileId,version);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileDownload.getFileContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDownload.getFileName() + "\"")
                .body(new ByteArrayResource(fileDownload.getByteContent()));
    }

    @PutMapping("/{fileId}")
    @ApiOperation(value = "Update existing File with Version", notes = "This method is to update existing file with versioning in file system")
    public Response updateFile(@RequestParam("file") MultipartFile file,@PathVariable String fileId,@RequestParam(required = false) Boolean updateLatest) throws IOException, FileManagementException {
        String fileContentType= file.getContentType();
        String filename = file.getOriginalFilename();
        byte[] byteContent = file.getBytes();
        if(updateLatest == null) {
            updateLatest = false;
        }
        log.info("filename : {} | fileContentType : {} | size : {}",filename,fileContentType,byteContent.length);
        FileDTO fileDTO = fileManagementService.updateFile(fileId,filename,fileContentType,byteContent,updateLatest);
        return Response.builder().status("SUCCESS")
                .data(fileDTO)
                .build();

    }

    @DeleteMapping("/{fileId}")
    @ApiOperation(value = "Delete existing File(s)", notes = "This method is to update existing file with versioning in file system")
    public Response deleteFile(@PathVariable String fileId) throws IOException, FileManagementException {
        Integer count = fileManagementService.deleteFile(fileId);
        return Response.builder().status("SUCCESS")
                .data("Deleted fileId ["+fileId+"] Successfully of count ["+count+"]")
                .build();

    }


}
