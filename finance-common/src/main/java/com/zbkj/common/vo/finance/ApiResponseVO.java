package com.zbkj.common.vo.finance;

import lombok.Data;

import java.util.List;

/**
 * 第三方API响应通用VO
 */
@Data
public class ApiResponseVO<T> {
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String msg;
    
    /**
     * 响应数据
     */
    private ApiResponseData<T> data;
    
    @Data
    public static class ApiResponseData<T> {
        
        /**
         * 是否查询
         */
        private Boolean ifQuery;
        
        /**
         * 查询时间
         */
        private Integer time;
        
        /**
         * 总数
         */
        private Integer total;
        
        /**
         * 是否成功
         */
        private Boolean success;
        
        /**
         * 错误消息
         */
        private String errorMsg;
        
        /**
         * 影响行数
         */
        private Integer affectedRows;
        
        /**
         * 列名
         */
        private List<String> columns;
        
        /**
         * 行数据
         */
        private List<T> rowData;
    }
} 