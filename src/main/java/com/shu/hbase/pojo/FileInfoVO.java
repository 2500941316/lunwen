package com.shu.hbase.pojo;

import lombok.Data;

@Data
public class FileInfoVO {
    private String name;
    private Long size;
    private Long time;
    private String type;
    private String path;
    private String back;
    private boolean isDir;
    private boolean isNew;
    private float precent;
    private boolean isMyShare;
    private String fileId;
    private String sharer;


   public FileInfoVO()
    {
        this.setPrecent(100);
    }
}
