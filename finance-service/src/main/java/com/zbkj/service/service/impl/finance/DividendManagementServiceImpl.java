package com.zbkj.service.service.impl.finance;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.zbkj.common.enums.FarmTypeEnum;
import com.zbkj.common.enums.UserTypeEnum;
import com.zbkj.common.model.finance.DividendDetail;
import com.zbkj.common.model.finance.DividendManagement;
import com.zbkj.common.model.finance.ShareManagement;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.DividendCreateRequest;
import com.zbkj.common.vo.finance.DividendManagementExportVO;
import com.zbkj.common.vo.finance.DividendManagementPageVO;
import com.zbkj.common.vo.finance.DividendManagementVO;
import com.zbkj.common.vo.finance.DividendStatisticsVO;
import com.zbkj.service.dao.finance.DividendManagementDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.DividendDetailService;
import com.zbkj.service.service.finance.DividendManagementService;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分红管理服务实现类
 */
@Slf4j
@Service
public class DividendManagementServiceImpl extends ServiceImpl<DividendManagementDao, DividendManagement> implements DividendManagementService {

    @Autowired
    private DividendDetailService dividendDetailService;
    @Autowired
    private UserService userService;

    @Override
    public Boolean addDividend(DividendCreateRequest request) {
        User info = userService.getInfo();
        DividendManagement dividendManagement=new DividendManagement();
        dividendManagement.setDividendDate(new Date());
        dividendManagement.setDividendTitle(request.getDividendTitle());
        dividendManagement.setTotalAmount(request.getTotalAmount());
        dividendManagement.setUserId(info.getId());
        dividendManagement.setOrganizationId(info.getOrganizationId());
        dividendManagement.setStatus(0);
        dividendManagement.setCreateTime(new Date());
        dividendManagement.setUpdateTime(new Date());
        dividendManagement.setDeleteFlag(0);
        save(dividendManagement);
        //添加分红明细
        for (DividendCreateRequest.DividendDetailRequest item : request.getDividendDetails()) {
            DividendDetail dividendDetail=new DividendDetail();
            dividendDetail.setDividendId(dividendManagement.getId());
            dividendDetail.setUserId(item.getUserId());
            dividendDetail.setMemberName(item.getMemberName());
            dividendDetail.setHoldingRatio(item.getHoldingRatio());
            dividendDetail.setDividendAmount(item.getDividendAmount());
            dividendDetail.setCreateTime(new Date());
            dividendDetail.setUpdateTime(new Date());
            dividendDetail.setDeleteFlag(0);
            dividendDetailService.save(dividendDetail);
        }
        return true;
    }

    @Override
    public List<DividendManagementVO> dividendList(DividendManagementPageVO pageParamRequest) {
        List<DividendManagementVO> resultList=new ArrayList<>();
        LambdaQueryWrapper<DividendManagement> lqw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(pageParamRequest.getDividendTitle())) {
            lqw.like(DividendManagement::getDividendTitle, pageParamRequest.getDividendTitle());
        }
        if (StringUtils.hasText(pageParamRequest.getDividendDate())) {
            lqw.like(DividendManagement::getDividendDate, pageParamRequest.getDividendDate());
        }
        User info = userService.getInfo();
        if(info.getFarmType().equals(FarmTypeEnum.TENANT_TYPE_COOPERATIVE.getCode())&&info.getUserType().equals(UserTypeEnum.USER_TYPE_ADMIN.getCode())){
            lqw.eq(DividendManagement::getOrganizationId, info.getOrganizationId());
        }else{
            lqw.eq(DividendManagement::getUserId, info.getId());
        }
        List<DividendManagement> dividendManagements = this.list(lqw);
        if(!CollectionUtils.isEmpty(dividendManagements)){
            dividendManagements.stream().forEach(item -> {
                DividendManagementVO dividendManagementVO = new DividendManagementVO();
                BeanUtils.copyProperties(item, dividendManagementVO);
                List<DividendDetail> dividendDetails = dividendDetailService.list(new LambdaQueryWrapper<DividendDetail>().eq(DividendDetail::getDividendId, item.getId()));
                dividendManagementVO.setDividendDetails(dividendDetails);
                resultList.add(dividendManagementVO);
            });
        }
        return resultList;
    }

    /**
     * 导出分红管理数据
     * @param pageVO 查询参数
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    @Override
    public void exportDividendData(DividendManagementPageVO pageVO, HttpServletResponse response) throws Exception {
        setExcelRespProp(response, "分红管理数据导出");
        try {
            
            // 构建查询条件
            LambdaQueryWrapper<DividendManagement> lqw = new LambdaQueryWrapper<>();
            lqw.eq(DividendManagement::getDeleteFlag, 0);
            
            if (StringUtils.hasText(pageVO.getDividendTitle())) {
                lqw.like(DividendManagement::getDividendTitle, pageVO.getDividendTitle());
            }
            if (StringUtils.hasText(pageVO.getDividendDate())) {
                lqw.like(DividendManagement::getDividendDate, pageVO.getDividendDate());
            }
            
            lqw.orderByDesc(DividendManagement::getCreateTime);
            
            List<DividendManagement> dividendList = this.list(lqw);
            
            // 转换为导出VO
            List<DividendManagementExportVO> exportList = dividendList.stream().map(dividend -> {
                DividendManagementExportVO exportVO = new DividendManagementExportVO();
                exportVO.setId(dividend.getId());
                exportVO.setDividendTitle(dividend.getDividendTitle());
                exportVO.setTotalAmount(dividend.getTotalAmount());
                exportVO.setDividendDate(dividend.getDividendDate() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dividend.getDividendDate()) : "");
                exportVO.setUserId(dividend.getUserId());
                exportVO.setOrganizationId(dividend.getOrganizationId());
                exportVO.setStatus(dividend.getStatus());
                exportVO.setCreateTime(dividend.getCreateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dividend.getCreateTime()) : "");
                exportVO.setUpdateTime(dividend.getUpdateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dividend.getUpdateTime()) : "");
                exportVO.setDeleteFlag(dividend.getDeleteFlag());
                return exportVO;
            }).collect(Collectors.toList());
            
            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), DividendManagementExportVO.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet("分红管理数据")
                    .doWrite(exportList);
            
            log.info("导出分红管理数据成功，共{}条记录", dividendList.size());
        } catch (Exception e) {
            log.error("导出分红管理数据失败", e);
            throw e;
        }
    }

    @Override
    public List<DividendManagementVO> dividendAdminList(DividendManagementPageVO pageParamRequest) {
        List<DividendManagementVO> resultList=new ArrayList<>();
        LambdaQueryWrapper<DividendManagement> lqw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(pageParamRequest.getDividendTitle())) {
            lqw.like(DividendManagement::getDividendTitle, pageParamRequest.getDividendTitle());
        }
        if (StringUtils.hasText(pageParamRequest.getDividendDate())) {
            lqw.like(DividendManagement::getDividendDate, pageParamRequest.getDividendDate());
        }
        List<DividendManagement> dividendManagements = this.list(lqw);
        if(!CollectionUtils.isEmpty(dividendManagements)){
            dividendManagements.stream().forEach(item -> {
                DividendManagementVO dividendManagementVO = new DividendManagementVO();
                BeanUtils.copyProperties(item, dividendManagementVO);
                List<DividendDetail> dividendDetails = dividendDetailService.list(new LambdaQueryWrapper<DividendDetail>().eq(DividendDetail::getDividendId, item.getId()));
                dividendManagementVO.setDividendDetails(dividendDetails);
                resultList.add(dividendManagementVO);
            });
        }
        return resultList;
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