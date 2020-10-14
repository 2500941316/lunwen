package com.shu.hbase.tools.hbasepool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.hbase.client.Connection;

public class ConnectionPoolConfig extends GenericObjectPool<Connection> {
    public ConnectionPoolConfig(PooledObjectFactory<Connection> factory) {
        super(factory);
    }

    public ConnectionPoolConfig(PooledObjectFactory<Connection> factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

    public ConnectionPoolConfig(PooledObjectFactory<Connection> factory, GenericObjectPoolConfig config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
