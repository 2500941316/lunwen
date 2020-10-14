package com.shu.hbase.tools.hbasepool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.hbase.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HbaseConnectionPool {
    private static Logger logger = LoggerFactory.getLogger(HbaseConnectionPool.class);
    private static ConnectionFactory factory;
    private static ConnectionPoolConfig connectionPool;
    private static GenericObjectPoolConfig config;

    static {
        factory = new ConnectionFactory();
        config = new GenericObjectPoolConfig();
        config.setMaxTotal(30);
        //最小空闲数
        config.setMinIdle(2);
        config.setTimeBetweenEvictionRunsMillis(60000);
        //最大空闲数
        config.setMaxIdle(8);
        config.setMaxWaitMillis(10000);
        connectionPool = new ConnectionPoolConfig(factory, config);
    }

    public synchronized static Connection getHbaseConnection() throws Exception {
        try {
            logger.info("归还HBase连接了"+connectionPool.getReturnedCount());
            logger.info("借出HBase连接了"+connectionPool.getBorrowedCount());
            return connectionPool.borrowObject();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public static void releaseConnection(Connection connection) {
        try {
            logger.info("归还了HBase连接");
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            connectionPool.returnObject(connection);
        }
    }
}
