package com.riteny.util.exception.core.exception

 abstract class CommonException(var errorCode: String, var errorMsg: String, var langType: String) :
    RuntimeException(errorMsg) {
}