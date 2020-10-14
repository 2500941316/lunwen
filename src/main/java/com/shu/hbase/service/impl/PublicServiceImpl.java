package com.shu.hbase.service.impl;

import com.shu.hbase.pojo.Static;
import com.shu.hbase.service.impl.downLoad.DownLoad;
import com.shu.hbase.service.impl.upload.MvcToHadoop;
import com.shu.hbase.service.interfaces.PublicService;
import com.shu.hbase.tools.TableModel;
import com.shu.hbase.tools.hbasepool.HbaseConnectionPool;
import com.shu.hbase.tools.hdfspool.HdfsConnectionPool;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;


@Service
public class PublicServiceImpl implements PublicService {

    @Autowired
    MvcToHadoop mvcToHadoop;

    private Logger logger = LoggerFactory.getLogger(PublicServiceImpl.class);


    //接收上传的文件到后端服务器
    @Override
    public TableModel uploadTomvc(MultipartFile file, Integer chunk, Integer chunks, String uid, HttpServletRequest request, String backId) {
        logger.info("接收到上传的文件，开始执行上传逻辑");
        //获取项目的根路径
        String realpath = request.getSession().getServletContext().getRealPath("/");
        String fileId = uid + "_" + System.currentTimeMillis();
        //截取上传文件的类型
        String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        File video = new File(realpath + "final" + uid + "/" + file.getOriginalFilename());
        logger.info("截取上传文件类型成功");
        //先判断文件的父目录是否存在，不存在需要创建；否则报错
        try {
            if (!video.getParentFile().exists()) {
                video.getParentFile().mkdirs();
                video.createNewFile();//创建文件
            }
            if (chunk == null && chunks == null) {//没有分片 直接保存
                logger.info("文件没有分片，直接保存");
                file.transferTo(video);
                return mvcToHadoop.createFile(realpath + "final" + uid + "/" + file.getOriginalFilename(), backId, fileId, uid);
            } else {
                logger.info("文件分片，新建临时保存文件夹");
                //根据guid 创建一个临时的文件夹
                File file2 = new File(realpath + "/" + uid + "/" + file.getOriginalFilename() + "/" + chunk + fileType);
                if (!file2.exists()) {
                    file2.mkdirs();
                }

                //保存每一个分片
                file.transferTo(file2);

                //如果当前是最后一个分片，则合并所有文件
                if (chunk == (chunks - 1)) {
                    logger.info("开始保存最后一个分片");
                    File tempFiles = new File(realpath + "/" + uid + "/" + file.getOriginalFilename());
                    File[] files = tempFiles.listFiles();
                    while (true) {
                        assert files != null;
                        if (files.length == chunks) {
                            break;
                        }
                        Thread.sleep(300);
                        files = tempFiles.listFiles();
                    }
                    FileOutputStream fileOutputStream = null;
                    BufferedOutputStream bufferedOutputStream = null;
                    BufferedInputStream inputStream = null;
                    try {
                        logger.info("开始进行文件写入的准备工作");
                        //创建流
                        fileOutputStream = new FileOutputStream(video, true);
                        //创建文件输入缓冲流
                        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                        byte[] buffer = new byte[4096];//一次读取1024个字节
                        //对这个文件数组进行排序
                        logger.info("对文件数组进行排序");
                        Arrays.sort(files, new Comparator<File>() {
                                    @Override
                                    public int compare(File o1, File o2) {
                                        int o1Index = Integer.parseInt(o1.getName().split("\\.")[0]);
                                        int o2Index = Integer.parseInt(o2.getName().split("\\.")[0]);
                                        return Integer.compare(o1Index, o2Index);
                                    }
                                }
                        );
                        logger.info("开始将分片的文件合成为一个主文件");
                        for (File fileTemp : files) {
                            inputStream = new BufferedInputStream(new FileInputStream(fileTemp));
                            int readcount;
                            while ((readcount = inputStream.read(buffer)) > 0) {
                                bufferedOutputStream.write(buffer, 0, readcount);
                                bufferedOutputStream.flush();
                            }
                            inputStream.close();
                        }
                        bufferedOutputStream.close();
                        logger.info("分片文件合成成功！");
                        logger.info("开始写入hdfs");
                        return mvcToHadoop.createFile(realpath + "final" + uid + "/" + file.getOriginalFilename(), backId, fileId, uid);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e.getMessage());
                        return TableModel.error("上传失败");
                    } finally {
                        for (File value : files) {
                            value.delete();
                        }
                        tempFiles.delete();
                        assert inputStream != null;
                        inputStream.close();
                        fileOutputStream.close();
                        bufferedOutputStream.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
        return TableModel.error("网络异常");
    }

    @Override
    public void downLoad(String fileId, String gId, HttpServletResponse response, HttpServletRequest request, String uid) {
        //从files表中查询出下载文件的物理地址，然后调用下载函数
        Connection hBaseConn = null;
        FileSystem fs = null;
        Table fileTable = null;
        try {
            hBaseConn = HbaseConnectionPool.getHbaseConnection();
            fs = HdfsConnectionPool.getHdfsConnection();
            fileTable = hBaseConn.getTable(TableName.valueOf(Static.FILE_TABLE));
            logger.info("开始验证用户下载权限");
            //权限验证
            if (!CrudMethods.verifite(fileTable, uid, fileId, gId)) {
                logger.info("权限校验失败");
                return;
            }
            logger.info("用户下载权限校验成功");
            Get get = new Get(Bytes.toBytes(fileId));
            get.addFamily(Bytes.toBytes(Static.FILE_TABLE_CF));
            Result result = fileTable.get(get);
            String fileName = "";
            String path = "";
            logger.info("开始根据文件id查询文件存储路径");
            if (!result.isEmpty()) {
                for (Cell cell : result.rawCells()) {
                    if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals(Static.FILE_TABLE_NAME)) {
                        fileName = Bytes.toString(CellUtil.cloneValue(cell));
                    } else if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals(Static.FILE_TABLE_PATH)) {
                        path = Bytes.toString(CellUtil.cloneValue(cell));
                    }
                }
                logger.info("调用下载方法,下载文件：" + fileName);
                DownLoad.downloadFromHDFSinOffset(fs, response, path, fileName, request);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            try {
                assert fileTable != null;
                fileTable.close();
                HbaseConnectionPool.releaseConnection(hBaseConn);
                HdfsConnectionPool.releaseConnection(fs);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }
    }
}
