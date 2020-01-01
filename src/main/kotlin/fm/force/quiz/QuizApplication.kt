package fm.force.quiz

import fm.force.quiz.core.repository.CustomJpaRepositoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl::class)
class QuizApplication

fun main(args: Array<String>) {
	runApplication<QuizApplication>(*args)
}
