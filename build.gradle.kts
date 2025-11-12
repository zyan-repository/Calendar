plugins {
    id("java")
    id("com.github.spotbugs") version "6.4.2"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType(com.github.spotbugs.snom.SpotBugsTask::class).configureEach {
    reports {
        create("html") {
            required.set(true)
        }
        create("text") {
            required.set(false)
        }
    }
}
