package com.zbkj.common.vo.finance;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 牧码通存栏数据响应VO
 */
@Data
@ApiModel(value = "LivestockInventoryResponseVO", description = "牧码通存栏数据响应")
public class LivestockInventoryResponseVO {

    @ApiModelProperty(value = "响应码")
    private Integer code;

    @ApiModelProperty(value = "响应消息")
    private String msg;

    @ApiModelProperty(value = "数据")
    private LivestockInventoryData data;

    @Data
    public static class LivestockInventoryData {

        @ApiModelProperty(value = "是否查询成功")
        private Boolean ifQuery;

        @ApiModelProperty(value = "查询耗时")
        private Integer time;

        @ApiModelProperty(value = "总数")
        private Integer total;

        @ApiModelProperty(value = "是否成功")
        private Boolean success;

        @ApiModelProperty(value = "错误信息")
        private String errorMsg;

        @ApiModelProperty(value = "影响行数")
        private Integer affectedRows;

        @ApiModelProperty(value = "列名")
        private List<String> columns;

        @ApiModelProperty(value = "行数据")
        private List<LivestockInventoryDataVO> rowData;
    }
} 