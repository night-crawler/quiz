package fm.force.quiz

import fm.force.quiz.core.repository.CustomJpaRepositoryImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl::class)
@EnableTransactionManagement
class QuizApplication

fun main(args: Array<String>) {
    runApplication<QuizApplication>(*args)
}
