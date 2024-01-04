pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://4thline.org/m2")
        }
        maven {
            isAllowInsecureProtocol = true
            url = uri("https://jitpack.io") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/groups/public/")
        }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/jcenter") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/google") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/gradle-plugin") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("https://dl.bintray.com/thelasterstar/maven/") }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots")
        }

        mavenCentral()
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://4thline.org/m2") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("https://jitpack.io") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/groups/public/") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/jcenter") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/google") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("http://maven.aliyun.com/nexus/content/repositories/gradle-plugin") }
        maven {
            isAllowInsecureProtocol = true
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }

        mavenCentral()
        maven { url = uri("https://repo1.maven.org/maven2/") }
    }
}

rootProject.name = "YicLibrary"
include(":yic-lib")
include(":JetpackMvvm")