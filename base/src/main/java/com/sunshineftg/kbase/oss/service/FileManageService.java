package com.sunshineftg.kbase.oss.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunshineftg.kbase.oss.entity.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件管理
 */
public interface FileManageService extends IService<FileEntity> {

	/**
	 * 上传文件
	 * @param file
	 * @return
	 */
	R uploadFile(MultipartFile file, String bucketName, String objectName);

	/**
	 * 读取文件
	 * @param bucket 桶名称
	 * @param fileName 文件名称
	 * @param response 输出流
	 */
	void getFile(String bucket, String fileName, HttpServletResponse response);

    /**
     * 获取文件数据
     * @param bucket 桶名称
     * @param fileName 文件名称
     */
    R getFileEntity(String bucket, String fileName);

    /**
     * 获取分享链接
     * @param bucket 桶名称
     * @param fileName 文件名称
     */
    R getFile(String bucket, String fileName, Integer expires);

	/**
	 * 删除文件
	 * @param id
	 * @return
	 */
	Boolean deleteFile(Long id);

}
