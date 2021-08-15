package com.project.fileupload.storage;

import com.project.fileupload.resource.FileDTO;

public interface IStorage {

    default FileDTO createFile(FileDTO fileDTO) {
        throw new RuntimeException("Need to implement Storage");
    }

    default FileDTO getFile(String docId) {
        throw new RuntimeException("Need to implement Storage");
    }




}
