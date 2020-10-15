package com.shu.hbase.pojo;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UploadFileVO {

    private String fileStr;

    private String fileMD5;

    private String fileName;

    private String key;

    private String appId;

    private int chunk;

    private int chunks;

}
