package fm.force.quiz.common

import java.util.*


val ALPHABET = ('a'..'z') + ('A'..'Z') + ('0'..'9')


fun getRandomString(len: Int = 8, alphabet: List<Char> = ALPHABET) =
        (1..len)
                .map { kotlin.random.Random.nextInt(0, alphabet.size) }
                .map(alphabet::get)
                .joinToString("")


fun Long.toBinaryString(): String = BitSet.valueOf(longArrayOf(this)).let { bs ->
    (63 downTo 0).joinToString("") {
        if (bs[it]) "1" else "0"
    }
}

fun Long.mostSignificantBit(): Int {
    var count = 0
    var n = this

    while (n != 0L && n != -1L) {
        n = n shr 1
        count++
    }
    return count
}

fun Long.maxSignificantValue(): Long {
    return (1L shl mostSignificantBit()) - 1
}

fun <T> Collection<T>?.toCustomSet(): Set<T> =
        this?.map { it }?.toSet() ?: setOf()
