# Colonel

[Javadoc](https://colonel.stellardrift.ca/javadoc/)

An API for Brigadier extensions.

Primarily tested on 1.16, but should work on 1.15 and 1.14 as well.

## Features
- Add argument types that are only required on the server

Example:

Here we're registering an EnumArgumentType that will ask the server for its completions:

```java
public void initialize() {
        ServerArgumentType.<EnumArgumentType<?>>builder(id("enum"))
                .type(EnumArgumentType.class)
                .serializer(new EnumArgumentType.Serializer())
                .fallbackProvider(arg -> StringArgumentType.word())
                .register();
}
```

Now this argument type can be used freely in arguments. If a client does not have your mod installed, it will see the argument as a StringArgumentType, completed by the server.

## Using

Colonel is designed to be small and easily `include()`-able. Releases are published to Maven Central. Depend on it like this:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    include(modImplementation("ca.stellardrift:colonel:0.2")!!)
}
```

Colonel is released under the terms of the Apache 2.0 license.

