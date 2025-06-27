package com.zbkj.pc.controller;

import com.zbkj.common.result.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * PC端测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/pc/test")
@Api(tags = "PC端测试接口")
public class TestController {

    @ApiOperation("测试连接")
    @GetMapping("/ping")
    public CommonResult<Map<String, Object>> ping() {
        log.info("PC端API测试请求");
        Map<String, Object> result = new HashMap<>();
        result.put("message", "PC端API连接成功");
        result.put("timestamp", System.currentTimeMillis());
        result.put("server", "finance-pc-20002");
        return CommonResult.success(result);
    }

    @ApiOperation("获取服务状态")
    @GetMapping("/status")
    public CommonResult<Map<String, Object>> status() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "running");
        result.put("service", "finance-pc");
        result.put("port", 20002);
        result.put("version", "1.0.0");
        return CommonResult.success(result);
    }
} 