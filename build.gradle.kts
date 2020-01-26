import java.io.FileInputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Properties
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

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

    id("org.liquibase.gradle") version "2.0.2"
    id("org.springframework.boot") version springBootVersion
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("org.asciidoctor.convert") version "2.4.0"

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
    liquibaseRuntime(sourceSets.getByName("main").compileClasspath)
    liquibaseRuntime(sourceSets.getByName("main").output)
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("org.springframework.boot:spring-boot:$springBootVersion")

    api("io.jsonwebtoken:jjwt-api:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-orgjson:0.10.7")
    compile("io.jsonwebtoken:jjwt-jackson:0.10.7")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")

    implementation("com.github.slugify:slugify:2.4")
    compile("am.ik.yavi:yavi:0.2.5")
    compile("de.mkammerer:argon2-jvm:2.6")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.vladmihalcea:hibernate-types-52:2.5.0")
    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.hibernate:hibernate-jpamodelgen:5.3.8.Final")

    compile("io.springfox:springfox-swagger2:2.9.2")
    compile("io.springfox:springfox-swagger-ui:2.9.2")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testCompile("io.kotlintest:kotlintest-extensions-spring:3.4.2")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "com.vaadin.external.google", module = "android-json")
    }
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
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
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    test {
        outputs.dir(snippetsDir)
        useJUnitPlatform()
    }

    asciidoctor {
        inputs.dir(snippetsDir)
        dependsOn(test)
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
