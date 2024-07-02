package com.riteny.exception.controller

import com.riteny.exception.TestException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/test/exception")
open class TestExceptionController {

    @GetMapping("/")
    open fun testException(): String {
        throw TestException("0", "test.index", "en_us")
    }
}