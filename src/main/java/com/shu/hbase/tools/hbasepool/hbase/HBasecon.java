package com.shu.hbase.tools.hbasepool.hbase;

import com.shu.hbase.pojo.Static;
import com.shu.hbase.tools.filetype.FileTypeJudge;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HBasecon {
    private static Configuration conf = new Configuration();
    ;
    private static Connection connection;
    private static Logger logger = LoggerFactory.getLogger(FileTypeJudge.class);

    public static Connection getConnection() {
        System.setProperty("java.security.krb5.conf", Static.kerberosConf);
        conf.set("hbase.zookeeper.quorum", "bdnn2.bdg.shu.edu.cn,server1.bdg.shu.edu.cn,bdnn1.bdg.shu.edu.cn");
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hadoop.security.authentication", "kerberos");
        conf.set("hbase.security.authentication", "kerberos");
        conf.set("hbase.master.kerberos.principal", "com/shu/hbase/_HOST@BDG.SHU.EDU.CN");
        conf.set("hbase.regionserver.kerberos.principal", "com/shu/hbase/_HOST@BDG.SHU.EDU.CN");
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI("shuwebfs/service@BDG.SHU.EDU.CN", Static.keytab);
            UserGroupInformation.setLoginUser(ugi);
            connection = ConnectionFactory.createConnection(conf);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) throws IOException {
        Connection connection = getConnection();
        Admin admin = connection.getAdmin();
        //指定表名
        TableName myuser = TableName.valueOf("shuwebfs:user3");
        //添加列族
        HTableDescriptor hTableDescriptor = new HTableDescriptor(myuser);
        HColumnDescriptor f1 = new HColumnDescriptor("f1");
        HColumnDescriptor f2 = new HColumnDescriptor("f2");
        hTableDescriptor.addFamily(f1);
        hTableDescriptor.addFamily(f2);
        //创建表
        admin.createTable(hTableDescriptor);
        System.out.println("创建成功");
        //关闭连接
        admin.close();
        connection.close();
    }
}
