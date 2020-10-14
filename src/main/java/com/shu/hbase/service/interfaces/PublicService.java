package com.shu.hbase.service.interfaces;

import com.shu.hbase.tools.TableModel;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface PublicService {

    TableModel uploadTomvc(MultipartFile file, Integer chunk, Integer chunks, String username, HttpServletRequest request, String backId);

    void downLoad(String fileId, String gId, HttpServletResponse response, HttpServletRequest request, String username);
}
