package com.emt.dms1.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityResponse<T> {
    private String message;
    private T data;
    private List<T> entities;
    private Integer statusCode;
    private HttpHeaders headers;
    private long contentLength;
    private Resource body;
}
