package com.shu.hbase.tools.hdfspool;

import com.shu.hbase.tools.hbasepool.HbaseConnectionPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public  class HdfsConnectionPool {
    private static Logger logger = LoggerFactory.getLogger(HbaseConnectionPool.class);
    private static ConnectionFactory factory;
    private static ConnectionPoolConfig connectionPool;
    private static GenericObjectPoolConfig config;

    static {
        factory = new ConnectionFactory();
        config = new GenericObjectPoolConfig();
        config.setMaxTotal(20);
        //最小空闲数
        config.setMinIdle(2);
        //最大空闲数
        config.setMaxIdle(8);
        config.setMaxWaitMillis(10000);
        connectionPool = new ConnectionPoolConfig(factory, config);
    }

    public synchronized static FileSystem getHdfsConnection() throws Exception {
        try {
            logger.info("归还了Hdfs连接"+connectionPool.getReturnedCount());
            logger.info("借出了Hdfs连接"+connectionPool.getBorrowedCount());
            return  connectionPool.borrowObject();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
        }
    }

    public static void releaseConnection(FileSystem fileSystem) throws Exception {
        try {
            logger.info("归还了Hdfs连接");
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        } finally {
                connectionPool.returnObject(fileSystem);
        }
    }
}
