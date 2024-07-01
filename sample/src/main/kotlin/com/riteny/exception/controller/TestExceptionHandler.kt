package com.riteny.exception.controller

import com.alibaba.fastjson2.JSONObject
import com.riteny.util.exception.core.api.CommonApiExceptionHandler
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TestExceptionHandler : CommonApiExceptionHandler<TestException, JSONObject> {
    override fun handler(
        e: TestException, request: HttpServletRequest, response: HttpServletResponse
    ): JSONObject {

        val result = JSONObject()
        result["resultCode"] = e.errorCode
        result["resultMsg"] = e.errorMsg

        return result
    }

}