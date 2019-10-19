package fm.force.quiz.common

val ALPHABET = ('a'..'z') + ('A'..'Z') + ('0'..'9')


fun getRandomString(len: Int = 8) =
        (1..len)
                .map { kotlin.random.Random.nextInt(0, ALPHABET.size) }
                .map(ALPHABET::get)
                .joinToString("");
