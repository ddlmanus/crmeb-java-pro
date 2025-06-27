package com.zbkj.service.service.impl.finance;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.finance.ShareChangeRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.vo.finance.ShareChangeRecordExportVO;
import com.zbkj.service.dao.finance.ShareChangeRecordDao;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.ShareChangeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 股份变更记录服务实现类
 */
@Slf4j
@Service
public class ShareChangeRecordServiceImpl extends ServiceImpl<ShareChangeRecordDao, ShareChangeRecord> implements ShareChangeRecordService {
    @Autowired
    private ShareChangeRecordDao shareChangeRecordDao;
    @Autowired
    private UserService userService;

    /**
     * 分页查询股份变更记录
     * @param pageParamRequest 分页参数
     * @param keywords 关键词
     * @return 分页结果
     */
    @Override
    public PageInfo<ShareChangeRecord> getShareChangeRecordPage(PageParamRequest pageParamRequest, String keywords) {
        try {
          Page<ShareChangeRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
            LambdaQueryWrapper<ShareChangeRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShareChangeRecord::getDeleteFlag, 0);
            
            if (keywords != null && !keywords.trim().isEmpty()) {
                wrapper.and(w -> w.like(ShareChangeRecord::getMemberName, keywords)
                        .or().like(ShareChangeRecord::getChangeReason, keywords));
            }
            
            wrapper.orderByDesc(ShareChangeRecord::getChangeDate);
            List<ShareChangeRecord> list = shareChangeRecordDao.selectList(wrapper);
            return CommonPage.copyPageInfo(page, list);
        } catch (Exception e) {
            log.error("分页查询股份变更记录失败", e);
            return null;
        }
    }

    /**
     * 根据用户ID获取变更记录
     * @param userId 用户ID
     * @return 变更记录列表
     */
    @Override
    public List<ShareChangeRecord> getByUserId(Integer userId) {
        try {
            LambdaQueryWrapper<ShareChangeRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShareChangeRecord::getUserId, userId);
            wrapper.eq(ShareChangeRecord::getDeleteFlag, 0);
            wrapper.orderByDesc(ShareChangeRecord::getChangeDate);
            return this.list(wrapper);
        } catch (Exception e) {
            log.error("根据用户ID获取变更记录失败，userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 导出股份变更记录数据
     * @param keywords 关键词
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    @Override
    public void exportShareChangeRecordData(String keywords, HttpServletResponse response) throws Exception {
        setExcelRespProp(response, "股份变更记录导出");
        try {
            // 构建查询条件
            LambdaQueryWrapper<ShareChangeRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ShareChangeRecord::getDeleteFlag, 0);
            
            if (keywords != null && !keywords.trim().isEmpty()) {
                wrapper.and(w -> w.like(ShareChangeRecord::getMemberName, keywords)
                        .or().like(ShareChangeRecord::getChangeReason, keywords));
            }
            
            wrapper.orderByDesc(ShareChangeRecord::getChangeDate);
            
            List<ShareChangeRecord> recordList = this.list(wrapper);
            
            // 转换为导出VO
            List<ShareChangeRecordExportVO> exportList = recordList.stream().map(record -> {
                ShareChangeRecordExportVO exportVO = new ShareChangeRecordExportVO();
                exportVO.setId(record.getId());
                exportVO.setUserId(record.getUserId());
                exportVO.setMemberName(record.getMemberName());
                exportVO.setRatio(record.getRatio());
                exportVO.setChangeReason(record.getChangeReason());
                exportVO.setChangeDate(record.getChangeDate() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getChangeDate()) : "");
                exportVO.setCreateTime(record.getCreateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getCreateTime()) : "");
                exportVO.setUpdateTime(record.getUpdateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(record.getUpdateTime()) : "");
                exportVO.setDeleteFlag(record.getDeleteFlag());
                exportVO.setOrganizationId(record.getOrganizationId());
                exportVO.setChangeCount(record.getChangeCount());
                return exportVO;
            }).collect(Collectors.toList());
            
            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), ShareChangeRecordExportVO.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet("股份变更记录数据")
                    .doWrite(exportList);
            
            log.info("导出股份变更记录数据成功，共{}条记录", recordList.size());
        } catch (Exception e) {
            log.error("导出股份变更记录数据失败", e);
            throw e;
        }
    }

    @Override
    public List<ShareChangeRecord> shareChangeList() {
        User info = userService.getInfo();
        LambdaQueryWrapper<ShareChangeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareChangeRecord::getUserId, info.getId());
        return this.shareChangeRecordDao.selectList(queryWrapper);
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