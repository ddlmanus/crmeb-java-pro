package com.zbkj.common.dto;

import lombok.Data;
import java.util.List;

/**
 * 牧码通存栏量接口响应
 */
@Data
public class StockQuantityApiResponse {
    
    private Integer code;
    private String msg;
    private StockQuantityData data;
    
    @Data
    public static class StockQuantityData {
        private Boolean ifQuery;
        private Integer time;
        private Integer total;
        private Boolean success;
        private String errorMsg;
        private Integer affectedRows;
        private List<String> columns;
        private List<StockQuantityRow> rowData;
    }
    
    @Data
    public static class StockQuantityRow {
        private String province;
        private String city;
        private String county;
        private String township;
        private String farm_code;
        private String farm_name;
        private String livestock_type;
        private String breed;
        private String breeding_stage;
        private String last_period_stock;
        private String current_period_out;
        private String current_period_add;
        private String current_period_stock;
        private String current_OFF_PEN;
        private String current_TRANSFER_OUT;
        private String current_QUARANTINE;
        private String current_DEATH;
        private String current_PHASE_OUT;
        private String current_KILL;
        private String create_time;
    }
} 