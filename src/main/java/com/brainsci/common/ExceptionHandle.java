package com.brainsci.common;

import com.brainsci.springsecurity.execption.BaseException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常捕获处理
 */
@ControllerAdvice
public class ExceptionHandle {

    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ResultBean handle(BaseException e) {

        System.out.println("ResultHandler.error(e);"+e);

        return ResultHandler.error(e);
    }

}
