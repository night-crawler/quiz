package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.extensions.ProjectLevelExtension
import io.kotlintest.spring.SpringAutowireConstructorExtension
import io.kotlintest.spring.SpringListener

object ProjectConfig : AbstractProjectConfig() {
    private var started: Long = 0
    override fun listeners() = listOf(SpringListener)
    override fun extensions(): List<ProjectLevelExtension> = listOf(SpringAutowireConstructorExtension)

    override fun beforeAll() {
        started = System.currentTimeMillis()
    }

    override fun afterAll() {
        val time = System.currentTimeMillis() - started
        println("overall time [ms]: $time")
    }

    override fun parallelism(): Int = 1
}
