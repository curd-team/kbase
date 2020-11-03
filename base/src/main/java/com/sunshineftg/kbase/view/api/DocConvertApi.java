package com.sunshineftg.kbase.view.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "file-online-preview",url = "${zuul.routes.file-online-preview.url}")
public interface DocConvertApi {


    /**
     * 添加到转换服务队列中
     * @param url 文件下载url
     */
    @GetMapping("/addTask")
    void addPreviewTask(@RequestParam(value = "url") String url);

}
