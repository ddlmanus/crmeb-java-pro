package com.zbkj.service.service.impl.finance;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.finance.DividendDetail;
import com.zbkj.common.vo.finance.DividendDetailExportVO;
import com.zbkj.service.dao.finance.DividendDetailDao;
import com.zbkj.service.service.finance.DividendDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分红明细服务实现类
 */
@Slf4j
@Service
public class DividendDetailServiceImpl extends ServiceImpl<DividendDetailDao, DividendDetail> implements DividendDetailService {

    /**
     * 根据分红ID获取明细列表
     * @param dividendId 分红ID
     * @return 明细列表
     */
    @Override
    public List<DividendDetail> getByDividendId(String dividendId) {
        try {
            LambdaQueryWrapper<DividendDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DividendDetail::getDividendId, dividendId);
            wrapper.eq(DividendDetail::getDeleteFlag, 0);
            wrapper.orderByDesc(DividendDetail::getCreateTime);
            return this.list(wrapper);
        } catch (Exception e) {
            log.error("根据分红ID获取明细列表失败，dividendId: {}", dividendId, e);
            return null;
        }
    }

    /**
     * 根据用户ID获取分红明细
     * @param userId 用户ID
     * @return 分红明细列表
     */
    @Override
    public List<DividendDetail> getByUserId(Integer userId) {
        try {
            LambdaQueryWrapper<DividendDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DividendDetail::getUserId, userId);
            wrapper.eq(DividendDetail::getDeleteFlag, 0);
            wrapper.orderByDesc(DividendDetail::getCreateTime);
            return this.list(wrapper);
        } catch (Exception e) {
            log.error("根据用户ID获取分红明细失败，userId: {}", userId, e);
            return null;
        }
    }

    /**
     * 导出分红明细数据
     * @param dividendId 分红ID
     * @param response HTTP响应对象
     * @throws Exception 导出异常
     */
    @Override
    public void exportDividendDetailData(String dividendId, HttpServletResponse response) throws Exception {
        setExcelRespProp(response, "分红明细数据导出");
        try {
            // 构建查询条件
            LambdaQueryWrapper<DividendDetail> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DividendDetail::getDeleteFlag, 0);
            
            if (dividendId != null && !dividendId.trim().isEmpty()) {
                wrapper.eq(DividendDetail::getDividendId, dividendId);
            }
            
            wrapper.orderByDesc(DividendDetail::getCreateTime);
            
            List<DividendDetail> detailList = this.list(wrapper);
            
            // 转换为导出VO
            List<DividendDetailExportVO> exportList = detailList.stream().map(detail -> {
                DividendDetailExportVO exportVO = new DividendDetailExportVO();
                exportVO.setId(detail.getId());
                exportVO.setDividendId(detail.getDividendId());
                exportVO.setUserId(detail.getUserId());
                exportVO.setMemberName(detail.getMemberName());
                exportVO.setHoldingRatio(detail.getHoldingRatio());
                exportVO.setDividendAmount(detail.getDividendAmount());
                exportVO.setCreateTime(detail.getCreateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(detail.getCreateTime()) : "");
                exportVO.setUpdateTime(detail.getUpdateTime() != null ? 
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(detail.getUpdateTime()) : "");
                exportVO.setDeleteFlag(detail.getDeleteFlag());
                return exportVO;
            }).collect(Collectors.toList());
            
            // 使用EasyExcel导出数据
            EasyExcel.write(response.getOutputStream(), DividendDetailExportVO.class)
                    .excelType(ExcelTypeEnum.XLSX)
                    .sheet("分红明细数据")
                    .doWrite(exportList);
            
            log.info("导出分红明细数据成功，共{}条记录", detailList.size());
        } catch (Exception e) {
            log.error("导出分红明细数据失败", e);
            throw e;
        }
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