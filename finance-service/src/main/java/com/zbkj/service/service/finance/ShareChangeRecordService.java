package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ShareChangeRecord;
import com.zbkj.common.request.PageParamRequest;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 股份变更记录服务接口
 */
public interface ShareChangeRecordService extends IService<ShareChangeRecord> {
    
    /**
     * 分页查询股份变更记录
     * @param pageParamRequest 分页参数
     * @param keywords 关键词
     * @return 分页结果
     */
    PageInfo<ShareChangeRecord> getShareChangeRecordPage(PageParamRequest pageParamRequest, String keywords);
    
    /**
     * 根据用户ID获取变更记录
     * @param userId 用户ID
     * @return 变更记录列表
     */
    List<ShareChangeRecord> getByUserId(Integer userId);

    /**
     * 导出股份变更记录数据
     * @param keywords 关键词
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    void exportShareChangeRecordData(String keywords, HttpServletResponse response) throws Exception;

    List<ShareChangeRecord> shareChangeList();
} 