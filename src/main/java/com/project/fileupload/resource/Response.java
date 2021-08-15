package com.project.fileupload.resource;

import lombok.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "data")
public class Response<T> implements Serializable {
    private String status;
    private Map<MessageType, Collection<String>> messages;
    private T data;
}
