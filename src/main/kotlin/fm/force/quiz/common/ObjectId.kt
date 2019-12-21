package fm.force.quiz.common

import java.security.SecureRandom
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class ObjectId {
    companion object {
        private val localEpoch = LocalDateTime.parse("2019-12-01T00:00:00").atZone(ZoneId.of("UTC"))
                .toInstant().toEpochMilli()

        private const val totalBits = 63
        private const val timestampBits = 40
        private const val sequenceBits = totalBits - timestampBits

        private const val maxLocalTimestamp = (1L shl timestampBits) - 1
        private const val maxSequenceNum = (1 shl sequenceBits) - 1

        val maxEffectiveInstant: Instant = Instant.ofEpochMilli(localEpoch + maxLocalTimestamp)
        val minEffectiveInstant: Instant = Instant.ofEpochMilli(localEpoch)

        private fun of(localMillis: Long, seq: Long) = (localMillis shl sequenceBits) or seq

        fun of(ts: Instant): Long {
            val localMillis = ts.toEpochMilli() - localEpoch

            if (localMillis < 0)
                throw IllegalArgumentException("Provided timestamp $ts is less than min $minEffectiveInstant")
            if (localMillis > maxLocalTimestamp)
                throw IllegalArgumentException("Provided timestamp $ts is greater than max $maxEffectiveInstant")

            return of(localMillis, SecureRandom().nextInt(maxSequenceNum).toLong())
        }

        fun nextSequence(objectId: Long): Long {
            if (objectId < 0)
                throw IllegalArgumentException("id must be positive")

            return of(objectId shr sequenceBits, SecureRandom().nextInt(maxSequenceNum).toLong())
        }

        fun now() = of(Instant.now())
    }
}
