package com.riteny.httpclient

import com.alibaba.fastjson2.JSONObject
import com.riteny.logger.CommonLoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

class CommonHttpRestTemplate private constructor() {

    companion object {

        val logger = CommonLoggerFactory.getLogger("api")

        /**
         * 獲取實例對象（鏈式編程風格）
         */
        fun getInstance() = CommonHttpRestTemplate()

        fun <T> executeGetMethod(
            url: String,
            params: Map<String, Any>?,
            headers: HttpHeaders?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val startTime = System.currentTimeMillis()

            logger.info("#Request : $url #Method : GET #Param : $headers")

            var tempUrl = "$url?"
            params?.let { paramsMap ->
                paramsMap.keys.forEach { key -> tempUrl += "$key=${paramsMap[key]}&" }
                tempUrl = url.substring(0, url.length - 1)
            }

            try {
                val httpEntity = HttpEntity(params, headers)
                val response = getRestTemplate(connTimeout, readTimeout).exchange(
                    tempUrl,
                    HttpMethod.GET,
                    httpEntity,
                    responseType
                )

                val useTime = System.currentTimeMillis() - startTime
                logger.info("#Response : $url #Method : GET  #use time(ms) : $useTime #Result : ${response.body}")

                val responseBody = response.body ?: throw RuntimeException("Can not find any response .")
                return responseBody
            } catch (e: Exception) {
                val useTime = System.currentTimeMillis() - startTime
                logger.error("#Response : $url #Method : GET  #use time(ms) : $useTime #Result : ${e.message}");
                throw e
            }
        }

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headerMap 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeGetMethod(
            url: String,
            params: Map<String, Any>?,
            headerMap: Map<String, String>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val headers = headerMap?.let { setHeaders { headerMap.forEach { (key, value) -> add(key, value) } } }
            return executeGetMethod(url, params, headers, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic auth username
         * @param basicAuthPassword Basic auth password
         * @param headerMap 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthGetMethod(
            url: String,
            params: Map<String, Any>?,
            headerMap: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {

            val headers = HttpHeaders()
            headerMap?.let {
                headerMap.forEach { (key, value) -> headers.add(key, value) }
            }

            headers.setBasicAuth(basicAuthUserName, basicAuthPassword)

            return executeGetMethod(url, params, headers, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param headerMap 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenGetMethod(
            url: String,
            params: Map<String, Any>?,
            headerMap: Map<String, String>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {

            val headers = HttpHeaders()
            headerMap?.let {
                headerMap.forEach { (key, value) -> headers.add(key, value) }
            }

            headers.setBearerAuth(bearerToken)

            return executeGetMethod(url, params, headers, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeGetMethod(
            url: String,
            params: Map<String, Any>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeGetMethod(url, params, HttpHeaders(), responseType, connTimeout, readTimeout)

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic auth username
         * @param basicAuthPassword Basic auth password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthGetMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthGetMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, responseType, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenGetMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenGetMethod(
            url, params, null, bearerToken, responseType, connTimeout, readTimeout
        )


        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeGetMethod(
            url: String, responseType: Class<T>, connTimeout: Int = 10000, readTimeout: Int = 4000
        ) = executeGetMethod(url, null, HttpHeaders(), responseType, connTimeout, readTimeout)

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param basicAuthUserName Basic auth username
         * @param basicAuthPassword Basic auth password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthGetMethod(
            url: String,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthGetMethod(
            url, null, null, basicAuthUserName, basicAuthPassword, responseType, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenGetMethod(
            url: String,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenGetMethod(
            url, null, null, bearerToken, responseType, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeGetMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            connTimeout: Int,
            readTimeout: Int = 4000
        ) = executeGetMethod(url, params, headers, JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param basicAuthUserName Basic auth username
         * @param basicAuthPassword Basic auth password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthGetMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int,
            readTimeout: Int = 4000
        ) = executeBasicAuthGetMethod(
            url, params, headers, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenGetMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            bearerToken: String,
            connTimeout: Int,
            readTimeout: Int = 4000
        ) = executeBearerTokenGetMethod(
            url, params, headers, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeGetMethod(
            url: String, params: Map<String, Any>?, connTimeout: Int = 10000, readTimeout: Int = 4000
        ) = executeGetMethod(url, params, HttpHeaders(), JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic auth username
         * @param basicAuthPassword Basic auth password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthGetMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthGetMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenGetMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenGetMethod(
            url, params, null, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeGetMethod(url: String, connTimeout: Int = 10000, readTimeout: Int = 4000) =
            executeGetMethod(url, null, HttpHeaders(), JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param basicAuthUserName Basic auth username
         * @param basicAuthPassword Basic auth password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthGetMethod(
            url: String,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthGetMethod(
            url, null, null, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起GET請求
         *
         * @param url 請求路徑
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenGetMethod(
            url: String,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenGetMethod(
            url, null, null, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )


        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executePostMethod(
            url: String,
            params: Map<String, Any>?,
            headers: HttpHeaders?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val startTime = System.currentTimeMillis()
            logger.info("#Request : $url #Method : POST #Param : $headers")

            try {
                val httpEntity = HttpEntity(params, headers)
                val response = getRestTemplate(connTimeout, readTimeout).exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    responseType
                )

                val useTime = System.currentTimeMillis() - startTime
                logger.info("#Response : $url #Method : POST  #use time(ms) : $useTime #Result : ${response.body}")

                val responseBody = response.body ?: throw RuntimeException("Can not find any response .")
                return responseBody
            } catch (e: Exception) {
                val useTime = System.currentTimeMillis() - startTime
                logger.error("#Response : $url #Method : POST  #use time(ms) : $useTime #Result : ${e.message}");
                throw e
            }
        }

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executePostMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val headerEntity = headers?.let {
                setHeaders { headers.forEach { (key, value) -> add(key, value) } }
            }

            return executePostMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthPostMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val headerEntity = HttpHeaders()
            headers?.let {
                headers.forEach { (key, value) -> headerEntity.add(key, value) }
            }

            headerEntity.setBasicAuth(basicAuthUserName, basicAuthPassword)

            return executePostMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenPostMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val headerEntity = HttpHeaders()
            headers?.let {
                headers.forEach { (key, value) -> headerEntity.add(key, value) }
            }

            headerEntity.setBearerAuth(bearerToken)

            return executePostMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executePostMethod(
            url: String,
            params: Map<String, Any>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executePostMethod(url, params, HttpHeaders(), responseType, connTimeout, readTimeout)

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthPostMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthPostMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, responseType, connTimeout, readTimeout
        )

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenPostMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenPostMethod(
            url, params, null, bearerToken, responseType, connTimeout, readTimeout
        )

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executePostMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executePostMethod(url, params, headers, JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthPostMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthPostMethod(
            url, params, headers, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenPostMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenPostMethod(
            url, params, headers, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executePostMethod(
            url: String,
            params: Map<String, Any>?,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executePostMethod(url, params, HttpHeaders(), JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthPostMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthPostMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起POST請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenPostMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenPostMethod(
            url, params, null, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )


        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executePutMethod(
            url: String,
            params: Map<String, Any>?,
            headers: HttpHeaders?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val startTime = System.currentTimeMillis()
            logger.info("#Request : $url #Method : PUT #Param : $headers")

            try {
                val httpEntity = HttpEntity(params, headers)
                val response = getRestTemplate(connTimeout, readTimeout).exchange(
                    url,
                    HttpMethod.PUT,
                    httpEntity,
                    responseType
                )

                val useTime = System.currentTimeMillis() - startTime
                logger.info("#Response : $url #Method : PUT  #use time(ms) : $useTime #Result : ${response.body}")

                val responseBody = response.body ?: throw RuntimeException("Can not find any response .")
                return responseBody
            } catch (e: Exception) {
                val useTime = System.currentTimeMillis() - startTime
                logger.error("#Response : $url #Method : PUT  #use time(ms) : $useTime #Result : ${e.message}");
                throw e
            }
        }

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executePutMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val headerEntity = headers?.let {
                setHeaders { headers.forEach { (key, value) -> add(key, value) } }
            }

            return executePutMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthPutMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val headerEntity = HttpHeaders()
            headers?.let {
                headers.forEach { (key, value) -> headerEntity.add(key, value) }
            }

            headerEntity.setBasicAuth(basicAuthUserName, basicAuthPassword)

            return executePutMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenPutMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val headerEntity = HttpHeaders()
            headers?.let { headers.forEach { (key, value) -> headerEntity.add(key, value) } }
            headerEntity.setBearerAuth(bearerToken)
            return executePutMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executePutMethod(
            url: String,
            params: Map<String, Any>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executePutMethod(url, params, HttpHeaders(), responseType, connTimeout, readTimeout)

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthPutMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthPutMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, responseType, connTimeout, readTimeout
        )

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenPutMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenPutMethod(
            url, params, null, bearerToken, responseType, connTimeout, readTimeout
        )

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executePutMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executePutMethod(url, params, headers, JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthPutMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthPutMethod(
            url, params, headers, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenPutMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenPutMethod(
            url, params, headers, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executePutMethod(
            url: String,
            params: Map<String, Any>?,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executePutMethod(url, params, HttpHeaders(), JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthPutMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthPutMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起PUT請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenPutMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenPutMethod(
            url, params, null, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )


        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeDelMethod(
            url: String,
            params: Map<String, Any>?,
            headers: HttpHeaders?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {
            val startTime = System.currentTimeMillis()

            logger.info("#Request : $url #Method : Del #Param : $headers")

            var tempUrl = "$url?"
            params?.let { paramsMap ->
                paramsMap.keys.forEach { key -> tempUrl += "$key=${paramsMap[key]}&" }
                tempUrl = url.substring(0, url.length - 1)
            }

            try {
                val httpEntity = HttpEntity(params, headers)
                val response = getRestTemplate(connTimeout, readTimeout).exchange(
                    tempUrl,
                    HttpMethod.DELETE,
                    httpEntity,
                    responseType
                )

                val useTime = System.currentTimeMillis() - startTime
                logger.info("#Response : $url #Method : Del  #use time(ms) : $useTime #Result : ${response.body}")

                val responseBody = response.body ?: throw RuntimeException("Can not find any response .")
                return responseBody
            } catch (e: Exception) {
                val useTime = System.currentTimeMillis() - startTime
                logger.error("#Response : $url #Method : Del  #use time(ms) : $useTime #Result : ${e.message}");
                throw e
            }
        }

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeDelMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {

            val headerEntity = headers?.let {
                setHeaders { headers.forEach { (key, value) -> add(key, value) } }
            }

            return executeDelMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthDelMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {

            val headerEntity = HttpHeaders()
            headers?.let { headers.forEach { (key, value) -> headerEntity.add(key, value) } }
            headerEntity.setBasicAuth(basicAuthUserName, basicAuthPassword)

            return executeDelMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenDelMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ): T {

            val headerEntity = HttpHeaders()
            headers?.let { headers.forEach { (key, value) -> headerEntity.add(key, value) } }
            headerEntity.setBearerAuth(bearerToken)

            return executeDelMethod(url, params, headerEntity, responseType, connTimeout, readTimeout)
        }

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeDelMethod(
            url: String,
            params: Map<String, Any>?,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeDelMethod(url, params, HttpHeaders(), responseType, connTimeout, readTimeout)

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthDelMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthDelMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, responseType, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenDelMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenDelMethod(
            url, params, null, bearerToken, responseType, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeDelMethod(
            url: String, responseType: Class<T>, connTimeout: Int = 10000, readTimeout: Int = 4000
        ) = executeDelMethod(url, null, HttpHeaders(), responseType, connTimeout, readTimeout)

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBasicAuthDelMethod(
            url: String,
            basicAuthUserName: String,
            basicAuthPassword: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthDelMethod(
            url, null, null, basicAuthUserName, basicAuthPassword, responseType, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param bearerToken Oauth Bearer token
         * @param responseType 返回值類型
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun <T> executeBearerTokenDelMethod(
            url: String,
            bearerToken: String,
            responseType: Class<T>,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenDelMethod(
            url, null, null, bearerToken, responseType, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeDelMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            connTimeout: Int,
            readTimeout: Int = 4000
        ) = executeDelMethod(url, params, headers, JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthDelMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int,
            readTimeout: Int = 4000
        ) = executeBasicAuthDelMethod(
            url, params, headers, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param headers 請求頭參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenDelMethod(
            url: String,
            params: Map<String, Any>?,
            headers: Map<String, String>?,
            bearerToken: String,
            connTimeout: Int,
            readTimeout: Int = 4000
        ) = executeBearerTokenDelMethod(
            url, params, headers, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeDelMethod(
            url: String, params: Map<String, Any>?, connTimeout: Int = 10000, readTimeout: Int = 4000
        ) = executeDelMethod(url, params, HttpHeaders(), JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthDelMethod(
            url: String,
            params: Map<String, Any>?,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthDelMethod(
            url, params, null, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param params 請求參數
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenDelMethod(
            url: String,
            params: Map<String, Any>?,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenDelMethod(
            url, params, null, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeDelMethod(url: String, connTimeout: Int = 10000, readTimeout: Int = 4000) =
            executeDelMethod(url, null, HttpHeaders(), JSONObject::class.java, connTimeout, readTimeout)

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param basicAuthUserName Basic Auth Username
         * @param basicAuthPassword Basic Auth Password
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBasicAuthDelMethod(
            url: String,
            basicAuthUserName: String,
            basicAuthPassword: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBasicAuthDelMethod(
            url, null, null, basicAuthUserName, basicAuthPassword, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 發起Del請求
         *
         * @param url 請求路徑
         * @param bearerToken Oauth Bearer token
         * @param connTimeout 連接超時時間（毫秒）
         * @param readTimeout 讀取超時時間（毫秒）
         */
        fun executeBearerTokenDelMethod(
            url: String,
            bearerToken: String,
            connTimeout: Int = 10000,
            readTimeout: Int = 4000
        ) = executeBearerTokenDelMethod(
            url, null, null, bearerToken, JSONObject::class.java, connTimeout, readTimeout
        )

        /**
         * 設置請求頭
         */
        private fun setHeaders(block: HttpHeaders.() -> Unit) = HttpHeaders().apply(block)

        /**
         * 獲取RestTemplate請求對象
         */
        private fun getRestTemplate(connectionTimeout: Int, readTimeout: Int): RestTemplate {

            val requestFactory = SimpleClientHttpRequestFactory()
            requestFactory.setConnectTimeout(connectionTimeout)
            requestFactory.setReadTimeout(readTimeout)

            return RestTemplate(requestFactory)
        }
    }

    private var url: String? = null

    private val headers: HttpHeaders = HttpHeaders()

    private val params: MutableMap<String, Any> = HashMap()

    private var connectionTimeout = 10000

    private var readTimeout = 100000

    /**
     * 設置請求路徑
     *
     * @param url 請求路徑
     */
    fun setUrl(url: String): CommonHttpRestTemplate {
        this.url = url
        return this
    }

    /**
     * 設置basic auth 的請求頭
     *
     * @param username 用戶名
     * @param password 密碼
     */
    fun setBasicAuth(username: String, password: String): CommonHttpRestTemplate {
        headers.setBasicAuth(username, password)
        return this
    }

    /**
     * 設置連接超時時間
     *
     * @param connectionTimeout 連接超時時間（毫秒）
     */
    fun setConnectTimeout(connectionTimeout: Int): CommonHttpRestTemplate {

        this.connectionTimeout = connectionTimeout

        return this
    }

    /**
     * 設置讀取超時時間
     *
     * @param readTimeout 讀取超時時間（毫秒）
     */
    fun setReadTimeout(readTimeout: Int): CommonHttpRestTemplate {

        this.readTimeout = readTimeout

        return this
    }

    /**
     * 添加請求參數
     *
     * @param key 參數名
     * @param value 參數值
     */
    fun addParam(key: String, value: Any): CommonHttpRestTemplate {
        this.params[key] = value
        return this
    }

    /**
     * 添加請求頭參數
     *
     * @param key 參數名
     * @param value 參數值
     */
    fun addHeader(key: String, value: String): CommonHttpRestTemplate {
        this.headers.add(key, value)
        return this
    }

    /**
     * 設置Basic 的授權模式
     *
     * @param username 用戶名
     * @param password 密碼
     */
    fun addBasicAuth(username: String, password: String): CommonHttpRestTemplate {
        headers.setBasicAuth(username, password)
        return this
    }

    /**
     * 設置Oauth Bearer token 的授權模式
     *
     * @param token 訪問令牌
     */
    fun addBearerAuth(token: String): CommonHttpRestTemplate {
        headers.setBearerAuth(token)
        return this
    }

    /**
     * 執行 GET 請求
     */
    fun executeGet(): JSONObject {

        val startTime = System.currentTimeMillis()
        logger.info("#Request : $url #Method : GET #Param : ")

        try {
            val url = parseParamsToUrlString()
            val ans = getRestTemplate().exchange(url, HttpMethod.GET, HttpEntity(null, headers), String::class.java)
            val responseBody = JSONObject.parseObject(ans.body, JSONObject::class.java)

            val useTime = System.currentTimeMillis() - startTime
            logger.info("#Response : $url #Method : GET  #use time(ms) : $useTime #Result : $responseBody")
            return responseBody
        } catch (e: Exception) {
            val useTime = System.currentTimeMillis() - startTime
            logger.error("#Response : " + url + " #Method : GET  #use time(ms) : " + useTime + " #Result : " + e.message)
            throw e
        }
    }

    /**
     * 執行 Post 請求
     */
    fun executePost(): JSONObject {

        val startTime = System.currentTimeMillis()
        logger.info("#Request : $url #Method : POST #Param : ")

        try {
            val url = url ?: throw RuntimeException("Url must not be null .")
            val ans = getRestTemplate().exchange(url, HttpMethod.POST, HttpEntity(params, headers), String::class.java)
            val responseBody = JSONObject.parseObject(ans.body, JSONObject::class.java)

            val useTime = System.currentTimeMillis() - startTime
            logger.info("#Response : $url #Method : POST  #use time(ms) : $useTime #Result : $responseBody")

            return responseBody
        } catch (e: Exception) {
            val useTime = System.currentTimeMillis() - startTime
            logger.error("#Response : " + url + " #Method : POST  #use time(ms) : " + useTime + " #Result : " + e.message)
            throw e
        }
    }

    /**
     * 執行 Put 請求
     */
    fun executePut(): JSONObject {

        val startTime = System.currentTimeMillis()
        logger.info("#Request : $url #Method : PUT #Param : ")

        try {
            val url = url ?: throw RuntimeException("Url must not be null .")
            val ans = getRestTemplate().exchange(url, HttpMethod.PUT, HttpEntity(params, headers), String::class.java)
            val responseBody = JSONObject.parseObject(ans.body, JSONObject::class.java)

            val useTime = System.currentTimeMillis() - startTime
            logger.info("#Response : $url #Method : PUT  #use time(ms) : $useTime #Result : $responseBody")

            return responseBody
        } catch (e: Exception) {
            val useTime = System.currentTimeMillis() - startTime
            logger.error("#Response : " + url + " #Method : PUT  #use time(ms) : " + useTime + " #Result : " + e.message)
            throw e
        }
    }

    /**
     * 執行 delete 請求
     */
    fun executeDel(): JSONObject {

        val startTime = System.currentTimeMillis()
        logger.info("#Request : $url #Method : DELETE #Param : ")

        try {
            val url = parseParamsToUrlString()
            val ans = getRestTemplate().exchange(url, HttpMethod.DELETE, HttpEntity(null, headers), String::class.java)
            val responseBody = JSONObject.parseObject(ans.body, JSONObject::class.java)

            val useTime = System.currentTimeMillis() - startTime
            logger.info("#Response : $url #Method : DELETE  #use time(ms) : $useTime #Result : $responseBody")
            return responseBody
        } catch (e: Exception) {
            val useTime = System.currentTimeMillis() - startTime
            logger.error("#Response : " + url + " #Method : DELETE  #use time(ms) : " + useTime + " #Result : " + e.message)
            throw e
        }
    }

    private fun getRestTemplate(): RestTemplate {

        val requestFactory = SimpleClientHttpRequestFactory()
        requestFactory.setConnectTimeout(connectionTimeout)
        requestFactory.setReadTimeout(readTimeout)

        return RestTemplate(requestFactory)
    }

    private fun parseParamsToUrlString(): String {

        var url = url ?: throw RuntimeException("Url must not be null .")

        url += "?"

        params.keys.forEach { key -> url += "$key=${params[key]}&" }

        url = url.substring(0, url.length - 1)

        return url
    }
}