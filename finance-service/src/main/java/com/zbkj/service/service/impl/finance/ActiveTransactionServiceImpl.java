package com.zbkj.service.service.impl.finance;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ActiveTransaction;
import com.zbkj.common.model.finance.ActiveTransactionBreeding;
import com.zbkj.common.model.finance.ActiveTransactionDetail;
import com.zbkj.common.model.finance.FarmBreedType;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.service.dao.finance.ActiveTransactionDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.ActiveTransactionBreedingService;
import com.zbkj.service.service.finance.ActiveTransactionDetailService;
import com.zbkj.service.service.finance.ActiveTransactionService;
import com.zbkj.common.vo.finance.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.service.service.finance.FarmBreedTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活体交易记录服务实现
 */
@Slf4j
@Service
public class ActiveTransactionServiceImpl extends ServiceImpl<ActiveTransactionDao, ActiveTransaction> implements ActiveTransactionService {

    @Value("${api.base-url:https://mmt.haoyicn.cn}")
    private String apiBaseUrl;

    @Value("${api.token:zAXyemg13M4Lc7tKhigKJ9jmpdiktP7W}")
    private String apiToken;
    @Resource
    private UserService userService;
    @Resource
    private ActiveTransactionDetailService activeTransactionDetailService;
    @Resource
    private ActiveTransactionBreedingService activeTransactionBreedingService;
    @Autowired
    private FarmBreedTypeService farmBreedTypeService;
    /**
     * 获取养殖场进场数据
     * @param requestVO 请求参数
     * @return 进场数据列表
     */
    public List<FarmEntryResponseVO> getFarmEntryData(FarmEntryRequestVO requestVO) {
        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("farm_code", requestVO.getFarmCode());
            params.put("breed", requestVO.getBreed());
            params.put("rqStart", requestVO.getRqStart());
            params.put("rqEnd", requestVO.getRqEnd());
            params.put("page", requestVO.getPage());
            params.put("pageSize", requestVO.getPageSize());

            // 发送API请求
            String url = apiBaseUrl + "/data-service/api/yangzhicx_mmt_get_data_2";
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("apiToken", apiToken)
                    .body(JSONUtil.toJsonStr(params))
                    .execute();

            if (!response.isOk()) {
                log.error("获取养殖场进场数据失败: {}", response.body());
                return new ArrayList<>();
            }

            // 解析响应
            ApiResponseVO<Map<String, Object>> apiResponse = JSONUtil.toBean(
                    response.body(),
                    JSONUtil.toBean(response.body(), ApiResponseVO.class).getClass()
            );

            if (apiResponse.getCode() != 0 || apiResponse.getData() == null || apiResponse.getData().getRowData() == null) {
                log.error("获取养殖场进场数据失败，API返回错误: {}", response.body());
                return new ArrayList<>();
            }

            // 转换结果
            List<Map<String, Object>> rawData = apiResponse.getData().getRowData();
            List<FarmEntryResponseVO> result = new ArrayList<>();

            for (Map<String, Object> item : rawData) {
                FarmEntryResponseVO vo = new FarmEntryResponseVO();
                vo.setId(item.get("id").toString());
                vo.setProvince(getStringValue(item, "province"));
                vo.setCity(getStringValue(item, "city"));
                vo.setCounty(getStringValue(item, "county"));
                vo.setTownship(getStringValue(item, "township"));
                vo.setFarmCode(getStringValue(item, "farm_code"));
                vo.setFarmName(getStringValue(item, "farm_name"));
                vo.setEntryDate(getStringValue(item, "entry_date"));
                vo.setLivestock(getStringValue(item, "livestock"));
                vo.setBreed(getStringValue(item, "breed"));
                vo.setLivestockType(getStringValue(item, "livestock_type"));
                vo.setEntryQuantity(getIntValue(item, "entry_quantity"));
                vo.setSelfBredQuantity(getIntValue(item, "self_bred_quantity"));
                vo.setTransferredQuantity(getIntValue(item, "transferred_quantity"));
                result.add(vo);
            }

            return result;

        } catch (Exception e) {
            log.error("获取养殖场进场数据异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取养殖场离场数据
     * @param requestVO 请求参数
     * @return 离场数据列表
     */
    public List<FarmExitResponseVO> getFarmExitData(FarmExitRequestVO requestVO) {
        try {
            // 构建请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("farm_code", requestVO.getFarmCode());
            params.put("breed", requestVO.getBreed());
            params.put("rqStart", requestVO.getRqStart());
            params.put("rqEnd", requestVO.getRqEnd());
            params.put("page", requestVO.getPage());
            params.put("pageSize", requestVO.getPageSize());

            // 发送API请求
            String url = apiBaseUrl + "/data-service/api/yangzhicx_mmt_get_data_4";
            HttpResponse response = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .header("apiToken",  apiToken)
                    .body(JSONUtil.toJsonStr(params))
                    .execute();

            if (!response.isOk()) {
                log.error("获取养殖场离场数据失败: {}", response.body());
                return new ArrayList<>();
            }

            // 解析响应
            ApiResponseVO<Map<String, Object>> apiResponse = JSONUtil.toBean(
                    response.body(),
                    JSONUtil.toBean(response.body(), ApiResponseVO.class).getClass()
            );

            if (apiResponse.getCode() != 0 || apiResponse.getData() == null || apiResponse.getData().getRowData() == null) {
                log.error("获取养殖场离场数据失败，API返回错误: {}", response.body());
                return new ArrayList<>();
            }

            // 转换结果
            List<Map<String, Object>> rawData = apiResponse.getData().getRowData();
            List<FarmExitResponseVO> result = new ArrayList<>();

            for (Map<String, Object> item : rawData) {
                FarmExitResponseVO vo = new FarmExitResponseVO();
                vo.setId(item.get("id").toString());
                vo.setProvince(getStringValue(item, "province"));
                vo.setCity(getStringValue(item, "city"));
                vo.setCounty(getStringValue(item, "county"));
                vo.setTownship(getStringValue(item, "township"));
                vo.setFarmCode(getStringValue(item, "farm_code"));
                vo.setFarmName(getStringValue(item, "farm_name"));
                vo.setExitDate(getStringValue(item, "exit_date"));
                vo.setLivestock(getStringValue(item, "livestock"));
                vo.setBreed(getStringValue(item, "breed"));
                vo.setLivestockType(getStringValue(item, "livestock_type"));
                vo.setExitQuantity(getIntValue(item, "exit_quantity"));
                vo.setSlaughteredQuantity(getIntValue(item, "slaughtered_quantity"));
                vo.setTransferredOutQuantity(getIntValue(item, "transferred_out_quantity"));
                vo.setIsolatedQuantity(getIntValue(item, "isolated_quantity"));
                vo.setDeadQuantity(getIntValue(item, "dead_quantity"));
                vo.setEliminationQuantity(getIntValue(item, "elimination_quantity"));
                vo.setKillQuantity(getIntValue(item, "kill_quantity"));
                result.add(vo);
            }

            return result;

        } catch (Exception e) {
            log.error("获取养殖场离场数据异常", e);
            return new ArrayList<>();
        }
    }

    /**
     * 同步进场数据到交易记录
     * @param farmCode 养殖场编码
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncEntryDataToTransaction(String farmCode, String startDate, String endDate) {
        try {
            // 获取进场数据
            FarmEntryRequestVO requestVO = new FarmEntryRequestVO();
            requestVO.setFarmCode(farmCode);
            requestVO.setRqStart(startDate);
            requestVO.setRqEnd(endDate);
            requestVO.setPage(1);
            requestVO.setPageSize(1000); // 批量同步，设置较大的页面大小

            List<FarmEntryResponseVO> entryData = getFarmEntryData(requestVO);
            if (entryData.isEmpty()) {
                log.info("没有找到进场数据，无需同步");
                return true;
            }

            // 获取已存在的交易记录外部ID列表
            List<String> existingIds = this.lambdaQuery()
                    .eq(ActiveTransaction::getTransactionType, 0) // 进场
                    .eq(ActiveTransaction::getFarmCode, farmCode)
                    .list()
                    .stream()
                    .map(ActiveTransaction::getExternalId)
                    .collect(Collectors.toList());

            // 过滤出需要新增的数据
            List<FarmEntryResponseVO> newData = entryData.stream()
                    .filter(item -> !existingIds.contains(item.getId()))
                    .collect(Collectors.toList());

            if (newData.isEmpty()) {
                log.info("所有进场数据已同步，无需重复同步");
                return true;
            }

            // 转换为交易记录并保存
            List<ActiveTransaction> transactions = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (FarmEntryResponseVO entry : newData) {
                ActiveTransaction transaction = new ActiveTransaction();
                
                // 设置基本信息
                transaction.setFarmCode(entry.getFarmCode());
                transaction.setFarmName(entry.getFarmName());
                transaction.setTransactionType(0); // 进场
                
                // 解析日期
                Date transactionDate = null;
                try {
                    transactionDate = sdf.parse(entry.getEntryDate());
                } catch (Exception e) {
                    transactionDate = new Date(); // 如果解析失败，使用当前日期
                }
                transaction.setTransactionDate(transactionDate);
                
                // 设置品种信息
                transaction.setBreed(entry.getBreed());
                transaction.setLivestock(entry.getLivestock());
                transaction.setLivestockType(entry.getLivestockType());
                
                // 设置数量
                transaction.setQuantity(entry.getEntryQuantity());
                transaction.setSelfBreedQuantity(entry.getSelfBredQuantity());
                transaction.setTransferredInQuantity(entry.getTransferredQuantity());
                
                // 设置外部ID
                transaction.setExternalId(entry.getId());
                
                // 设置状态和时间
                transaction.setTransactionStatus(0);
                Date now = new Date();
                transaction.setCreateTime(now);
                transaction.setUpdateTime(now);
                transaction.setDeleteFlag(0);
                
                transactions.add(transaction);
            }
            
            // 批量保存
            return this.saveBatch(transactions);
            
        } catch (Exception e) {
            log.error("同步进场数据到交易记录异常", e);
            return false;
        }
    }

    /**
     * 同步离场数据到交易记录
     * @param farmCode 养殖场编码
     * @param startDate 开始日期 (格式: yyyy-MM-dd)
     * @param endDate 结束日期 (格式: yyyy-MM-dd)
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean syncExitDataToTransaction(String farmCode, String startDate, String endDate) {
        try {
            // 获取离场数据
            FarmExitRequestVO requestVO = new FarmExitRequestVO();
            requestVO.setFarmCode(farmCode);
            requestVO.setRqStart(startDate);
            requestVO.setRqEnd(endDate);
            requestVO.setPage(1);
            requestVO.setPageSize(1000); // 批量同步，设置较大的页面大小

            List<FarmExitResponseVO> exitData = getFarmExitData(requestVO);
            if (exitData.isEmpty()) {
                log.info("没有找到离场数据，无需同步");
                return true;
            }

            // 获取已存在的交易记录外部ID列表
            List<String> existingIds = this.lambdaQuery()
                    .eq(ActiveTransaction::getTransactionType, 1) // 离场
                    .eq(ActiveTransaction::getFarmCode, farmCode)
                    .list()
                    .stream()
                    .map(ActiveTransaction::getExternalId)
                    .collect(Collectors.toList());

            // 过滤出需要新增的数据
            List<FarmExitResponseVO> newData = exitData.stream()
                    .filter(item -> !existingIds.contains(item.getId()))
                    .collect(Collectors.toList());

            if (newData.isEmpty()) {
                log.info("所有离场数据已同步，无需重复同步");
                return true;
            }

            // 转换为交易记录并保存
            List<ActiveTransaction> transactions = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (FarmExitResponseVO exit : newData) {
                ActiveTransaction transaction = new ActiveTransaction();
                
                // 设置基本信息
                transaction.setFarmCode(exit.getFarmCode());
                transaction.setFarmName(exit.getFarmName());
                transaction.setTransactionType(1); // 离场
                
                // 解析日期
                Date transactionDate = null;
                try {
                    transactionDate = sdf.parse(exit.getExitDate());
                } catch (Exception e) {
                    transactionDate = new Date(); // 如果解析失败，使用当前日期
                }
                transaction.setTransactionDate(transactionDate);
                
                // 设置品种信息
                transaction.setBreed(exit.getBreed());
                transaction.setLivestock(exit.getLivestock());
                transaction.setLivestockType(exit.getLivestockType());
                
                // 设置数量
                transaction.setQuantity(exit.getExitQuantity());
                transaction.setSlaughteredQuantity(exit.getSlaughteredQuantity());
                transaction.setTransferredOutQuantity(exit.getTransferredOutQuantity());
                transaction.setIsolatedQuantity(exit.getIsolatedQuantity());
                transaction.setDeadQuantity(exit.getDeadQuantity());
                transaction.setEliminationQuantity(exit.getEliminationQuantity());
                transaction.setKillQuantity(exit.getKillQuantity());
                
                // 设置外部ID
                transaction.setExternalId(exit.getId());
                
                // 设置状态和时间
                transaction.setTransactionStatus(0);
                Date now = new Date();
                transaction.setCreateTime(now);
                transaction.setUpdateTime(now);
                transaction.setDeleteFlag(0);
                
                transactions.add(transaction);
            }
            
            // 批量保存
            return this.saveBatch(transactions);
            
        } catch (Exception e) {
            log.error("同步离场数据到交易记录异常", e);
            return false;
        }
    }

    /**
     * 分页查询活体交易记录
     * @param pageVO 分页参数
     * @return 分页结果
     */
    @Override
    public PageInfo<ActiveTransaction> pageActiveTransaction(ActiveTransactionPageVO pageVO) {
        User info = userService.getInfo();
        Page<ActiveTransaction> page = PageHelper.startPage(pageVO.getPage(), pageVO.getLimit());
        LambdaQueryWrapper<ActiveTransaction> queryWrapper = new LambdaQueryWrapper<>();
        if(Objects.nonNull(pageVO.getTransactionType())){
            queryWrapper.eq(ActiveTransaction::getTransactionType, pageVO.getTransactionType());
        }
        if(StrUtil.isNotBlank(pageVO.getStartTime())){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date startDate = sdf.parse(pageVO.getStartTime());
                queryWrapper.ge(ActiveTransaction::getTransactionDate, startDate);
            } catch (ParseException e) {
                log.warn("开始时间格式错误: {}", pageVO.getStartTime());
            }
        }
        if(StrUtil.isNotBlank(pageVO.getEndTime())){
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date endDate = sdf.parse(pageVO.getEndTime());
                queryWrapper.le(ActiveTransaction::getTransactionDate, endDate);
            }catch (ParseException e){
                log.warn("结束时间格式错误: {}", pageVO.getEndTime());
            }
        }
        queryWrapper.eq(ActiveTransaction::getFarmCode, info.getFarmCode());
        List<ActiveTransaction> transactions = this.list(queryWrapper);
        PageInfo<ActiveTransaction> activeTransactionPageInfo = CommonPage.copyPageInfo(page, transactions);
       return activeTransactionPageInfo;
    }

