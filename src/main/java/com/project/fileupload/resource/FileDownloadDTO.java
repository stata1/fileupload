package com.project.fileupload.resource;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "byteContent")
public class FileDownloadDTO implements Serializable {
    private String fileName;
    private String fileContentType;
    private byte[] byteContent;
}
