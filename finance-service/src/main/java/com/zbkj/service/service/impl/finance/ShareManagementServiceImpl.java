package com.zbkj.service.service.impl.finance;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.enums.FarmTypeEnum;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.model.finance.ShareChangeRecord;
import com.zbkj.common.model.finance.ShareManagement;
import com.zbkj.common.model.finance.ShareManagementDate;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.*;
import com.zbkj.service.dao.finance.ShareManagementDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.ShareChangeRecordService;
import com.zbkj.service.service.finance.ShareManagementDateService;
import com.zbkj.service.service.finance.ShareManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 股份管理服务实现类
 */
@Slf4j
@Service
public class ShareManagementServiceImpl extends ServiceImpl<ShareManagementDao, ShareManagement> implements ShareManagementService {

    @Autowired
    private ShareChangeRecordService shareChangeRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private ShareManagementDateService  shareManagementDateService;
    /**
     * 新增股份
     * @param shareVO 股份信息
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addShare(ShareUpdateRequest shareVO) {
        User info = userService.getInfo();

        if(shareVO.getItems() == null || shareVO.getItems().size() == 0){
            return false;
        }
        ShareManagementDate shareManagementDate=new ShareManagementDate();
        shareManagementDate.setMemberName(info.getNickname());
        shareManagementDate.setChangeTime(new Date());
        shareManagementDate.setCreateTime(new Date());
        shareManagementDate.setUpdateTime(new Date());
        shareManagementDate.setUserId(info.getId());
        shareManagementDate.setId(IdWorker.getIdStr());
        shareManagementDateService.save(shareManagementDate);
        for (ShareManagementVO item : shareVO.getItems()) {
            int changeCount=0;
            ShareChangeRecord shareChangeRecord = new ShareChangeRecord();
            //用户id查询是否存在股份
            LambdaQueryWrapper<ShareManagement> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShareManagement::getUserId, item.getUserId());
            ShareManagement shareManagement = getOne(queryWrapper);
            if(shareManagement != null){
                //更新股份数
                shareManagement.setHoldingRatio(item.getSharePercentage());
                this.updateById(shareManagement);
                shareChangeRecord.setShareManageId(shareManagement.getId());
                changeCount++;
            }else{
                shareManagement = new ShareManagement();
                shareManagement = new ShareManagement();
                shareManagement.setId(IdWorker.getIdStr());
                shareManagement.setUserId(item.getUserId());
                shareManagement.setMemberName(item.getUserName());
                shareManagement.setHoldingRatio(item.getSharePercentage());
                shareManagement.setCreateTime(new Date());
                shareManagement.setUpdateTime(new Date());
                shareManagement.setDeleteFlag(0);
                shareManagement.setChangeDateId(shareManagementDate.getId());
                shareManagement.setOrganizationId(info.getOrganizationId());
                shareChangeRecord.setShareManageId(shareManagement.getId());
                save(shareManagement);
            }
            shareChangeRecord.setId(IdWorker.getIdStr());
            shareChangeRecord.setChangeDateId(shareManagementDate.getId());
            shareChangeRecord.setUserId(item.getUserId());
            shareChangeRecord.setMemberName(item.getUserName());
            shareChangeRecord.setRatio(item.getSharePercentage());
            shareChangeRecord.setChangeReason("股份变更");
            shareChangeRecord.setChangeDate(new Date());
            shareChangeRecord.setCreateTime(new Date());
            shareChangeRecord.setUpdateTime(new Date());
            shareChangeRecord.setChangeCount(changeCount);
            shareManagement.setOrganizationId(info.getOrganizationId());
            //设置变更次数
            shareChangeRecordService.save(shareChangeRecord);
        }
        return true;
    }

    @Override
    public List<ShareManagement> changeList(ShareManagementPageVO pageParamRequest) {
        List<ShareManagement> resultList = new ArrayList<>();
        LambdaQueryWrapper<ShareManagement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareManagement::getOrganizationId, userService.getInfo().getOrganizationId());
        if(!StringUtils.isEmpty(pageParamRequest.getChangeTime())){
            queryWrapper.ge(ShareManagement::getCreateTime, pageParamRequest.getChangeTime());
        }
        User info = userService.getInfo();
        if(info.getFarmType().equals(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode())&&info.getUserType().equals(UserTypeEnum.USER_TYPE_ADMIN.getCode())){
            queryWrapper.eq(ShareManagement::getOrganizationId, info.getOrganizationId());
        }else{
            queryWrapper.eq(ShareManagement::getUserId, info.getId());
        }
        queryWrapper.orderByDesc(ShareManagement::getCreateTime);
        List<ShareManagement> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            //查询当前登录用户的机构下的用户信息
            List<User> userList = userService.getByOrganizationId(userService.getInfo().getOrganizationId());
            if(!CollectionUtils.isEmpty(userList)){
                userList.stream().forEach(user -> {
                    ShareManagement shareManagement = new ShareManagement();
                    shareManagement.setUserId(user.getId());
                    shareManagement.setMemberName(user.getNickname());
                    shareManagement.setHoldingRatio(new BigDecimal(0));
                    shareManagement.setCreateTime(new Date());
                    shareManagement.setUpdateTime(new Date());
                    shareManagement.setDeleteFlag(0);
                    shareManagement.setOrganizationId(userService.getInfo().getOrganizationId());
                    resultList.add(shareManagement);
                });
            }
            return resultList;
        }
        else {
            return list;
        }
    }

    /**
     * 导出股份管理数据
     * @param pageVO 查询参数
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    @Override
    public void exportShareData(ShareManagementPageVO pageVO, HttpServletResponse response) throws Exception {
        setExcelRespProp(response, "股份管理数据导出");
        try {
            User info = userService.getInfo();
            
            // 构建查询条件
            LambdaQueryWrapper<ShareManagement> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ShareManagement::getOrganizationId, info.getOrganizationId());
            queryWrapper.eq(ShareManagement::getDeleteFlag, 0);
            
            // 添加搜索条件
            if (StringUtils.hasText(pageVO.getChangeTime())) {
                queryWrapper.ge(ShareManagement::getCreateTime, pageVO.getChangeTime());
            }
            
            queryWrapper.orderByDesc(ShareManagement::getCreateTime);
            
            List<ShareManagement> shareList = this.list(queryWrapper);
            
            // 转换为导出VO
            List<ShareManagementExportVO> exportList = shareList.stream().map(share -> {
                ShareManagementExportVO exportVO = new ShareManagementExportVO();
                exportVO.setId(share.getId());
                exportVO.setUserId(share.getUserId());
                exportVO.setMemberName(share.getMemberName());
                exportVO.setHoldingRatio(share.getHoldingRatio());
                exportVO.setCreateTime(share.getCreateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(share.getCreateTime()) : "");
                exportVO.setUpdateTime(share.getUpdateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(share.getUpdateTime()) : "");
                exportVO.setDeleteFlag(share.getDeleteFlag());
                exportVO.setOrganizationId(share.getOrganizationId());
                return exportVO;
            }).collect(Collectors.toList());
            
            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), ShareManagementExportVO.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet("股份管理数据")
                    .doWrite(exportList);
            
            log.info("导出股份管理数据成功，共{}条记录", shareList.size());
        } catch (Exception e) {
            log.error("导出股份管理数据失败", e);
            throw e;
        }
    }

    @Override
    public List<ShareManagement> getAdminList(ShareManagementPageVO request) {
        LambdaQueryWrapper<ShareManagement> queryWrapper = new LambdaQueryWrapper<>();
       if(!StringUtils.isEmpty(request.getChangeTime())){
           queryWrapper.ge(ShareManagement::getCreateTime, request.getChangeTime());
       }
       if(request.getUserId() != null){
           queryWrapper.eq(ShareManagement::getUserId, request.getUserId());
       }
       if(request.getUserName() != null){
           queryWrapper.like(ShareManagement::getMemberName, request.getUserName());
       }
       queryWrapper.eq(ShareManagement::getDeleteFlag, 0);
       queryWrapper.orderByDesc(ShareManagement::getCreateTime);
       return this.list(queryWrapper);
    }

    @Override
    public PageInfo<ShareManagementDate> changeDateList(ShareManagementDatePageVO pageParamRequest) {
        return shareManagementDateService.changeDateList(pageParamRequest);
    }

    @Override
    public List<ShareManagement> changeDateDetail(String id) {
        LambdaQueryWrapper<ShareManagement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareManagement::getChangeDateId, id);
        return this.list(queryWrapper);
    }

    @Override
    public List<ShareChangeRecord> changeRecordList(String id) {
        LambdaQueryWrapper<ShareChangeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareChangeRecord::getShareManageId, id);
        return shareChangeRecordService.list(queryWrapper);
    }

    /**
     * 设置excel下载响应头属性
     */
    private void setExcelRespProp(HttpServletResponse response, String rawFileName) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode(rawFileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
    }

} 