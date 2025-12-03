dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:${project.properties["redissonVersion"]}")
    implementation(project(":core:core-enum"))
    implementation(project(":core:core-domain"))
}
