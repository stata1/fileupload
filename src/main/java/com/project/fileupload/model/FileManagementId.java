package com.project.fileupload.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Embeddable
@EqualsAndHashCode
public class FileManagementId implements Serializable {
    @Column(name = "FILE_MANAGE_ID")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String fileId;
    @Column(name = "FILE_VERSION")
    private Integer fileVersion;
}
