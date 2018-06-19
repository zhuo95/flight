package com.zz.flight.service.impl;

import com.google.common.collect.Lists;
import com.zz.flight.service.FileService;
import com.zz.flight.util.FTPUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service("FileService")
public class FileServiceImpl implements FileService {
    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        //获取扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".")+1);
        String uploadFileName = UUID.randomUUID().toString() + "." + fileExtensionName;

        //创建路径
        File fileDir = new File(path);
        if(!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        //创建文件
        File targetFile = new File(path,uploadFileName);
        //把springmvc 封装的MultiFile 传入到targetfile
        try {
            // 把上传文件放到targetFile
            file.transferTo(targetFile);
            //再传到ftp服务器
            FTPUtil.uploadFile(Lists.newArrayList(targetFile));
            //上传完毕后删除upload下的文件
            targetFile.delete();
        } catch (IOException e) {
            return null;
        }
        return targetFile.getName();
    }

}
