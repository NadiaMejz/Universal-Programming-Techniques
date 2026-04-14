plugins {
    java
    application
}

repositories { mavenCentral() }

dependencies {
    implementation("com.rabbitmq:amqp-client:5.25.0")
}

application {
    applicationName = "chatroom"
    mainClass.set("org.example.zad1.Main")
}
