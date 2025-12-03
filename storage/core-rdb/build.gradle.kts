allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    implementation(project(":core:core-enum"))
    implementation(project(":core:core-domain"))

    api("org.springframework.boot:spring-boot-starter-data-jpa")

    // Database
    runtimeOnly("org.postgresql:postgresql:${project.properties["postgresqlVersion"]}")
    runtimeOnly("com.h2database:h2")
}
