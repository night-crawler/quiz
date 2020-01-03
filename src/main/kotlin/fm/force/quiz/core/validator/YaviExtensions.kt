package fm.force.quiz.core.validator

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.konstraint
import am.ik.yavi.builder.konstraintOnCondition
import am.ik.yavi.core.ConstraintCondition
import fm.force.quiz.core.repository.CommonRepository
import java.util.*
import java.util.function.Predicate
import kotlin.reflect.KProperty1

fun <T> notNullCondition(property: KProperty1<T, Any?>) = ConstraintCondition<T> { dto, _ -> property(dto) != null }


fun <T, K : Any?> ValidatorBuilder<T>.mandatory(
        property: KProperty1<T, K>,
        errorTemplate: String = "%s must be present",
        locale: Locale = Locale.ENGLISH,
        chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = errorTemplate.format(locale, property.name)
    val predicate = Predicate<T> { property(it) != null }
    constraintOnTarget(predicate, property.name, "", msgError)
    chain(this)
    return this
}


fun <T, I, K : Collection<I>?> ValidatorBuilder<T>.optionalSubset(
        sup: KProperty1<T, K>,
        sub: KProperty1<T, K>,

        errorTemplate: String = "%s must be a subset of %s",
        locale: Locale = Locale.ENGLISH,
        chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val errorMessage = errorTemplate.format(locale, sub.name, sup.name)

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


fun <T, K : Long?, R> ValidatorBuilder<T>.fkConstraint(
        property: KProperty1<T, K>,
        repository: CommonRepository<R>,
        getOwnerId: () -> Long,
        wrongFkTemplate: String = "%s must be positive",
        doesNotExistErrorTemplate: String = "%s length must exist and belong to you",
        locale: Locale = Locale.ENGLISH,
        chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgWrongFk = wrongFkTemplate.format(locale, property.name)
    val msgDoesNotExist = doesNotExistErrorTemplate.format(locale, property.name)
    val a = Predicate<T> {
        val value = property(it)
        if (value == null) true
        else repository.existsByIdAndOwnerId(value, getOwnerId())
    }

    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThan(0).message(msgWrongFk)
        }
        constraintOnTarget(a, property.name, "", msgDoesNotExist)
        chain(this)
    }
    return this
}


fun <T, K : String?> ValidatorBuilder<T>.stringConstraint(
        property: KProperty1<T, K>,
        range: ClosedRange<Int>,
        errorTemplate: String = "%s length must be in range [%d; %d]",
        locale: Locale = Locale.ENGLISH,
        chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = errorTemplate.format(locale, property.name, range.start, range.endInclusive)

    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThanOrEqual(range.start).message(msgError)
            lessThanOrEqual(range.endInclusive).message(msgError)
        }
        chain(this)
    }
    return this
}


fun <T, K : Int?> ValidatorBuilder<T>.intConstraint(
        property: KProperty1<T, K>,
        range: ClosedRange<Int>,
        errorTemplate: String = "%s must be in range [%d; %d]",
        locale: Locale = Locale.ENGLISH,
        chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = errorTemplate.format(locale, property.name, range.start, range.endInclusive)

    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThanOrEqual(range.start).message(msgError)
            lessThanOrEqual(range.endInclusive).message(msgError)
        }
        chain(this)
    }
    return this
}

fun <T, K : Collection<Long>?, R> ValidatorBuilder<T>.fkListConstraint(
        property: KProperty1<T, K>,
        repository: CommonRepository<R>,
        range: ClosedRange<Int>,
        getOwnerId: () -> Long,
        errorTemplate: String = "Provide %d - %d items for the field %s",
        missingItemsTemplate: String = "Some of entered items are missing or do not belong to your user",
        locale: Locale = Locale.ENGLISH,
        chain: ValidatorBuilder<T>.(ValidatorBuilder<T>) -> Unit = { }
): ValidatorBuilder<T> {
    val msgError = errorTemplate.format(locale, range.start, range.endInclusive, property.name)
    val msgSomeItemsAreMissing = missingItemsTemplate.format(locale, repository::class.simpleName)

    val whenSomeItemsDoNotExist = Predicate<T> {
        val value = property(it)
        if (value.isNullOrEmpty()) true
        else repository.findOwnedIds(value, getOwnerId()).toSet() == value
    }

    konstraintOnCondition(notNullCondition(property)) {
        konstraint(property) {
            greaterThanOrEqual(range.start).message(msgError)
            lessThanOrEqual(range.endInclusive).message(msgError)
        }
        forEach(property, property.name, fkValidator)
        constraintOnTarget(whenSomeItemsDoNotExist, property.name, "", msgSomeItemsAreMissing)
        chain(this)
    }
    return this
}

val fkValidator = ValidatorBuilder.of(Long::class.java)
        .constraint(Long::toLong, "") {
            it.greaterThan(0).message("Must be positive")
        }
        .build()

val nonEmptyString = ValidatorBuilder.of(String::class.java)
        .constraint(String::toString, "") {
            it.notEmpty().message("String must not be empty")
        }
        .build()