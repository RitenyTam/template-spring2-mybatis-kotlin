package com.riteny.util.exception.internationalization

interface InternationalizationDatasource {

    companion object {
        val internationalizationProfileMap: MutableMap<String, MutableMap<String, String>> = HashMap()
    }

    /**
     * 傳入對應的異常内容index
     * 從數據源内獲取到對應的國際化文本
     *
     * @param index 異常内容的索引
     * @return 異常内容的國際化文本
     */
    fun getValue(index: String, lang: String): String {

        val contextMap = internationalizationProfileMap.get(lang)

        return contextMap?.get(index) ?: index
    }
}