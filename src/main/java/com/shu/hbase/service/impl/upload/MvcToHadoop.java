package com.shu.hbase.service.impl.upload;

import com.shu.hbase.pojo.Static;
import com.shu.hbase.service.impl.CrudMethods;
import com.shu.hbase.tools.TableModel;
import com.shu.hbase.tools.filetype.FileType;
import com.shu.hbase.tools.filetype.FileTypeJudge;
import com.shu.hbase.tools.hbasepool.HbaseConnectionPool;
import com.shu.hbase.tools.hdfspool.HdfsConnectionPool;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Component
public class MvcToHadoop {

    @Autowired
    public MvcToFastDfs mvcToFastDfs;

    private static Logger logger = LoggerFactory.getLogger(MvcToHadoop.class);

    public TableModel createFile(String mvcPath, String backId, String fileId, String uId) throws Exception {
        FileSystem fs = null;
        Connection hBaseConn = null;
        Table userTable = null;
        FSDataOutputStream out = null;
        InputStream typeIn = null;
        InputStream in = null;
        String hdfsPath = null;
        File localPath = null;
        try {
            logger.info("开始将文件持久化写入");
            fs = HdfsConnectionPool.getHdfsConnection();
            hBaseConn = HbaseConnectionPool.getHbaseConnection();
            userTable = hBaseConn.getTable(TableName.valueOf(Static.USER_TABLE));
            //权限验证：上传只能自己给自己上传，backId的前8位必须等于uId
            if (!backId.substring(0, 8).equals(uId)) {
                logger.error("权限不足，无法写入");
                return TableModel.error("您的权限不足");
            }
            localPath = new File(mvcPath);
            in = new FileInputStream(localPath);
            typeIn = new FileInputStream(localPath);
            FileType type = FileTypeJudge.getType(typeIn);
            String fileType = FileTypeJudge.isFileType(type);
            //做二次判断，如果还是"其他",则对文件后缀再次判断
            String[] videoTypes = new String[]{"avi", "ram", "rm", "mpg", "mov", "asf", "mp4", "flv", "mid"};
            if (fileType.equals("other")) {
                int i = localPath.getName().lastIndexOf(".");
                for (String videoType : videoTypes) {
                    if (videoType.equals(localPath.getName().substring(i + 1))) {
                        fileType = "video";
                    }
                }
            }

            /**
             * 如果文件的大小小于128M,则存入fastdfs中
             */
            if (localPath.length() < 128 * 1024 * 1024) {
                logger.info("文件大小小于128M,存入fastdfs");
                hdfsPath = Static.FASTDFSADDR + mvcToFastDfs.uploadFile(in, localPath.getName());
                logger.info("文件上传到fastdfs成功");
            } else {
                //查询hdfs的路径
                logger.info("查找并拼接hdfs的物理地址");
                hdfsPath = CrudMethods.findUploadPath(backId);
                if (hdfsPath != null) {
                    hdfsPath = hdfsPath + "/" + localPath.getName();
                }
                out = fs.create(new Path(hdfsPath));
                IOUtils.copyBytes(in, out, 4096, true);
                out.close();
                logger.info("文件上传到hdfs成功");
            }
            logger.info("将文件插入到hbase表格中");
            CrudMethods.insertToFiles(localPath, fileType, hdfsPath, backId, uId, fileId);
            logger.info("方法返回");
            return TableModel.success("上传成功");
        } catch (Exception e) {
            logger.info(e.getMessage());
            return TableModel.error("上传失败");
        } finally {
            localPath.delete();
            userTable.close();
            HbaseConnectionPool.releaseConnection(hBaseConn);
            HdfsConnectionPool.releaseConnection(fs);
        }
    }
}
