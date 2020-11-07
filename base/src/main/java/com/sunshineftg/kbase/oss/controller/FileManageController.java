package com.sunshineftg.kbase.oss.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunshineftg.kbase.oss.entity.FileEntity;
import com.sunshineftg.kbase.oss.service.FileManageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 文件
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileManageController {

	private final FileManageService fileManageService;

	/**
	 * 分页查询
	 * @param page 分页对象
	 * @param fileEntity 文件管理
	 * @return
	 */
	@GetMapping("/page")
	public R getSysFilePage(Page page, FileEntity fileEntity) {
		return R.ok(fileManageService.page(page, Wrappers.query(fileEntity)));
	}

	/**
	 * 通过id删除文件管理
	 * @param id id
	 * @return R
	 */
	@DeleteMapping("/{id}")
	public Object removeById(@PathVariable Long id) {
		return R.ok(fileManageService.deleteFile(id));
	}

	/**
	 * 上传文件 文件名采用uuid,避免原始文件名中带"-"符号导致下载的时候解析出现异常
	 * @param file 资源
	 * @return R(/ file / bucketName / filename)
	 */
	@PostMapping("/upload")
	public Object upload(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "bucketName", required = false) String bucketName,
                         @RequestParam(value = "objectName", required = false) String objectName) {
		return fileManageService.uploadFile(file, bucketName, objectName);
	}

	/**
	 * 获取文件
	 * @param bucket 桶名称
	 * @param fileName 文件空间/名称
	 * @param response
	 * @return
	 */
	@GetMapping("/{bucket}/{fileName}")
	public void file(@PathVariable String bucket, @PathVariable String fileName, HttpServletResponse response) {
		fileManageService.getFile(bucket, fileName, response);
	}

    /**
     * 获取文件
     * @return
     */
    @GetMapping("/{bucketName}/{fileName}/{expires}")
    public Object file(@PathVariable String bucketName,
                     @PathVariable String fileName,
                     @PathVariable Integer expires) {
        return fileManageService.getFile(bucketName, fileName, expires);
    }

    /**
     * 获取文件数据
     * @param bucketName 桶名称
     * @param fileName 文件空间/名称
     * @return
     */
    @GetMapping("/info/{bucketName}/{fileName}")
    public Object get(@PathVariable("bucketName") String bucketName, @PathVariable("fileName") String fileName) {
        return fileManageService.getFileEntity(bucketName, fileName);
    }
}
