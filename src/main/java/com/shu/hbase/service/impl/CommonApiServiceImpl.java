package com.shu.hbase.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.shu.hbase.pojo.Static;
import com.shu.hbase.pojo.UploadFileVO;
import com.shu.hbase.service.interfaces.CommonApiService;
import com.shu.hbase.tools.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.util.Base64;

@Service
public class CommonApiServiceImpl implements CommonApiService {

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    private Logger logger = LoggerFactory.getLogger(PublicServiceImpl.class);


    @Override
    public String upload(UploadFileVO uploadFileVO) {

        logger.info("开始执行文件分片上传方法");
        if (uploadToFDFS(uploadFileVO)) {

        }
        return null;
    }


    private boolean uploadToFDFS(UploadFileVO uploadFileVO) {
        logger.info("fastdfs开始接收分片文件" + uploadFileVO.getChunk());
        //定义文件Id
        String fileId = uploadFileVO.getAppId() + "_" + System.currentTimeMillis();

        //截取上传文件的类型
        String fileType = uploadFileVO.getFileName().substring(uploadFileVO.getFileName().lastIndexOf(".") + 1);
        //对当前分片的文件字符串进行base64解码,转为输入流
        byte[] decode = Base64.getDecoder().decode(uploadFileVO.getFileStr());
        System.out.println(decode.length);
        BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(decode));
        StorePath path = null;
        String noGroupPath = null;
        Long historyUpload = 0L;
        String historyUploadStr = RedisUtil.getString(Static.REDISSIZE + uploadFileVO.getFileMD5());
        if (StrUtil.isNotEmpty(historyUploadStr)) {
            historyUpload = Convert.toLong(historyUploadStr);
        }
        logger.info("historyUpload大小为:" + historyUpload);
        if (uploadFileVO.getChunk() == 0) {

            logger.info(uploadFileVO.getChunk() + "开始上传");
            try {
                path = appendFileStorageClient.uploadAppenderFile("group1", inputStream, decode.length, fileType);
                logger.info(uploadFileVO.getChunk() + ":更新完fastdfs");
                if (path == null) {
                    logger.error("fastdfs获取文件出错");
                    return false;
                }
            } catch (Exception e) {
                logger.error("初次上传远程文件出错", e);
                return false;
            }
            noGroupPath = path.getPath();
            RedisUtil.setString(Static.REDISPATH + uploadFileVO.getFileMD5(), noGroupPath);
            logger.info("上传文件 result={}", path);
        } else {
            logger.info(uploadFileVO.getChunk() + "块开始合并");
            noGroupPath = RedisUtil.getString(Static.REDISPATH + uploadFileVO.getFileMD5());
            if (noGroupPath == null) {
                logger.error("文件初始化失败");
                return false;
            }
            try {
                //追加方式实际实用如果中途出错多次,可能会出现重复追加情况,这里改成修改模式,即时多次传来重复文件块,依然可以保证文件拼接正确
                appendFileStorageClient.modifyFile("group1", noGroupPath, inputStream, decode.length, historyUpload);
                logger.info(uploadFileVO.getChunk() + ":更新完fastdfs");

            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error("更新远程文件出错");
                return false;
            }
        }
        //修改历史上传大小
        historyUpload = historyUpload + decode.length;
        RedisUtil.setString(Static.REDISSIZE + uploadFileVO.getFileMD5(), Convert.toStr(historyUpload));

        if (uploadFileVO.getChunk() + 1 == uploadFileVO.getChunks()) {
            //如果是最后一个块则删除redis内容
            RedisUtil.delKeys(new String[]{
                    Static.REDISSIZE + uploadFileVO.getFileMD5(),
                    Static.REDISPATH + uploadFileVO.getFileMD5()
            });

            //调用插入hbase的方法


        }


        //得到了下载的地址后，现在要插入hbase中，将fileid进行编码，返回http://10.10.0.92:8080/download? 编码后fileId
        return true;
    }


}
