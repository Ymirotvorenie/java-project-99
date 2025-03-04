plugins {
	application
	checkstyle
	jacoco
	id("org.springframework.boot") version "3.3.2"
	id("io.spring.dependency-management") version "1.1.6"
	id("io.freefair.lombok") version "8.6"
	id("io.sentry.jvm.gradle") version "4.10.0"

}

group = "hexlet.code"
version = "0.0.1-SNAPSHOT"

application {
	mainClass.set("hexlet.code.AppApplication")
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/snapshot") }
	maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	//H2 database
	implementation("com.h2database:h2:2.2.224")
	implementation("com.zaxxer:HikariCP:5.1.0")

	//JsonNullable<>
	implementation("org.openapitools:jackson-databind-nullable:0.2.6")

	//MapStruct
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	//Authentication
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	testImplementation("org.springframework.security:spring-security-test")

	//Tests
	testImplementation(platform("org.junit:junit-bom:5.10.3"))
	testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("net.javacrumbs.json-unit:json-unit-assertj:3.2.7")
	implementation("net.datafaker:datafaker:2.3.0")
	implementation("org.instancio:instancio-junit:5.0.1")

	//auto restart
	implementation("org.springframework.boot:spring-boot-devtools")

	//swagger
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	reports { xml.required.set(true) }
}

sentry {
	includeSourceContext = true
	org = "ymirotvorenie"
	projectName = "java-spring-boot"
	authToken = System.getenv("SENTRY_AUTH_TOKEN")
}

tasks.sentryBundleSourcesJava {
	enabled = System.getenv("SENTRY_AUTH_TOKEN") != null
}