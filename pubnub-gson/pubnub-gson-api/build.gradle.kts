plugins {
    checkstyle
    alias(libs.plugins.benmanes.versions)
    alias(libs.plugins.lombok)
    id("pubnub.shared")
    id("pubnub.dokka")
}

dependencies {
    api(project(":pubnub-core:pubnub-core-api"))
    implementation(project(":pubnub-core:pubnub-core-impl"))

    implementation(libs.slf4j)
    implementation(libs.jetbrains.annotations)
}

checkstyle {
    toolVersion = "8.14"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    sourceSets = listOf(project.sourceSets.getByName("main"))
}

tasks.withType<Checkstyle>().configureEach {
    exclude("**/vendor/**", "**/*Test*")

    reports {
        xml.required = true
        html.required = true
    }
}
