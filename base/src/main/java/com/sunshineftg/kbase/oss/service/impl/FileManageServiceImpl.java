package com.sunshineftg.kbase.oss.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.services.s3.model.S3Object;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunshineftg.kbase.oss.config.OssProperties;
import com.sunshineftg.kbase.oss.config.OssTemplate;
import com.sunshineftg.kbase.oss.entity.FileEntity;
import com.sunshineftg.kbase.oss.mapper.FileMapper;
import com.sunshineftg.kbase.oss.service.FileManageService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件管理
 */
@Slf4j
@Service
@AllArgsConstructor
public class FileManageServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileManageService {

	private final OssProperties ossProperties;

	private final OssTemplate ossTemplate;

    @Override
    public R uploadFile(MultipartFile file, String bucketName, String objectName) {
        String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());
        String bucketFileName = StrUtil.isBlank(objectName) ? fileName : objectName;
        bucketName = StrUtil.isBlank(bucketName) ? ossProperties.getBucketName() : bucketName;
        Map<String, String> resultMap = new HashMap<>(4);
		resultMap.put("bucketName", bucketName);
		resultMap.put("fileName", bucketFileName);
		resultMap.put("url", String.format("/file/%s/%s", bucketName, fileName));

		try {
			ossTemplate.putObject(bucketName, bucketFileName, file.getInputStream());
			// 文件管理数据记录,收集管理追踪文件
			fileLog(file, bucketName, fileName);
		}
		catch (Exception e) {
			log.error("上传失败", e);
			return R.failed(e.getLocalizedMessage());
		}
		return R.ok(resultMap);
	}

	/**
	 * 读取文件
	 * @param bucket
	 * @param fileName
	 * @param response
	 */
	@Override
	public void getFile(String bucket, String fileName, HttpServletResponse response) {
		try (S3Object s3Object = ossTemplate.getObject(bucket, fileName)) {
			response.setContentType("application/octet-stream; charset=UTF-8");
			response.setHeader("Content-Disposition","attachment;filename="+fileName);
			IoUtil.copy(s3Object.getObjectContent(), response.getOutputStream());
		}
		catch (Exception e) {
			log.error("文件读取异常: {}", e.getLocalizedMessage());
		}
	}

	/**
	 * 删除文件
	 * @param id
	 * @return
	 */
	@Override
	@SneakyThrows
	@Transactional(rollbackFor = Exception.class)
	public Boolean deleteFile(Long id) {
		FileEntity fileEntity = this.getById(id);
		if (fileEntity == null) {
		    return false;
        }
		ossTemplate.removeObject(fileEntity.getBucketName(), fileEntity.getFileName());
		return this.removeById(id);
	}

    @Override
    @SneakyThrows
    public R getFile(String bucket, String fileName, Integer expires) {
        Map<String, Object> responseBody = new HashMap<>(8);
        // Put Object info
        responseBody.put("bucket", bucket);
        responseBody.put("object", fileName);
        responseBody.put("url", ossTemplate.getObjectURL(bucket, fileName, expires));
        responseBody.put("expires", expires);
        return R.ok(responseBody);
    }

    /**
	 * 文件管理数据记录,收集管理追踪文件
	 * @param file 上传文件格式
	 * @param fileName 文件名
	 */
	private void fileLog(MultipartFile file, String bucketName, String fileName) {
		FileEntity fileEntity = new FileEntity();
		// 原文件名
		String original = file.getOriginalFilename();
		fileEntity.setFileName(fileName);
		fileEntity.setOriginal(original);
		fileEntity.setFileSize(file.getSize());
		fileEntity.setType(FileUtil.extName(original));
		fileEntity.setBucketName(bucketName);
		// TODO
		fileEntity.setCreateUser("admin");
		this.save(fileEntity);
	}

}
