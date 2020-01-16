package fm.force.quiz.core.repository

import fm.force.quiz.core.entity.Topic
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository

@Repository
interface TopicRepository : CustomJpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic>, CommonRepository<Topic>
