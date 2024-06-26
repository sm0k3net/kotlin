pluginManagement {
    includeBuild("build-logic/gradle-plugins")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "pubnub"
include("pubnub-core:pubnub-core-api")
include("pubnub-core:pubnub-core-impl")
include("pubnub-kotlin")
include("pubnub-kotlin:pubnub-kotlin-api")
include("pubnub-kotlin:pubnub-kotlin-impl")
include("pubnub-gson")
include("pubnub-gson:pubnub-gson-api")
include("pubnub-gson:pubnub-gson-impl")
include("examples:kotlin-app")
include("examples:java-app")
include("build-logic:ktlint-custom-rules")
