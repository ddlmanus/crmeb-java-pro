package com.zbkj.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.StyleSet;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.constants.DateConstants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.exception.CrmebException;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 导出工具类
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
public class ExportUtil {

    /**
     * 导出Excel文件
     *
     * @param fileName 文件名(无需后缀)
     * @param title    文件标题
     * @param voList   数据列表
     * @param aliasMap 别名Map（别名需要与数据列表的数据对应）
     * @param response HttpServletResponse
     */
    public static void exportExcel(String fileName, String title, List<?> voList, LinkedHashMap<String, String> aliasMap, HttpServletResponse response) {
        if (StrUtil.isBlank(fileName)) {
            throw new CrmebException("文件名不能为空");
        }
        if (StrUtil.isBlank(title)) {
            throw new CrmebException("标题不能为空");
        }
        if (CollUtil.isEmpty(voList)) {
            throw new CrmebException("数据列表不能为空");
        }
        if (CollUtil.isEmpty(aliasMap)) {
            throw new CrmebException("别名map不能为空");
        }

        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");

            // 通过工具类创建writer，直接写入输出流
            ExcelWriter writer = ExcelUtil.getWriter(true);

            // 设置样式
            CellStyle headCellStyle = writer.getHeadCellStyle();
            Font font = writer.createFont();
            font.setBold(true);
            headCellStyle.setFont(font);
            CellStyle styleSet = writer.getCellStyle();
            styleSet.setWrapText(true);

            // 自定义标题别名
            aliasMap.forEach(writer::addHeaderAlias);
            // 合并单元格后的标题行
            writer.merge(aliasMap.size() - 1, title);
            writer.merge(aliasMap.size() - 1, StrUtil.format("生成时间:{}", DateUtil.now()));
            // 设置宽度自适应
            writer.setColumnWidth(-1, 22);

            // 一次性写出内容
            writer.write(voList, true);

            // 将writer的内容写入到response的输出流
            writer.flush(response.getOutputStream());
            // 关闭writer，释放内存
            writer.close();
        } catch (IOException e) {
            throw new CrmebException("导出Excel异常：" + e.getMessage());
        }
    }

    /**
     * 上传部分设置
     */
    public static void setUpload(String rootPath, String modelPath, String type) {
        if (StrUtil.isBlank(rootPath) || StrUtil.isBlank(modelPath) || StrUtil.isBlank(type)) {
            throw new CrmebException("请检查上传参数，上传参数不能为空");
        }
        UploadUtil.setRootPath(rootPath);
        UploadUtil.setModelPath(UploadConstants.UPLOAD_FILE_KEYWORD + "/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/" + modelPath);
//        UploadUtil.setType(type);
    }


}
