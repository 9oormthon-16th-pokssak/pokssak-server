dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // JWT - jjwt
    implementation("io.jsonwebtoken:jjwt-api:${project.properties["jjwtVersion"]}")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${project.properties["jjwtVersion"]}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${project.properties["jjwtVersion"]}")

    // Validation
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":core:core-enum"))
    implementation(project(":core:core-domain"))
    implementation(project(":external:ai"))
    implementation(project(":storage:core-rdb"))
    implementation(project(":storage:redis"))

    // OpenApi Spec
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${project.properties["springDocOpenApiVersion"]}")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-data-redis")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testRuntimeOnly("com.h2database:h2")
}

tasks.getByName("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}
