import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


plugins {
    val kotlinVersion = "1.3.50"

    idea
    java
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion

    id("org.liquibase.gradle") version "2.0.1"
    id("org.springframework.boot") version "2.1.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("org.asciidoctor.convert") version "1.5.8"
}


group = "fm.force"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

val developmentOnly by configurations.creating
configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    mavenCentral()
}

val snippetsDir by extra { file("build/generated-snippets") }

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Table")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    compile("javax.xml.bind:jaxb-api:2.3.1")
    compile("javax.activation:activation:1.1.1")
    compile("org.glassfish.jaxb:jaxb-runtime:2.3.2")

    compile("org.liquibase:liquibase-core:3.8.0")

    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase.ext:liquibase-hibernate5:3.8")
    liquibaseRuntime("ch.qos.logback:logback-core")
    liquibaseRuntime("ch.qos.logback:logback-classic")
    liquibaseRuntime(sourceSets.getByName("main").compileClasspath)
    liquibaseRuntime(sourceSets.getByName("main").output)
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.springframework.boot:spring-boot:2.1.8.RELEASE")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:2.1.0")


    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.vladmihalcea:hibernate-types-52:2.5.0")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}

if (!project.hasProperty("runList")) {
    project.ext.set("runList", "main")
}

project.ext.set(
        "diffChangelogFile",
        "src/main/resources/db/changelog/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_changelog.xml"
)

liquibase {
    activities.register("main") {
        this.arguments = mapOf(
                "driver" to "org.postgresql.Driver",
                "url" to "jdbc:postgresql://localhost:5432/quiz",
                "username" to "postgres",
                "password" to "postgres",
                "changeLogFile" to "src/main/resources/db/changelog/db.changelog-master.xml",
                "referenceUrl" to "hibernate:spring:fm.force.quiz?" +
                        "dialect=org.hibernate.dialect.PostgreSQL95Dialect&" +
                        "hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy&" +
                        "hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy",
                "defaultSchemaName" to "",
                "logLevel" to "debug",
                "classpath" to "src/main/resources/"
        )
    }
    activities.register("diffLog") {
        this.arguments = mapOf(
                "driver" to "org.postgresql.Driver",
                "url" to "jdbc:postgresql://localhost:5432/quiz",
                "username" to "postgres",
                "password" to "postgres",
                "changeLogFile" to project.ext.get("diffChangelogFile"),
                "referenceUrl" to "hibernate:spring:fm.force.quiz?" +
                        "dialect=org.hibernate.dialect.PostgreSQL95Dialect&" +
                        "hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy&" +
                        "hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy",
                "defaultSchemaName" to "",
                "logLevel" to "debug",
                "classpath" to "$buildDir/classes/kotlin/main"
        )
    }

    runList = project.ext.get("runList")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    outputs.dir(snippetsDir)
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
}
