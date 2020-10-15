package com.shu.hbase.pojo;

public class Static {

    public static final String keytab = "/usr/local/springboot/krb/shuwebfs.service.keytab";
    public static final String kerberosConf = "/usr/local/springboot/krb/krb5.conf";

    public static final String NAMESPACE = "shuwebfs";

    public static final String BASEURL = "/shuwebfs/";

    public static final String GROUP_TABLE = "shuwebfs:group";
    public static final String GROUP_TABLE_CF = "info";
    public static final String GROUP_TABLE_MEMBER = "member";
    public static final String GROUP_TABLE_NAME = "name";
    public static final String GROUP_TABLE_fileId = "fileId";
    public static final String GROUP_TABLE_SIZE = "size";

    public static final String INDEX_TABLE = "shuwebfs:index";
    public static final String INDEX_TABLE_CF = "info";

    //user表只用来控制单个人空间大小
    public static final String USER_TABLE = "shuwebfs:user";
    public static final String USER_TABLE_CF = "info";
    public static final String USER_TABLE_SIZE = "size";

    public static final String FILE_TABLE = "shuwebfs:files";
    public static final String FILE_TABLE_CF = "info";
    public static final String FILE_TABLE_NAME = "name";
    public static final String FILE_TABLE_Auth = "auth";
    public static final String FILE_TABLE_SIZE = "size";
    public static final String FILE_TABLE_ISDIR = "dir";
    public static final String FILE_TABLE_TIME = "time";
    public static final String FILE_TABLE_PATH = "path";
    public static final String FILE_TABLE_BACK = "back";
    public static final String FILE_TABLE_TYPE = "type";

    //fastdfs文件下载的地址
    public static final String FASTDFSADDR = "http://10.10.0.92/";

    //redis存储分片地址的key前缀
    public static final String REDISPATH = "path-";

    //redis存储分片大小的key前缀
    public static final String REDISSIZE = "size-";


}
