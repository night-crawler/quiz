package fm.force.quiz.filter

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SampleFilter : Filter {
    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        logger.debug("Hello, I am a filter, and my filtering precedes any interceptor")
        chain?.doFilter(request, response)
    }
}
