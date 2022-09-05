package com.easemob.mua.services;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.easemob.mua.base.CodeEnum;
import com.easemob.mua.exception.BizException;
import com.easemob.mua.mapper.ChatMapper;
import com.easemob.mua.mapper.MatchingMapper;
import com.easemob.mua.pojo.dto.rsp.UploadImgRspDto;
import com.easemob.mua.pojo.dto.rsp.UserRspDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author easemob_developer
 * @date 2022/5/26
 */

@Slf4j
@Service
public class FileService {
    @Value("${server.port}")
    private String port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${pic.dir}")
    private String picDir;

    @Value("${headimg.dir}")
    private String headimg;

    @Value("${album.dir}")
    private String album;

    @Autowired
    MatchingMapper matchingMapper;
    @Autowired
    ChatMapper chatMapper;
    @Autowired
    IUserService userService;


    /**
     * 恋爱清单附件上传
     * @param multipartFile
     * @param userId
     * @return
     */
    public String saveLoveFile(MultipartFile multipartFile,String userId) {
        if (StrUtil.isEmpty(userId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"用户ID不能为空!");
        }

        String fileName = userId+"_"+System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename();
        File file = new File(picDir + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error("save file error,{}", e.getMessage());
            throw new BizException(CodeEnum.ERROR,"图片上传失败!");
        }
        String fileUrl =  "file/" + fileName;
        log.info("fileUrl:{}", fileUrl);

        return fileUrl;
    }


    public UploadImgRspDto saveFile(MultipartFile multipartFile,String userId) {
        if (StrUtil.isEmpty(userId)) {
            throw new BizException(CodeEnum.MISS_PARAM,"用户ID不能为空!");
        }
        UserRspDto userRspDto = userService.getUserById(userId);
        if (!userRspDto.isMatching()){
            throw new BizException(CodeEnum.UN_MATCH,"该用户未匹配，不能定制欢迎页!");
        }
        String fileName = userId+"_"+System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename();
        File file = new File(picDir + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error("save file error,{}", e.getMessage());
            throw new BizException(CodeEnum.ERROR,"图片上传失败!");
        }
//        String fileUrl = getFileUrl(filename);
        String fileUrl =  "file/" + fileName;
        log.info("fileUrl:{}", fileUrl);
        matchingMapper.setSplashUrlByCode(userRspDto.getMatchingCode(),fileUrl);

        UploadImgRspDto headImgRspDto = new UploadImgRspDto();
        headImgRspDto.setSplashUrl(fileUrl);
        return headImgRspDto;
    }


    public UploadImgRspDto uploadHeadImg(MultipartFile multipartFile) {

        String fileName = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)+"_"+multipartFile.getOriginalFilename();

        File file = new File(headimg + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error("save file error,{}", e.getMessage());
            throw new BizException(CodeEnum.ERROR,"图片上传失败!");
        }
//        String fileUrl = getFileUrl(filename);
        String fileUrl =  "file/" + fileName;
        log.info("fileUrl:{}", fileUrl);
        UploadImgRspDto headImgRspDto = new UploadImgRspDto();
        headImgRspDto.setAvatar(fileUrl);
        return headImgRspDto;
    }

    public UploadImgRspDto uploadAlbum(String matchCode,MultipartFile multipartFile) {
        String fileName = matchCode+"_"+DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN)+"_"+multipartFile.getOriginalFilename();
        File file = new File(album + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            log.error("save file error,{}", e.getMessage());
            throw new BizException(CodeEnum.ERROR,"图片上传失败!");
        }
//        String fileUrl = getFileUrl(filename);
        String fileUrl =  "file/" + fileName;
        log.info("fileUrl:{}", fileUrl);
        matchingMapper.setAlbumUrlByCode(matchCode,fileUrl);
        UploadImgRspDto headImgRspDto = new UploadImgRspDto();
        headImgRspDto.setAlbumUrl(fileUrl);
        return headImgRspDto;
    }


    public List<String> getFiles() {
        List<String> fileUrls = new ArrayList<>();

        File file = new File(picDir);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    fileUrls.add(getFileUrl(file1.getName()));
                }
            }
        }
        return fileUrls;
    }

    private String getFileUrl(String fileName) {
        try {
            InetAddress address = InetAddress.getLocalHost();
//            "http://" + address.getHostAddress() + ":" + port + contextPath + "file/" + fileName;
            String fileUrl =  "file/" + fileName;
            log.info("fileUrl:{}", fileUrl);
            return fileUrl;
        } catch (UnknownHostException e) {
            log.error("get host error,{}", e.getMessage());
            throw new BizException(CodeEnum.ERROR,"图片上保存失败!");
        }
    }

    public Boolean downloadFile(HttpServletResponse response, String fileName) {
        File file = new File(picDir + fileName);
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);

                response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode(fileName, "UTF-8"));
                ServletOutputStream outputStream = response.getOutputStream();

                FileCopyUtils.copy(fileInputStream, outputStream);
                return true;
            } catch (IOException e) {
                log.error("download file error: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }
}
