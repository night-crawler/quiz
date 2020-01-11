package fm.force.quiz.common

import org.springframework.data.jpa.domain.Specification
import javax.persistence.metamodel.SingularAttribute


class SpecificationBuilder {
    companion object {
        fun <T, F> fk(prop0: () -> F, attr: SingularAttribute<T, F>) =
                Specification<T> { root, _, builder ->
                    builder.equal(root[attr], prop0())
                }

        fun <T> equals(value: Boolean, attr: SingularAttribute<T, Boolean>) =
                Specification<T> { root, _, builder ->
                    builder.equal(root[attr], value)
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
