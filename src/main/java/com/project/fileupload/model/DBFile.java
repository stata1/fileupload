package com.project.fileupload.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = {"content"})
@Entity
@Table(name = "T_DB_FILES")
public class DBFile implements Serializable {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "DOC_ID")
    private String docId;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_MANAGE_ID")
    private String fileId;

    @Column(name = "FILE_CONTENT_TYPE")
    private String fileContentType;

    @Lob
    @Column(name = "CONTENT")
    private byte[] content;
}
