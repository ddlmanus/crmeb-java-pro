package com.zbkj.common.model.finance;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName("eb_share_management_date")
@Data
public class ShareManagementDate {

    private String id;
    private Integer userId;
    private String memberName;
    private Date changeTime;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //删除标识
    private Integer deleteFlag;
}
