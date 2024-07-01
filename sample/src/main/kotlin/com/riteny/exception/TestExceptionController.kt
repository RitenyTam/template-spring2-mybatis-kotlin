package com.riteny.exception

import com.riteny.exception.controller.TestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/test/exception")
class TestExceptionController {

    @GetMapping("/")
    fun testException(): String {
        throw TestException("0", "test.index", "en_us")
    }
}