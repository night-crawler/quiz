package fm.force.quiz.core.service

import com.github.slugify.Slugify
import org.springframework.stereotype.Service

@Service
class SlugifyService {
    private val slugifier = Slugify()
    fun slugify(text: String): String = slugifier.slugify(text)
}
