plugins {
    id("java")
}

group = "com.dabburi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("com.jcraft:jsch:0.1.55")
}

tasks.test {
    useJUnitPlatform()
}