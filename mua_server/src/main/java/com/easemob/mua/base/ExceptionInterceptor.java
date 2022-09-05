package com.easemob.mua.base;

import com.easemob.mua.exception.BizException;
import com.easemob.mua.utils.JsonUtil;
import com.easemob.mua.utils.ResultUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author easemob_developer
 * @date 2022/5/25
 */
@RestControllerAdvice
public class ExceptionInterceptor extends ResponseEntityExceptionHandler {

    private static Logger log = LoggerFactory.getLogger(ExceptionInterceptor.class);

    public void printLog(Exception ex) {
        String jsonStr = null;
        if (ex instanceof BizException) {
            try {
                jsonStr = JsonUtil.toJsonString(ResultUtil.bizException((BizException) ex));
            } catch (JsonProcessingException e) {
                log.error("当前请求返回结果对象json转换异常。", e);
            }
            log.info(String.format("当前请求返回结果:%s", jsonStr));
        } else {
            try {
                jsonStr = JsonUtil.toJsonString(ResultUtil.otherException());
            } catch (JsonProcessingException e) {
                log.error("当前请求返回结果对象json转换异常。", e);
            }
            log.error(String.format("当前请求返回结果:%s", jsonStr), ex);
        }
    }

    /**
     * 处理业务异常封装返回结果
     *
     * @param exception 业务异常对象
     * @return BaseResponse 业务异常封装返回结果
     * @author easemob_developer
     * @date 2020/05/06 17:47:14
     */
    @ExceptionHandler(BizException.class)
    public BaseResponse bizException(BizException exception) {
        printLog(exception);
        return ResultUtil.bizException(exception);
    }

    /**
     * 处理其它异常封装返回结果
     *
     * @param exception 其它异常对象
     * @return BaseResponse 其它异常封装返回结果
     * @author easemob_developer
     * @date 2020/05/06 17:48:24
     */
    @ExceptionHandler(Exception.class)
    public Object otherException(Exception exception) {
        printLog(exception);
        return ResultUtil.otherException();
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        // 返回封装
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", status.value());
        result.put("msg", status.getReasonPhrase());
        result.put("path", this.getRequest().getRequestURI());
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, ex, WebRequest.SCOPE_REQUEST);
        }
        ResponseEntity<Object> res = new ResponseEntity<>(result, headers, status);
        String jsonStr = null;
        try {
            jsonStr = JsonUtil.toJsonString(res);
        } catch (JsonProcessingException e) {
            log.error("当前请求返回结果对象json转换异常。", e);
        }
        log.error(String.format("当前请求返回结果:%s", jsonStr), ex);
        return res;
    }

    public HttpServletRequest getRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
        return request;
    }
}
