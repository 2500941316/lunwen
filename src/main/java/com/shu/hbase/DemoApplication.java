package com.shu.hbase;

import com.github.tobato.fastdfs.FdfsClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.context.annotation.Import;
import org.springframework.jmx.support.RegistrationPolicy;
@ServletComponentScan
@SpringBootApplication
@Import(FdfsClientConfig.class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
public class DemoApplication {
    private static Logger logger = LoggerFactory.getLogger(DemoApplication.class);

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore",
                "/usr/local/springboot/krb/BDGRootCA.truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "shu");
        SpringApplication.run(DemoApplication.class, args);

    }



}
