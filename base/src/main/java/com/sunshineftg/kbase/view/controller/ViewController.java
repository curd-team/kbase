package com.sunshineftg.kbase.view.controller;


import com.baomidou.mybatisplus.extension.api.R;
import com.sunshineftg.kbase.view.api.DocConvertApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("viewTask")
@RestController
@Slf4j
public class ViewController {

    @Resource
    private DocConvertApi docConvertApi;

    @GetMapping("addViewTask")
    public R<String> addViewTask(String url){
        docConvertApi.addPreviewTask(url);
        return R.ok(null);
    }
}
