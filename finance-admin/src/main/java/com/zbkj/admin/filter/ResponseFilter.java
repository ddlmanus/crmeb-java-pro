package com.zbkj.admin.filter;


import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.utils.RequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.FilterInvocation;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


/**
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2023 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 * 返回值输出过滤器
 */
//@Component
public class ResponseFilter implements Filter {


    @Autowired
    CrmebConfig crmebConfig;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        ResponseWrapper wrapperResponse = new ResponseWrapper((HttpServletResponse) response);//转换成代理类
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (request.getRequestURI().contains("19017f4dd97a446eb8c07a47f4a34346szt5jzynfz.png")) {
            return;
        }

        FilterInvocation fi = new FilterInvocation(servletRequest, wrapperResponse, filterChain);

        //OPTIONS请求直接放行
        if(request.getMethod().equals(HttpMethod.OPTIONS.toString())){
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
            return;
        }


        // 这里只拦截返回，直接让请求过去，如果在请求前有处理，可以在这里处理
        filterChain.doFilter(request, wrapperResponse);
        byte[] content = wrapperResponse.getContent();//获取返回值
        //判断是否有值
        if (content.length > 0) {
            String str = new String(content, StandardCharsets.UTF_8);

            try {
                HttpServletRequest req = (HttpServletRequest) request;
                str = new ResponseRouter().filter(str, RequestUtil.getUri(req), crmebConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //把返回值输出到客户端
            ServletOutputStream outputStream = response.getOutputStream();
            if (str.length() > 0) {
                outputStream.write(str.getBytes());
                outputStream.flush();
                outputStream.close();
                //最后添加这一句，输出到客户端
                response.flushBuffer();
            }
        }
    }
}
