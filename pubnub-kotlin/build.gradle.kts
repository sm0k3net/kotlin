plugins {
    alias(libs.plugins.benmanes.versions)
    id("pubnub.shared")
    id("pubnub.dokka")
}

dependencies {
    api(project(":pubnub-core:pubnub-core-api"))
    api(project(":pubnub-kotlin:pubnub-kotlin-api"))
    implementation(project(":pubnub-core:pubnub-core-impl"))
    implementation(project(":pubnub-kotlin:pubnub-kotlin-impl"))
}
