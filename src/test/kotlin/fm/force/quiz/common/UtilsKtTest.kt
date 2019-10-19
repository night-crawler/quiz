package fm.force.quiz.common

import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.specs.*


open class UtilsKtTest : WordSpec({
    "getRandomString" should {
        "generate random string" {
            val str = getRandomString(8)
            str.shouldHaveLength(8)
        }
    }
})
