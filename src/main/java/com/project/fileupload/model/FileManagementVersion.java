package com.project.fileupload.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder(toBuilder = true)
@Entity
@Table(name = "T_FILE_MANAGEMENT_VERSION")
public class FileManagementVersion implements Serializable {
    @EmbeddedId
    private FileManagementId fileManagement;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "DATA_FILE_ID", referencedColumnName = "DOC_ID")
    private DBFile dataFile;

    @Column(name="LATEST")
    private Boolean latest;
}
