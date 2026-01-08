package com.survivalcoding.ai_court.data.api

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.concurrent.ConcurrentHashMap

/**
 * 서버가 세션(JSESSIONID) 기반이면 이게 거의 필수.
 * 앱 프로세스 살아있는 동안 host 별로 쿠키 저장/재사용.
 */

class SessionCookieJar: CookieJar{
    private val store = ConcurrentHashMap<String, List<Cookie>>() // key = host

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        store[url.host] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return store[url.host].orEmpty()
    }
}