package com.zbkj.common.dto;

import lombok.Data;

import java.util.List;

/**
 * 养殖品种API响应
 */
@Data
public class BreedingProductApiResponse {
    
    private Integer code;
    private String msg;
    private BreedingProductApiData data;
    
    @Data
    public static class BreedingProductApiData {
        private Boolean ifQuery;
        private Integer time;
        private Integer total;
        private Boolean success;
        private String errorMsg;
        private Object affectedRows;
        private List<String> columns;
        private List<BreedingProductApiRow> rowData;
    }
    
    @Data
    public static class BreedingProductApiRow {
        private String code;
        private String name;
        private String split_species;
    }
} 