    /**
     * 获取活体交易记录详情
     * @param id 记录ID
     * @return 交易记录详情
     */
    @Override
    public ActiveTransactionVO getActiveTransactionDetail(String id) {
        ActiveTransactionVO activeTransactionVO = new ActiveTransactionVO();
        ActiveTransactionDetail activeTransactionDetail = activeTransactionDetailService.getbyActiveID(id);
        BeanUtils.copyProperties(activeTransactionDetail, activeTransactionVO);
        //获取交易记录品种
        activeTransactionVO.setActiveTransactionBreedingList(activeTransactionBreedingService.getByActiveTransactionId(id));
        return activeTransactionVO;
    }
    @Override
    public boolean add(ActiveTransactionRequestVO requestVO) {
        String activeTransactionIds = requestVO.getActiveTransactionIds();
        //转成list
        List<String> activeTransactionIdList = Arrays.asList(activeTransactionIds.split(","));
        if(!CollectionUtils.isEmpty(activeTransactionIdList)){
            activeTransactionIdList.stream().forEach(activeTransactionId->{
                ActiveTransaction activeTransaction = this.getById(activeTransactionId);
                activeTransaction.setTransactionStatus(0);
                activeTransaction.setUpdateTime(new Date());
                this.updateById(activeTransaction);
                ActiveTransactionDetail activeTransactionDetail=new ActiveTransactionDetail();
                BeanUtils.copyProperties(requestVO, activeTransactionDetail);
                activeTransactionDetail.setActiveTransactionId(activeTransactionId);
                activeTransactionDetail.setId(IdWorker.getIdStr());
                activeTransactionDetail.setBankTransactionNo(requestVO.getBankTransactionNo());
                activeTransactionDetail.setCreateTime(new Date());
                activeTransactionDetail.setUpdateTime(new Date());
                activeTransactionDetail.setDeleteFlag(false);
                this.activeTransactionDetailService.save(activeTransactionDetail);
                if(CollectionUtils.isNotEmpty(requestVO.getActiveTransactionBreedingList())){
                    requestVO.getActiveTransactionBreedingList().forEach(item->{
                        ActiveTransactionBreeding activeTransactionBreeding=new ActiveTransactionBreeding();
                        activeTransactionBreeding.setId(IdWorker.getIdStr());
                        BeanUtils.copyProperties(item, activeTransactionBreeding);
                        activeTransactionBreeding.setCreateTime(new Date());
                        activeTransactionBreeding.setUpdateTime(new Date());
                        activeTransactionBreeding.setActiveTransactionId(activeTransactionId);
                        activeTransactionBreeding.setDeleteFlag(false);
                        this.activeTransactionBreedingService.save(activeTransactionBreeding);
                    });
                }
            });
        }
        return  true;
    }

    @Override
    public List<FarmBreedType> getBreedingType() {
        User info = userService.getInfo();
        return farmBreedTypeService.getByFarmCode(info.getFarmCode());
    }

    /**
     * 转换为VO对象
     * @param transaction 活体交易记录实体
     * @return 活体交易记录VO
     */
    private ActiveTransactionVO convertToVO(ActiveTransaction transaction) {
        ActiveTransactionVO vo = new ActiveTransactionVO();
        BeanUtils.copyProperties(transaction, vo);
        return vo;
    }
    
    /**
     * 从Map中获取String值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
    
    /**
     * 从Map中获取Integer值
     */
    private Integer getIntValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
} 