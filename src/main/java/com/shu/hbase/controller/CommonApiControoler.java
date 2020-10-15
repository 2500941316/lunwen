package com.shu.hbase.controller;

import com.shu.hbase.pojo.UploadFileVO;
import com.shu.hbase.service.interfaces.CommonApiService;
import com.shu.hbase.tools.TableModel;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class CommonApiControoler {

    @Autowired
    CommonApiService commonApiService;

    @ApiOperation(value = "上传文件到服务器")
    @PostMapping("upload")
    public String uploadToMvc(@RequestBody UploadFileVO uploadFileVO) {

        return commonApiService.upload(uploadFileVO);
    }

}
