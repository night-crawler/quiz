package io.kotlintest.provided.fm.force.quiz.common

import fm.force.quiz.common.ObjectId
import io.kotlintest.data.forall
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import io.kotlintest.shouldThrow
import io.kotlintest.tables.row
import java.lang.IllegalArgumentException

open class ObjectIdTest : StringSpec() {
    init {
        "should check for boundaries" {
            forall(
                    row(ObjectId.maxEffectiveInstant.plusSeconds(1)),
                    row(ObjectId.minEffectiveInstant.minusSeconds(1))
            ) {
                shouldThrow<IllegalArgumentException> { ObjectId.of(it) }
            }

            forall(
                    row(-1L)
            ) {
                shouldThrow<IllegalArgumentException> { ObjectId.nextSequence(it) }
            }
        }

        "should tolerate boundaries" {
            // least significant 23 bits are used to store random sequence
            val minObjectId = ObjectId.of(ObjectId.minEffectiveInstant)
            ((minObjectId shr 23) shl 23) shouldBe 0

            // most significant 40 bits are used to store the timestamp
            val maxObjectId = ObjectId.of(ObjectId.maxEffectiveInstant)
            ((maxObjectId shr 23) shl 23).toString(2).count { it == '1' } shouldBe 40

            // after replacing of random sequence with a new one, the most significant bits must remain the same
            val nextMaxObjectId = ObjectId.nextSequence(maxObjectId)
            ((nextMaxObjectId shr 23) shl 23).toString(2).count { it == '1' } shouldBe 40

            // hopefully, it should not
            maxObjectId shouldNotBe nextMaxObjectId

            ObjectId.now() shouldNotBe null
        }
    }
}