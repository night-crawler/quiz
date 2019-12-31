package fm.force.quiz.configuration

import fm.force.quiz.core.repository.CustomJpaRepositoryImpl
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(repositoryBaseClass = CustomJpaRepositoryImpl::class)
class ApplicationConfiguration {
}