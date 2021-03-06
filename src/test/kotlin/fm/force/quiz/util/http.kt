package fm.force.quiz.util

import io.kotlintest.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.web.servlet.ResultHandler

val expectOkOrPrint = ResultHandler { result ->
    val statusCode = result.response.status
    val statusSeries = HttpStatus.Series.valueOf(statusCode)
    if (statusSeries != HttpStatus.Series.SUCCESSFUL) {
        println("$statusCode :: $statusSeries :: ${result.request.pathInfo}")
        println(result.response.contentAsString)
        println(JMapper.pformat(result.response.contentAsString))
    }
    statusSeries shouldBe HttpStatus.Series.SUCCESSFUL
}

val pprint = ResultHandler { result ->
    val statusCode = result.response.status
    val statusSeries = HttpStatus.Series.valueOf(statusCode)
    println("$statusCode :: $statusSeries :: ${result.request.pathInfo}")
    println(JMapper.pformat(result.response.contentAsString))
}

inline fun <reified T> MockHttpServletResponse.toDTO() = JMapper.fromString<T>(contentAsString)

inline fun <reified K, reified V> MockHttpServletResponse.toMap() = JMapper.toMap<K, V>(contentAsString)

val MockHttpServletResponse.entityId get() = JMapper.toMap<String, String>(contentAsString)["id"]!!.toLong()
