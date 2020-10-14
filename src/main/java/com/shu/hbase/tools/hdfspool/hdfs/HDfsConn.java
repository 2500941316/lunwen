package com.shu.hbase.tools.hdfspool.hdfs;

import com.shu.hbase.pojo.Static;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.IOException;

public class HDfsConn {
    private static Configuration conf = new Configuration();

    public static FileSystem getConnection() throws IOException {
        System.setProperty("java.security.krb5.conf", Static.kerberosConf);
        UserGroupInformation.createRemoteUser("shuwebfs");
        conf = new Configuration();
        conf.set("hadoop.job.ugi", "shuwebfs");

        conf.set("fs.defaultFS","hdfs://bdg-proto-nameservice");
        Configuration conf = new Configuration();
        conf.set("hadoop.security.authentication", "kerberos");

        String user = "shuwebfs/service@BDG.SHU.EDU.CN";
        String keyTab = Static.keytab;
        UserGroupInformation.setConfiguration(conf);
        try {
            UserGroupInformation.loginUserFromKeytab(user, keyTab);
        } catch (IOException e) {
        }
        return FileSystem.get(conf);
    }
}
