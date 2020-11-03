package com.sunshineftg.kbase.oss.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunshineftg.kbase.oss.entity.BucketEntity;
import com.sunshineftg.kbase.oss.service.BucketManageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * oss
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bucket")
public class BucketManageController {

    private final BucketManageService bucketManageService;

    @GetMapping("/page")
    public R getBucketPage(Page page, BucketEntity bucketEntity) {
        return R.ok(bucketManageService.page(page, Wrappers.query(bucketEntity)));
    }

    @PostMapping("/{bucketName}")
    public Object create(@PathVariable String bucketName) {
	    return bucketManageService.create(bucketName);
    }

    @DeleteMapping("/{id}")
    public Object deleteBucketId(@PathVariable Long id) {
        return R.ok(bucketManageService.deleteBucket(id));
    }

    @GetMapping("/{id}")
    public Object bucket(@PathVariable Long id) {
        return bucketManageService.getBucket(id);
    }

    /*
    @GetMapping("/{bucketName}")
    public Object bucket(@PathVariable String bucketName) {
        return bucketManageService.getBucket(bucketName);
    }
     */
}
