package fm.force.quiz.security.entity

import io.kotlintest.matchers.string.shouldHaveMinLength
import io.kotlintest.specs.WordSpec

open class BaseEntityTest : WordSpec({
    val baseEntity = BaseEntity()
    "BaseEntity" When {
        "called toString() method" should {
            "does not fail" {
                // id has null value now, but it does not fail somehow
                baseEntity.toString() shouldHaveMinLength 1
            }
        }
    }
})
