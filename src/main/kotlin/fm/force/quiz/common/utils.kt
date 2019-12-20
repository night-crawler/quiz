package fm.force.quiz.common

import java.time.Instant
import java.util.*
import kotlin.math.log2
import kotlin.math.nextUp

val ALPHABET = ('a'..'z') + ('A'..'Z') + ('0'..'9')


fun getRandomString(len: Int = 8) =
        (1..len)
                .map { kotlin.random.Random.nextInt(0, ALPHABET.size) }
                .map(ALPHABET::get)
                .joinToString("")

fun Long.toBinaryString(): String = BitSet.valueOf(longArrayOf(this)).let { bs ->
    (63 downTo 0).joinToString("") {
        if (bs[it]) "1" else "0"
    }
}

fun Long.mostSignificantBit(): Int {
    var count = 0
    var n = this

    while(n != 0L && n != -1L) {
        n = n shr 1
        count++
        if (count > 100)
            break
    }
    return count
}

fun Long.maxSignificant(): Long {
    return (1L shl mostSignificantBit()) - 1
}

class IdGenerator {
    companion object {
        fun valueOf(ts: Instant) {

        }
    }
    fun generate() {
        val a = System.currentTimeMillis()
        val q = Date(a)
    }
}