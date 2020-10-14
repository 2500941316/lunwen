package com.shu.hbase.service.impl.upload;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class MvcToFastDfs {
    private Logger logger = LoggerFactory.getLogger(MvcToFastDfs.class);

    /**
     * @param fis      文件输入流
     * @param fileName 文件名称
     * @return
     */
    public String uploadFile(InputStream fis, String fileName) {
        StorageClient1 storageClient = null;
        try {
            // 获取触发器
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.g_tracker_group);
            TrackerServer trackerServer = trackerClient.getConnection();
            // 获取存储服务器
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient = new StorageClient1(trackerServer, storageServer);
            logger.info("获取storage客户端成功");
            NameValuePair[] meta_list = null;
            //将输入流写入file_buff数组
            byte[] file_buff = null;
            if (fis != null) {
                int len = fis.available();
                file_buff = new byte[len];
                fis.read(file_buff);
            }
            logger.info("开始上传");

            return storageClient.upload_file1(file_buff, getFileExt(fileName), meta_list);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        } finally {
            if (fis != null) {
                try {
                    assert storageClient != null;
                    storageClient.close();
                    fis.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            }
        }
    }


    private String getFileExt(String fileName) {
        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }
    }
}

