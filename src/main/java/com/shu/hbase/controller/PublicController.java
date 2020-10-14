package com.shu.hbase.controller;

import com.shu.hbase.service.interfaces.PublicService;
import com.shu.hbase.tools.TableModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;


@RestController
@CrossOrigin
public class PublicController {
    private String username = "19721631";

    @Autowired
    PublicService publicService;


    /**
     * 下载文件api
     *
     * @param
     * @throws IOException
     */
    @ApiOperation(value="web下载接口")
    @GetMapping("downLoad")
    public void downLoad(@RequestParam String fileId, String gId, HttpServletResponse response, HttpServletRequest request, Principal principal) {
        if (principal != null) {
            username = principal.getName();
        }
        publicService.downLoad(fileId, gId, response, request, username);
    }


    /**
     * 上传文件到mvc后端
     *
     * @param file
     * @param chunk
     * @param chunks
     * @param request
     */
    @ApiOperation(value="上传文件到后端服务器")
    @PostMapping("uploadToBacken")
    public TableModel uploadTomvc(@RequestParam MultipartFile file, Integer chunk, Integer chunks, String backId, HttpServletRequest request, Authentication authentication) {
        if (file.isEmpty()) {
            return TableModel.error("参数为空");
        }
        return publicService.uploadTomvc(file, chunk, chunks, username, request, backId);
    }

}
