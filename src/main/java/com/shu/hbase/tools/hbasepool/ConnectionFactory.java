package com.shu.hbase.tools.hbasepool;


import com.shu.hbase.tools.hbasepool.hbase.HBasecon;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.hadoop.hbase.client.Connection;


public class ConnectionFactory extends BasePooledObjectFactory<Connection>
{

    //创建一个新的对象放入池中
    @Override
    public Connection create()  {
        //创建对象
        return HBasecon.getConnection();
    }

    //用PooledObject封装对象放入池中
    @Override
    public PooledObject<Connection> wrap(Connection connection) {
        //包装实际对象
        return new DefaultPooledObject<>(connection);
    }
}