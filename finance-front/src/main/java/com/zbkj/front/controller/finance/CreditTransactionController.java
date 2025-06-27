package com.zbkj.front.controller.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.CreditTransactionPageVO;
import com.zbkj.common.vo.finance.CreditTransactionVO;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.finance.CreditTransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端授信交易记录控制器
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
@Slf4j
@RestController
@RequestMapping("api/front/finance/creditTransaction")
@Api(tags = "用户端授信交易记录控制器")
public class CreditTransactionController {

    @Autowired
    private CreditTransactionService creditTransactionService;
    
    @Autowired
    private UserService userService;

    @ApiOperation(value = "分页查询交易记录")
    @PostMapping("/page")
    public CommonResult<IPage<CreditTransactionVO>> pageTransactions(@RequestBody CreditTransactionPageVO pageRequest) {
        try {
            IPage<CreditTransactionVO> result = creditTransactionService.getCurrentUserTransactions(pageRequest);
            return CommonResult.success(result);
        } catch (Exception e) {
            log.error("分页查询交易记录失败", e);
            return CommonResult.failed("查询交易记录失败：" + e.getMessage());
        }
    }

    @ApiOperation(value = "我的授信交易记录列表")
    @RequestMapping(value = "/my/list", method = RequestMethod.GET)
    public CommonResult<Object> getMyTransactionList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false) Integer transactionType) {
        
        try {
            // 获取当前登录用户ID
            Integer currentUserId = userService.getInfo().getId();
            
            // 构建查询参数 - 暂时返回简单结果
            return CommonResult.success("交易记录查询功能开发中");
        } catch (Exception e) {
            log.error("获取用户交易记录失败", e);
            return CommonResult.failed("获取交易记录失败");
        }
    }

    @ApiOperation(value = "授信交易记录详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    public CommonResult<CreditTransactionVO> getTransactionDetail(@PathVariable String id) {
        try {
            CreditTransactionVO transaction = creditTransactionService.getTransactionDetail(id);
            
            if (transaction == null) {
                return CommonResult.failed("交易记录不存在");
            }
            
            // 验证该交易记录是否属于当前用户
            Integer currentUserId = userService.getInfo().getId();
            if (!transaction.getUserId().equals(currentUserId.toString())) {
                return CommonResult.failed("无权查看该交易记录");
            }
            
            return CommonResult.success(transaction);
        } catch (Exception e) {
            log.error("获取交易记录详情失败", e);
            return CommonResult.failed("获取交易记录详情失败");
        }
    }

    @ApiOperation(value = "授信交易统计信息")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public CommonResult<Object> getMyTransactionStatistics() {
        try {
            // 获取当前登录用户ID
            Integer currentUserId = userService.getInfo().getId();
            
            // TODO: 实现用户交易统计功能
            // 可以统计：总消费金额、总还款金额、待还款金额、交易次数等
            
            return CommonResult.success("统计功能待完善");
        } catch (Exception e) {
            log.error("获取交易统计失败", e);
            return CommonResult.failed("获取统计信息失败");
        }
    }

    @ApiOperation(value = "按交易类型获取交易记录")
    @RequestMapping(value = "/type/{type}", method = RequestMethod.GET)
    public CommonResult<Object> getTransactionsByType(
            @PathVariable Integer type,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        try {
            // 获取当前登录用户ID
            Integer currentUserId = userService.getInfo().getId();
            
            // TODO: 实现按类型查询交易记录
            return CommonResult.success("按类型查询功能开发中");
        } catch (Exception e) {
            log.error("按类型获取交易记录失败", e);
            return CommonResult.failed("获取交易记录失败");
        }
    }
} 