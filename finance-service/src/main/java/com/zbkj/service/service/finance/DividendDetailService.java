package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.finance.DividendDetail;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 分红明细服务接口
 */
public interface DividendDetailService extends IService<DividendDetail> {
    
    /**
     * 根据分红ID获取明细列表
     * @param dividendId 分红ID
     * @return 明细列表
     */
    List<DividendDetail> getByDividendId(String dividendId);
    
    /**
     * 根据用户ID获取分红明细
     * @param userId 用户ID
     * @return 分红明细列表
     */
    List<DividendDetail> getByUserId(Integer userId);

    /**
     * 导出分红明细数据
     * @param dividendId 分红ID
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    void exportDividendDetailData(String dividendId, HttpServletResponse response) throws Exception;
} 