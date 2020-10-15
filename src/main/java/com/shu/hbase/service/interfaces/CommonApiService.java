package com.shu.hbase.service.interfaces;

import com.shu.hbase.pojo.UploadFileVO;
import com.shu.hbase.tools.TableModel;

public interface CommonApiService {

    String upload(UploadFileVO uploadFileVO);

}
