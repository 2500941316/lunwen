package com.shu.hbase.service.impl;

import com.shu.hbase.pojo.Static;
import com.shu.hbase.tools.hbasepool.HbaseConnectionPool;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrudMethods {
    private static Logger logger = LoggerFactory.getLogger(CrudMethods.class);

    //根据fileId和用户来校验权限
    static boolean verifite(Table fileTable, String authId, String filedId, String gId) throws IOException {
        //通过fileTable查出该文件的权限信息
        //如果fileId为8位，则说明在查首页,只有本人能查到
        if ((filedId.equals(authId)) || filedId.substring(0, 8).equals("00000000")) {
            return true;
        } else if (filedId.length() == 8) {
            logger.info("无法访问其他用户的根目录，权限验证失败");
            return false;
        } else {
            Get get = new Get(Bytes.toBytes(filedId));
            get.setMaxVersions();
            get.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_Auth));
            Result result = fileTable.get(get);
            List<Cell> cells = result.listCells();
            String newAuthId = null;
            if (!gId.isEmpty()) {
                logger.info("gId不为空，开始组合新的AuthId进行验证");
                newAuthId = gId + authId;
            }
            logger.info("开始查找文件权限，进行循环比对");
            if (cells == null) {
                logger.info("权限列表为空，文件可能不存在，权限验证失败");
                return false;
            }
            for (Cell cell : cells) {
                if (Bytes.toString(CellUtil.cloneValue(cell)).equals(newAuthId) || Bytes.toString(CellUtil.cloneValue(cell)).equals(authId) || Bytes.toString(CellUtil.cloneValue(cell)).equals("公开")) {
                    logger.info("权限比对成功，返回true");
                    return true;
                }
            }
        }
        logger.info("权限比对失败，返回false");
        return false;
    }

    //根据file表的文件id来查找，文件对应的物理路径
    public static String findUploadPath(String backId) {
        Connection hBaseConn = null;
        Table fileTable = null;
        String path = null;
        if (backId.length() == 8) {
            path = Static.BASEURL + backId;
        } else {
            try {
                hBaseConn = HbaseConnectionPool.getHbaseConnection();
                fileTable = hBaseConn.getTable(TableName.valueOf(Static.FILE_TABLE));
                //根据gid查询每个组的文件
                Get get = new Get(Bytes.toBytes(backId));
                get.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_PATH));
                Result result = fileTable.get(get);
                Cell cell = result.rawCells()[0];
                path = Bytes.toString(CellUtil.cloneValue(cell));

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    assert fileTable != null;
                    fileTable.close();
                    HbaseConnectionPool.releaseConnection(hBaseConn);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return path;
    }


    //将上传的文件名和新建的文件夹 checkTable方法中新建的文件插入files表中
    public static boolean insertToFiles(File localPath, String fileType, String hdfsPath, String backId, String uId, String fileId) {
        Connection hBaseConn = null;
        Table fileTable = null;
        if (!backId.substring(0, 8).equals(uId)) {
            return false;
        }
        try {
            hBaseConn = HbaseConnectionPool.getHbaseConnection();
            fileTable = hBaseConn.getTable(TableName.valueOf(Static.FILE_TABLE));

            long l = System.currentTimeMillis();
            Put put = new Put(Bytes.toBytes(fileId));
            if (localPath != null) {
                put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_NAME), Bytes.toBytes(localPath.getName()));
                put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_SIZE), Bytes.toBytes(String.valueOf(localPath.length())));
                put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_ISDIR), Bytes.toBytes("false"));
            } else {
                String newPath = hdfsPath;
                newPath = newPath.substring(newPath.lastIndexOf("/") + 1);
                put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_NAME), Bytes.toBytes(newPath));
                put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_SIZE), Bytes.toBytes("-"));
                put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_ISDIR), Bytes.toBytes("true"));
            }
            //如果是首页的数据则back设为/+学号；如果不是首页的数据则back设为当前文件夹的id号
            put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_TYPE), Bytes.toBytes(fileType));
            put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_BACK), Bytes.toBytes(backId));
            put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_PATH), Bytes.toBytes(hdfsPath));

            //如果当前的backid不等于uid，说明在一个文件夹下面上传文件，则先查询文件夹的权限，然后等于该文件夹的权限
            if (!backId.equals(uId)) {
                //查询fileId是backId的文件夹的权限
                Get authGet = new Get(Bytes.toBytes(backId));
                authGet.setMaxVersions();
                authGet.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_Auth));
                Result authResult = fileTable.get(authGet);
                List<String> authList = new ArrayList<>();
                if (!authResult.isEmpty()) {
                    for (Cell cell : authResult.rawCells()) {
                        if (Bytes.toString(CellUtil.cloneQualifier(cell)).equals(Static.FILE_TABLE_Auth)) {
                            authList.add(Bytes.toString(CellUtil.cloneValue(cell)));
                        }
                    }
                }
                for (String auth : authList) {
                    put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_Auth), ++l, Bytes.toBytes(auth));
                }
            } else {
                put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_Auth), Bytes.toBytes(uId));
            }

            put.addColumn(Bytes.toBytes(Static.FILE_TABLE_CF), Bytes.toBytes(Static.FILE_TABLE_TIME), Bytes.toBytes(l + ""));
            fileTable.put(put);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HbaseConnectionPool.releaseConnection(hBaseConn);
        }
        return true;
    }



}
