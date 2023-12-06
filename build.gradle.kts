plugins {
    application
    id("java")
    id("io.freefair.lombok") version "8.4"
}

group = "co.elastic.examples"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

tasks.register<JavaExec>("index-data") {
    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass = "co.elastic.examples.Indexer"
}

tasks.register<JavaExec>("search-bot") {
    classpath = java.sourceSets["main"].runtimeClasspath
    standardInput = System.`in`
    mainClass = "co.elastic.examples.QABot"
}

dependencies {
    implementation("dev.langchain4j:langchain4j:0.24.0");
    implementation("dev.langchain4j:langchain4j-ollama:0.24.0")

    //implementation("dev.langchain4j:langchain4j-elasticsearch:0.24.0")
    implementation("co.elastic.clients:elasticsearch-java:8.11.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0")
    implementation("org.projectlombok:lombok:1.18.30")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.jline:jline:3.24.1")
    implementation("org.slf4j:slf4j-nop:2.0.9")
}

tasks.test {
    useJUnitPlatform()
}


sourceSets {
    main {
        java {
            srcDirs("src/main/java-lc4j")
        }
    }
}
