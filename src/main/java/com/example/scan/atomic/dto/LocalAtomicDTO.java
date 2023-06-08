package com.example.scan.atomic.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LocalAtomicDTO {

    private boolean bean;
    private String beanName;
    private String packageName;
    private String className;
    private String method;
    private List<String> paramsTypeList;
    private String returnType;

}
