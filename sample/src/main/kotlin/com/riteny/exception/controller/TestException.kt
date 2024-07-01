package com.riteny.exception.controller

import com.riteny.util.exception.core.exception.CommonApiException

class TestException(errorCode: String, errorMsg: String, langType: String) :
    CommonApiException(errorCode, errorMsg, langType)