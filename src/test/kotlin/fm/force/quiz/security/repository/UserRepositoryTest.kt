package fm.force.quiz.security.repository

import fm.force.quiz.security.entity.Role
import fm.force.quiz.security.entity.User
import io.kotlintest.Spec
import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.provided.fm.force.quiz.security.SecurityTestConfiguration
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import org.springframework.test.context.ContextConfiguration


@ContextConfiguration(classes = [SecurityTestConfiguration::class])
open class UserRepositoryTest(
        private val userRepository: UserRepository,
        private val jpaUserRepository: JpaUserRepository,
        private val jpaRoleRepository: JpaRoleRepository
) : WordSpec() {

    private lateinit var adminRole: Role
    private lateinit var teacherRole: Role
    private lateinit var studentRole: Role

    private lateinit var testUsers: List<User>

    override fun beforeSpec(spec: Spec) {
        adminRole = jpaRoleRepository.findByName("ADMIN")!!
        teacherRole = jpaRoleRepository.findByName("TEACHER")!!
        studentRole = jpaRoleRepository.findByName("STUDENT")!!

        testUsers = listOf(
                User(username = "vasya", email = "vasya@force.fm"),

                User(username = "admin", password = "admin", email = "admin@force.fm", roles = listOf(adminRole)),

                User(username = "student-1", password = "student-1", email = "student-1@force.fm", roles = listOf(studentRole)),
                User(username = "student-2", password = "student-2", email = "student-2@force.fm", roles = listOf(studentRole)),

                User(username = "teacher-1", password = "teacher-1", email = "teacher-1@force.fm", roles = listOf(teacherRole)),
                User(username = "teacher-2", password = "teacher-2", email = "teacher-2@force.fm", roles = listOf(teacherRole))
        )

        jpaUserRepository.saveAll(testUsers)
    }

    init {
        "UserRepository" should {
            "find users by name" {
                val user = jpaUserRepository.findByEmail("vasya@force.fm")
                user.shouldNotBeNull()
            }

            "find users by id" {
                val user = jpaUserRepository.findById(1)
                user.shouldNotBeNull()
            }

            "find users by roles" {
                val admins = jpaUserRepository.findUsersByRolesName("ADMIN")
                admins.shouldHaveSize(1)

                val students = jpaUserRepository.findUsersByRolesName("STUDENT")
                students.shouldHaveSize(2)
            }

            "find user by email and password" {
                val user = jpaUserRepository.findByEmailAndPassword("admin@force.fm", "admin")
                user.shouldNotBeNull()
            }

            "find users by a list of given roles" {
                val expectedUserRoleNames = setOf("ADMIN", "STUDENT")
                val users = jpaUserRepository.findUsersByRolesNameIn(expectedUserRoleNames)
                val actualUserRoleNames = users.map { it.roles }.flatten().map { it.name }.toSet()
                actualUserRoleNames.shouldBe(expectedUserRoleNames)

                // also we need to have an ability to find users with no roles at all
                val weirdos = jpaUserRepository.findUserByRolesIsEmpty()
                weirdos.shouldHaveSize(1)
                weirdos[0].username.shouldBe("vasya")
            }
        }
    }
}
