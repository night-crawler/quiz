import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    val kotlinVersion = "1.3.50"

    idea
    java
    kotlin("jvm") version kotlinVersion
//    kotlin("kapt") version kotlinVersion
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
java.sourceCompatibility = JavaVersion.VERSION_11

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

val snippetsDir = file("build/generated-snippets")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
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
    liquibaseRuntime("org.springframework.boot:spring-boot:2.1.8.RELEASE")

    api("io.jsonwebtoken:jjwt-api:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.10.7")
    runtimeOnly("io.jsonwebtoken:jjwt-orgjson:0.10.7")
    compile("io.jsonwebtoken:jjwt-jackson:0.10.7")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")


    compile("de.mkammerer:argon2-jvm:2.6")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.vladmihalcea:hibernate-types-52:2.5.0")
    runtimeOnly("org.postgresql:postgresql")

    compileOnly("org.projectlombok:lombok:1.18.10")
    annotationProcessor("org.projectlombok:lombok:1.18.10")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.4.2")
    testCompile("io.kotlintest:kotlintest-extensions-spring:3.4.2")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}


tasks["build"].dependsOn("createDirs")


if (!project.hasProperty("runList")) {
    project.ext["runList"] = "main"
}


liquibase {
    val changeLogDir = "$mainResourcesDir/db/changelog"
    val changesDir = "$changeLogDir/changes"
    val masterChangeLogFile = "$changeLogDir/changelog-master.xml"
    val changeLogTs = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))!!
    val diffChangeLogFile = "$changesDir/change-${changeLogTs}.xml"

    val liquibaseDefaultArguments = mapOf(
            "driver" to "org.postgresql.Driver",
            "url" to "jdbc:postgresql://localhost:5432/quiz",
            "username" to "postgres",
            "password" to "postgres",
            "referenceUrl" to "hibernate:spring:fm.force.quiz?" +
                    "dialect=org.hibernate.dialect.PostgreSQL95Dialect&" +
                    "hibernate.physical_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy&" +
                    "hibernate.implicit_naming_strategy=org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy",
            "defaultSchemaName" to "",
            "logLevel" to "debug",
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

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.test {
    outputs.dir(snippetsDir)
    useJUnitPlatform()
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
}
