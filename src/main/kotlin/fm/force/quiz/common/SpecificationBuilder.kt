package fm.force.quiz.common

import org.springframework.data.jpa.domain.Specification
import javax.persistence.metamodel.SingularAttribute
import kotlin.reflect.KProperty0

class SpecificationBuilder {
    companion object {
        fun <T, F> fk(prop0: KProperty0<F>, attr: SingularAttribute<T, F>) =
                Specification<T> { root, _, builder ->
                    builder.equal(root[attr], prop0())
                }

        fun <T> ciEquals(needle: String, attr: SingularAttribute<T, String>) =
                Specification<T> { root, _, builder ->
                    builder.equal(builder.lower(root[attr]), needle.toLowerCase())
                }

        fun <T> ciStartsWith(needle: String, attr: SingularAttribute<T, String>) =
                Specification<T> { root, _, builder ->
                    builder.like(builder.lower(root[attr]), "${needle.toLowerCase()}%")
                }

        fun <T> ciEndsWith(needle: String, attr: SingularAttribute<T, String>) =
                Specification<T> { root, _, builder ->
                    builder.like(builder.lower(root[attr]), "%${needle.toLowerCase()}")
                }

        fun <T> ciContains(needle: String, attr: SingularAttribute<T, String>) =
                Specification<T> { root, _, builder ->
                    builder.like(builder.lower(root[attr]), "%${needle.toLowerCase()}%")
                }
    }
}
