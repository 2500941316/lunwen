package com.shu.hbase.tools.hdfspool;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.hadoop.fs.FileSystem;

public class ConnectionPoolConfig extends GenericObjectPool<FileSystem> {
    public ConnectionPoolConfig(PooledObjectFactory<FileSystem> factory) {
        super(factory);
    }

    public ConnectionPoolConfig(PooledObjectFactory<FileSystem> factory, GenericObjectPoolConfig config) {
        super(factory, config);
    }

    public ConnectionPoolConfig(PooledObjectFactory<FileSystem> factory, GenericObjectPoolConfig config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
