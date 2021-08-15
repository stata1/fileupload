package com.project.fileupload.exception;

import com.project.fileupload.resource.MessageType;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FileManagementException extends Exception {

    private static final long serialVersionUID = -1468901461961846862L;

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    private MessageType messageType = MessageType.ERROR;

    public FileManagementException(String message) {
        super(message);
    }

    public FileManagementException(HttpStatus httpStatus,MessageType messageType,String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.messageType = messageType;
    }

    public FileManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileManagementException(HttpStatus httpStatus,MessageType messageType,String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
        this.messageType = messageType;
    }
}
