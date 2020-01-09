package fm.force.quiz.common

import io.kotlintest.matchers.string.shouldHaveLength
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec


open class UtilsKtTest : WordSpec({
    "getRandomString" should {
        "generate random string" {
            val str = getRandomString(8)
            str shouldHaveLength 8
        }
    }

    "toBinaryString" should {
        "produce correct length" {
            val one = 1L
            one.toBinaryString() shouldHaveLength 64

            Long.MAX_VALUE.toBinaryString() shouldHaveLength 64
            Long.MAX_VALUE.toBinaryString().count { it == '1' } shouldBe 63
            Long.MAX_VALUE.toBinaryString().count { it == '0' } shouldBe 1

            Long.MIN_VALUE.toBinaryString() shouldHaveLength 64
            Long.MIN_VALUE.toBinaryString().count { it == '1' } shouldBe 1
            Long.MIN_VALUE.toBinaryString().count { it == '0' } shouldBe 63
        }
    }
})
