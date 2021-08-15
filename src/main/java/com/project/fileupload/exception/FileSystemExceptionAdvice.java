package com.project.fileupload.exception;

import com.project.fileupload.resource.MessageType;
import com.project.fileupload.resource.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import springfox.documentation.service.ResponseMessage;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class FileSystemExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Response> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.error(exc.getMessage(),exc);
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(Response.builder().status(HttpStatus.EXPECTATION_FAILED.toString())
                        .messages(buildMessages(MessageType.ERROR,"File size exceed to max permitted size of file.",exc.getLocalizedMessage()))
                        .status("ERROR")
                        .build()
                );
    }

    @ExceptionHandler(FileManagementException.class)
    public ResponseEntity<Response> handleFileManagementException(FileManagementException exc) {
        log.error(exc.getMessage(),exc);
        return ResponseEntity.status(exc.getHttpStatus())
                .body(Response.builder().status(exc.getHttpStatus().toString())
                        .messages(buildMessages(exc.getMessageType(),exc.getMessage()))
                        .status("ERROR")
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleCommonException(Exception exc) {
        log.error(exc.getMessage(),exc);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Response.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                        .messages(buildMessages(MessageType.ERROR,exc.getLocalizedMessage()))
                        .status("ERROR")
                        .build()
                );
    }

    private Map<MessageType, Collection<String>> buildMessages(MessageType messageType, String... messages) {
        Map<MessageType, Collection<String>> messagesMap = new HashMap<>();
        if(messages != null && messages.length > 0) {
            messagesMap.put(messageType,Arrays.stream(messages).collect(Collectors.toSet()));
        }
        return messagesMap;
    }
}
