package com.zbkj.service.service.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ActiveTransaction;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.vo.finance.*;

import java.util.List;

/**
 * 活体交易记录服务接口
 */
public interface ActiveTransactionService extends IService<ActiveTransaction> {
    
    /**
     * 同步进场数据到交易记录
     * @param farmCode 养殖场编码
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 是否成功
     */
    Boolean syncEntryDataToTransaction(String farmCode, String startDate, String endDate);
    
    /**
     * 同步离场数据到交易记录
     * @param farmCode 养殖场编码
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 是否成功
     */
    Boolean syncExitDataToTransaction(String farmCode, String startDate, String endDate);
    
    /**
     * 分页查询活体交易记录
     * @param pageVO 分页参数
     * @return 分页结果
     */
    PageInfo<ActiveTransaction> pageActiveTransaction(ActiveTransactionPageVO pageVO);
    
    /**
     * 获取活体交易记录详情
     * @param id 记录ID
     * @return 交易记录详情
     */
    ActiveTransactionVO getActiveTransactionDetail(String id);

    boolean add(ActiveTransactionRequestVO requestVO);

    List<FarmBreedType> getBreedingType();
}