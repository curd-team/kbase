package com.sunshineftg.kbase.oss.service.impl;

import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunshineftg.kbase.oss.config.OssProperties;
import com.sunshineftg.kbase.oss.config.OssTemplate;
import com.sunshineftg.kbase.oss.entity.BucketEntity;
import com.sunshineftg.kbase.oss.mapper.BucketMapper;
import com.sunshineftg.kbase.oss.service.BucketManageService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 桶管理
 */
@Slf4j
@Service
@AllArgsConstructor
public class BucketManageManageServiceImpl extends ServiceImpl<BucketMapper, BucketEntity> implements BucketManageService {

	private final OssProperties ossProperties;

	private final OssTemplate ossTemplate;

    @Override
    @SneakyThrows
    public R create(String bucketName) {
        try {
            ossTemplate.createBucket(bucketName);
            // 收集管理追踪文件
            bucketLog(bucketName);
        }
        catch (Exception e) {
            log.error("创建失败", e);
            return R.failed(e.getLocalizedMessage());
        }
        return R.ok(ossTemplate.getBucket(bucketName).get());
    }

    /**
     * 删除桶
     * @param id
     * @return
     */
    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBucket(Long id) {
        BucketEntity bucketEntity = this.getById(id);
        if (bucketEntity == null) {
            return false;
        }
        ossTemplate.removeBucket(bucketEntity.getBucketName());
        return this.removeById(id);
    }

    @Override
    public R getBucket(Long id) {
        BucketEntity bucketEntity = this.getById(id);
        return R.ok(bucketEntity);
    }

    @Override
    public R getBucket(String bucketName) {
        return R.ok(ossTemplate.getBucket(bucketName).get());
    }

    /**
     * 桶管理数据记录,收集管理追踪文件
     */
    private void bucketLog(String bucketName) {
        BucketEntity bucketEntity = new BucketEntity();
        bucketEntity.setBucketName(bucketName);
        // TODO
        bucketEntity.setCreateUser("admin");
        this.save(bucketEntity);
    }
}
