package com.zbkj.front.controller.finance;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.zbkj.common.model.finance.CreditPaymentOrder;
import com.zbkj.common.model.finance.FarmInstitution;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.CreditRepaymentRequest;
import com.zbkj.common.response.UserInfoResponse;
import com.zbkj.common.result.CommonResult;
import com.zbkj.common.vo.finance.*;
import com.zbkj.common.request.UserAddRequest;
import com.zbkj.service.service.finance.CreditPaymentOrderService;
import com.zbkj.service.service.finance.FarmInstitutionService;
import com.zbkj.service.service.finance.MutualAidService;
import com.zbkj.service.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Api(tags = "社员互助金接口")
@RequestMapping("/api/front/finance/mutualAid")
public class MutualAidController {

    @Resource
    private MutualAidService mutualAidService;
    
    @Resource
    private UserService userService;
    @Resource
    private CreditPaymentOrderService creditPaymentOrderService;
    @Resource
    private FarmInstitutionService farmInstitutionService;
    /**
     * 获取养殖场信息(获取没有管理员的养殖场)
     */
    @GetMapping("/no-admin")
    @ApiOperation(value = "获取养殖场信息(获取没有管理员的养殖场)")
    public CommonResult<List<FarmInstitution>> getFarmInstitutionNoAdmin() {
        List<FarmInstitution> farmInstitutions = farmInstitutionService.getFarmInstitutionNoAdmin();
        return CommonResult.success(farmInstitutions);
    }

    /**
     * 分页查询社员列表
     * 如果是合作社，查询合作社下的员工信息
     * 如果是养殖户，只查询自己的信息
     */
    @PostMapping("/members/page")
    @ApiOperation(value = "分页查询社员列表")
    public CommonResult<IPage<MutualAidMemberVO>> getMembersList(@RequestBody MutualAidMemberPageRequest pageRequest) {
        return CommonResult.success(mutualAidService.getMembersList(pageRequest));
    }

    /**
     * 添加社员
     */
    @PostMapping("/members/add")
    @ApiOperation(value = "添加社员")
    public CommonResult<Boolean> addMember(@RequestBody @Validated UserAddRequest userRequest) {
        UserInfoResponse userInfo = userService.getUserInfo();
        userRequest.setOrganizationCode(userInfo.getOrganizationCode());
        userRequest.setOrganizationId(userInfo.getOrganizationId());
        userRequest.setOrganizationName(userInfo.getOrganizationName());
        userRequest.setPwd("000000");
        return CommonResult.success(userService.addUser(userRequest));
    }

    /**
     * 设置授信条件（利率和期限）
     */
    @PostMapping("/setCreditTerm")
    @ApiOperation(value = "设置授信条件")
    public CommonResult<Boolean> setCreditTerm(@RequestBody @Validated CreditTermSettingVO request) {
        return CommonResult.success(mutualAidService.setCreditTerm(request));
    }

    /**
     * 根据用户ID查询担保人列表
     */
    @GetMapping("/guarantors/{userId}")
    @ApiOperation(value = "查询担保人列表")
    public CommonResult<List<GuarantorVO>> getGuarantorsByUserId(@PathVariable("userId") Integer userId) {
        return CommonResult.success(mutualAidService.getGuarantorsByUserId(userId));
    }

    /**
     * 添加担保人
     */
    @PostMapping("/guarantors")
    @ApiOperation(value = "添加担保人")
    public CommonResult<Boolean> addGuarantor(@RequestBody @Validated EmployeeGuarantorRequestVO request) {
        return CommonResult.success(mutualAidService.addGuarantor(request));
    }

    /**
     * 分页查询还款单
     */
    @GetMapping("/credit/payment/order/page")
    @ApiOperation(value = "分页查询还款单")
    public CommonResult<CommonPage<CreditPaymentOrder>> pageRepaymentOrder(@Validated CreditOrderRequest repaymentPageVO) {
        return CommonResult.success(CommonPage.restPage(mutualAidService.pageRepaymentOrder(repaymentPageVO)));
    }

    /**
     * 根据用户ID分页查询还款记录
     */
    @PostMapping("/repayments/page")
    @ApiOperation(value = "分页查询还款记录")
    public CommonResult<IPage<RepaymentRecordVO>> getRepaymentRecords(@RequestBody MutualAidMemberPageRequest pageRequest) {
        return CommonResult.success(mutualAidService.getRepaymentRecords(pageRequest));
    }
    @PostMapping("/detail")
    @ApiOperation(value = "查询还款记录详情")
    public CommonResult<RepaymentRecordVO> getRepaymentDetail(String id) {
        return CommonResult.success(mutualAidService.getRepaymentDetail(id));
    }
    @ApiOperation(value = "授信还款（合作社下的种植户还款）")
    @RequestMapping(value = "/credit/repayment", method = RequestMethod.POST)
    public CommonResult<Boolean> creditRepayment(@RequestBody @Validated CreditRepaymentRequest request) {
        return CommonResult.success(mutualAidService.processRepayment(request.getCreditOrderNo(), request.getRepaymentAmount(),  request.getRepaymentProof()));
    }
    @ApiOperation(value = "根据授信支付订单号获取订单详情")
    @RequestMapping(value = "/credit/order/{creditOrderNo}", method = RequestMethod.GET)
    public CommonResult<CreditPaymentOrder> getCreditPaymentOrderDetail(@PathVariable(value = "creditOrderNo") String creditOrderNo) {
        return CommonResult.success(creditPaymentOrderService.getByCreditOrderNo(creditOrderNo));
    }

    /**
     * 根据用户ID分页查询交易记录
     */
    @PostMapping("/transactions/page")
    @ApiOperation(value = "分页查询交易记录")
    public CommonResult<IPage<TransactionRecordVO>> getTransactionRecords(@RequestBody MutualAidMemberPageRequest pageRequest) {
        return CommonResult.success(mutualAidService.getTransactionRecords(pageRequest));
    }
    @PostMapping("/audit")
    @ApiOperation(value = "审核还款记录")
    public CommonResult<Boolean> auditRepayment(@RequestBody RepaymentAuditVO repaymentAuditVO) {
        return CommonResult.success(mutualAidService.auditRepayment(repaymentAuditVO));
    }
} 