package com.riteny.util.exception.core.exception

open class CommonApiException(errorCode: String, errorMsg: String, langType: String) :
    CommonException(errorCode, errorMsg, langType)