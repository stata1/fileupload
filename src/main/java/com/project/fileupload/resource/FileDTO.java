package com.project.fileupload.resource;


import lombok.*;

import javax.persistence.Column;
import java.io.Serializable;
import java.net.URI;
import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class FileDTO implements Serializable {
    private String fileId;
    private Integer version;
    private Map<String, URI> urls;
    private String fileContentType;
}
