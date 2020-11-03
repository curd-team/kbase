package com.sunshineftg.kbase.oss.service;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sunshineftg.kbase.oss.entity.BucketEntity;

/**
 * 桶管理
 */
public interface BucketManageService extends IService<BucketEntity> {

    /**
     * 创建桶
     * @return
     */
    R create(String bucketName);

    R getBucket(Long id);

    R getBucket(String bucketName);

    /**
     * 删除桶
     * @param id
     * @return
     */
    Boolean deleteBucket(Long id);

}
