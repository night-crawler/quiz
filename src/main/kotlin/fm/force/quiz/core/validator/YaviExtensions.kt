package fm.force.quiz.core.validator

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnCondition
import am.ik.yavi.core.ConstraintCondition
import fm.force.quiz.common.toCustomSet
import fm.force.quiz.core.repository.CommonRepository
import java.time.Instant
import java.util.function.Predicate
import kotlin.reflect.KProperty1
import org.springframework.data.jpa.repository.JpaRepository

fun <T> notNullCondition(property: KProperty1<T, Any?>) =
    ConstraintCondition<T> { dto, _ -> property(dto) != null }

fun <T, E : Any?> ValidatorBuilder<T>.mandatory(
    property: KProperty1<T, E>,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = "${property.name} must be present"
    val predicate = Predicate<T> { property(it) != null }
    constraintOnTarget(predicate, property.name, "", msgError)
    chain(this)
    return this
}

fun <T, E, C : Collection<E>?> ValidatorBuilder<T>.optionalSubset(
    sup: KProperty1<T, C>,
    sub: KProperty1<T, C>,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val errorMessage = "${sub.name} must be a subset of ${sup.name}"

    val predicate = Predicate<T> {
        val maybeSuper = sup(it)
        val maybeSub = sub(it)
        if (maybeSuper == null || maybeSub == null) {
            true
        } else {
            val superSet = (maybeSuper as Collection<*>).toSet()
            val subSet = (maybeSub as Collection<*>).toSet()

            (subSet - superSet).isEmpty()
        }
    }

    konstraintOnCondition(notNullCondition(sup)) {
        konstraintOnCondition(notNullCondition(sub)) {
            constraintOnTarget(predicate, sub.name, "", errorMessage)
            chain(this)
        }
    }

    return this
}

fun <T, ID : Long?, R> ValidatorBuilder<T>.ownedFkConstraint(
    property: KProperty1<T, ID>,
    repository: CommonRepository<R>,
    getOwnerId: () -> Long,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgWrongFk = "Must be greater than zero"
    val msgDoesNotExist = "Must exist and belong to you"
    val predicate = Predicate<T> {
        val value = property(it)
        if (value == null) true
        else repository.existsByIdAndOwnerId(value, getOwnerId())
    }

    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThan(0).message(msgWrongFk)
        }
        constraintOnTarget(predicate, property.name, "", msgDoesNotExist)
        chain(this)
    }
    return this
}

fun <T, R> ValidatorBuilder<T>.fkConstraint(
    property: KProperty1<T, Long>,
    repository: JpaRepository<R, Long>,
    predicate: Predicate<T> = Predicate { repository.existsById(property(it)) },
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgWrongFk = "Must be greater than zero"
    val msgDoesNotExist = "Must exist"

    konstraint(property) { greaterThan(0).message(msgWrongFk) }
    constraintOnTarget(predicate, property.name, "", msgDoesNotExist)

    chain(this)
    return this
}

fun <T, R : String?> ValidatorBuilder<T>.stringConstraint(
    property: KProperty1<T, R>,
    range: ClosedRange<Int>,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = "Provide a string with length in range $range"

    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThanOrEqual(range.start).message(msgError)
            lessThanOrEqual(range.endInclusive).message(msgError)
        }
        chain(this)
    }
    return this
}

fun <T, V : Int?> ValidatorBuilder<T>.intConstraint(
    property: KProperty1<T, V>,
    range: ClosedRange<Int>,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = "Provide an integer in range $range"

    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThanOrEqual(range.start).message(msgError)
            lessThanOrEqual(range.endInclusive).message(msgError)
        }
        chain(this)
    }
    return this
}

fun <T, E, C : Collection<E>?> ValidatorBuilder<T>.collectionConstraint(
    property: KProperty1<T, C>,
    range: ClosedRange<Int>,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = "Provide $range items for the field"
    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThanOrEqual(range.start).message(msgError)
            lessThanOrEqual(range.endInclusive).message(msgError)
        }
        chain(this)
    }
    return this
}

fun <T, C : Collection<Long>?, R> ValidatorBuilder<T>.fksConstraint(
    property: KProperty1<T, C>,
    repository: JpaRepository<R, Long>,
    range: ClosedRange<Int>,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgSomeItemsAreMissing = "Some of entered items do not exist"
    val whenSomeItemsDoNotExist = Predicate<T> {
        val value = property(it)
        if (value.isNullOrEmpty()) true
        else repository.findAllById(value).toSet() == value.toCustomSet()
    }

    collectionConstraint(property, range) {
        constraintOnTarget(whenSomeItemsDoNotExist, property.name, "custom.fksConstraint", msgSomeItemsAreMissing)
        forEach(property, property.name, fkValidator)
        chain(this)
    }

    return this
}

fun <T, C : Collection<Long>?, R> ValidatorBuilder<T>.ownedFksConstraint(
    property: KProperty1<T, C>,
    repository: CommonRepository<R>,
    range: ClosedRange<Int>,
    getOwnerId: () -> Long,
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgSomeItemsAreMissing = "Some of entered items are missing or do not belong to your user"
    val whenSomeItemsDoNotExist = Predicate<T> {
        val value = property(it)
        if (value.isNullOrEmpty()) true
        else repository.findOwnedIds(value, getOwnerId()).toSet() == value.toCustomSet()
    }

    collectionConstraint(property, range) {
        constraintOnTarget(whenSomeItemsDoNotExist, property.name, "", msgSomeItemsAreMissing)
        forEach(property, property.name, fkValidator)
        chain(this)
    }

    return this
}

fun <T, R : Instant?> ValidatorBuilder<T>.instantConstraint(
    property: KProperty1<T, R>,
    getStart: () -> Instant = Instant::now,
    getEnd: () -> Instant = { Instant.MAX },
    errorTemplate: String = "Instant value {1} of field {0} is illegal",
    chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val whenTimeIsBad = Predicate<T> {
        val value = property(it)
        if (value == null) true
        else getStart() <= value && value <= getEnd()
    }

    konstraintOnCondition(notNullCondition(property)) {
        constraintOnTarget(whenTimeIsBad, property.name, "custom.instant", errorTemplate)
        chain(this)
    }
    return this
}

val fkValidator = ValidatorBuilder.of(Long::class.java)
    .constraint(Long::toLong, "") {
        it.greaterThan(0).message("Must be greater than zero")
    }
    .build()

val nonEmptyString = ValidatorBuilder.of(String::class.java)
    .constraint(String::toString, "") {
        it.notEmpty().message("String must not be empty")
    }
    .build()
