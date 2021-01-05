import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties

group = "fm.force"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

plugins {
    val kotlinVersion: String by System.getProperties()
    val springBootVersion: String by System.getProperties()
    val jibVersion: String by System.getProperties()

    jacoco
    idea
    java

    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.allopen") version kotlinVersion
    kotlin("plugin.noarg") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("org.liquibase.gradle") version "2.0.2"
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.0.9.RELEASE"

    id("org.jlleitschuh.gradle.ktlint") version "9.1.1"
    id("com.google.cloud.tools.jib") version jibVersion
}

val snippetsDir = file("build/generated-snippets")
val kotlinVersion: String by System.getProperties()
val springBootVersion: String by System.getProperties()

val mainResourcesSrcDirs: MutableSet<File> by lazy { project.sourceSets.getByName("main").resources.srcDirs }
val mainResourcesDir by lazy {
    if (mainResourcesSrcDirs.size != 1) {
        throw Exception("Protecting my future self from misbehaviour")
    }
    mainResourcesSrcDirs.elementAt(0)
}

val developmentOnly: Configuration by configurations.creating

configurations {
    runtimeClasspath {
        extendsFrom(developmentOnly)
    }
}

repositories {
    mavenCentral()
}

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Table")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation("javax.activation:activation:1.1.1")
    implementation("org.glassfish.jaxb:jaxb-runtime:2.3.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.module:jackson-module-jaxb-annotations")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0")

    implementation("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase:liquibase-core")
    liquibaseRuntime("org.liquibase.ext:liquibase-hibernate5:3.8")
    liquibaseRuntime(sourceSets.getByName("main").compileClasspath)
    liquibaseRuntime(sourceSets.getByName("main").output)
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.springframework.boot:spring-boot:$springBootVersion")

    api("io.jsonwebtoken:jjwt-api:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-orgjson:0.10.7")
    implementation("io.jsonwebtoken:jjwt-jackson:0.10.7")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.20.0")

    implementation("com.github.slugify:slugify:2.4")
    implementation("am.ik.yavi:yavi:0.4.0")
    implementation("de.mkammerer:argon2-jvm:2.6")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.vladmihalcea:hibernate-types-52:2.9.8")
    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.hibernate:hibernate-jpamodelgen:5.3.8.Final")

    implementation("io.springfox:springfox-swagger2:2.9.2")
    implementation("io.springfox:springfox-swagger-ui:2.9.2")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testImplementation("io.kotlintest:kotlintest-extensions-spring:3.4.2")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "com.vaadin.external.google", module = "android-json")
    }
}

if (!project.hasProperty("runList")) {
    project.ext["runList"] = "main"
}

liquibase {
    val changeLogDir = "$mainResourcesDir/db/changelog"
    val changesDir = "$changeLogDir/changes"
    val masterChangeLogFile = "$changeLogDir/changelog-master.xml"
    val changeLogTs = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))!!
    val diffChangeLogFile = "$changesDir/change-$changeLogTs.xml"

    val props = Properties().apply {
        load(FileInputStream("$mainResourcesDir/liquibase.properties"))
    }

    val liquibaseDefaultArguments = props.toMap() + mapOf(
        "classpath" to "$buildDir/classes/kotlin/main"
    )

    activities.register("main") {
        arguments = liquibaseDefaultArguments + mapOf("changeLogFile" to masterChangeLogFile)
    }
    activities.register("diffLog") {
        arguments = liquibaseDefaultArguments + mapOf("changeLogFile" to diffChangeLogFile)
    }

    runList = project.ext.get("runList")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict", "-Xopt-in=kotlin.RequiresOptIn")
            jvmTarget = "11"
        }
    }

    test {
        outputs.dir(snippetsDir)
        useJUnitPlatform()
    }

    create<JacocoReport>("codeCoverageReport") {
        executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

        sourceSets(sourceSets["main"])

        reports {
            xml.isEnabled = true
            xml.destination = file("$buildDir/reports/jacoco/report.xml")
            html.isEnabled = true
            csv.isEnabled = false
        }

        dependsOn(project.getTasksByName("test", false))
    }
}

ktlint {
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(true)
    enableExperimentalRules.set(true)
    additionalEditorconfigFile.set(file("./.editorconfig"))

    disabledRules.set(setOf("experimental:multiline-if-else"))

    reporters {
        reporter(ReporterType.PLAIN)
        reporter(ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

jib {
    from {
        image = "openjdk:11-jre-slim"
    }
    to {
        image = "ncrawler/${project.name}"
        tags = setOf(version as String, "latest")
    }
    container {
        labels = mapOf(
            "maintainer" to "Igor Kalishevsky <lilo.panic@gmail.com>"
        )
        jvmFlags = listOf("-Xms512m")
        ports = listOf("8181")
    }
}

tasks.getByName<BootJar>("bootJar") {
    isExcludeDevtools = true
}
