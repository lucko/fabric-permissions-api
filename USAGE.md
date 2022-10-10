# Usage

### Adding the dependency

Add the following to your build script:
```groovy
repositories {
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
}

dependencies {
    // Approach #1: Ensure fabric-permissions-api is always available by including it within your own jar (it's only ~8KB!)
    include(modImplementation('me.lucko:fabric-permissions-api:0.2-SNAPSHOT'))
    
    // Approach #2: Depend on fabric-permissions-api, but require that users install it themselves
    modImplementation 'me.lucko:fabric-permissions-api:0.2-SNAPSHOT'
}
```

Then depend on `"fabric-permissions-api-v0": "*"` in your fabric.mod.json.

## Usage (checking permissions)

All the methods you need to check for permissions are in the `Permissions` class.

#### Checking permissions for an `Entity`
This of course includes all subtypes, most notably `ServerPlayerEntity`.

```java
ServerPlayerEntity player = ...;
if (Permissions.check(player, "mymod.permission")) {
    // Woo!
}
```

#### Checking permissions for a `CommandSource`

```java
CommandSource source = ...;
if (Permissions.check(source, "mymod.permission")) {
    // Woo!
}
```

#### Checking permissions as part of a command

```java
CommandManager.literal("test")
    .requires(Permissions.require("mymod.command.test"))
    .executes(ctx -> {
        ctx.getSource().sendFeedback(Text.of("Woo!"), false);
        return Command.SINGLE_SUCCESS;
    })
    .build();
```

#### Checking permissions with a fallback default result

```java
// Fallback to requiring permission level 4 if the permission isn't set
if (Permissions.check(source, "mymod.permission", 4)) {
    // Woo!
}
```

```java
// Fallback to true if the permission isn't set
if (Permissions.check(source, "mymod.permission", true)) {
    // Woo!
}
```

#### Checking permissions for a (potentially) offline player
Permission checks for offline players can be made using the players unique id (UUID). The result is returned as a [CompletableFuture](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html).
```java
UUID uuid = ...;
Permissions.check(uuid, "mymod.permission").thenAcceptAsync(result -> {
    if (result) {
        // Woo!
    }
});
```

To simplify checks **not** made on the server thread, you can use `join()`.
```java
UUID uuid = ...;
if (Permissions.check(uuid, "mymod.permission").join()) {
    // Woo    
};
```

## Usage (getting options)

All the methods you need to get option values are in the `Options` class.

#### Getting options for an `Entity`
This of course includes all subtypes, most notably `ServerPlayerEntity`.

```java
ServerPlayerEntity player = ...;
Optional<String> value = Options.get(player, "prefix");
```

#### Getting options for a `CommandSource`

```java
CommandSource source = ...;
Optional<String> value = Options.get(source, "prefix");
```

#### Getting options with a fallback default value

```java
// Fallback to a different string the option isn't set
String value = Options.get(source, "prefix", "[Default]");
```

#### Getting options and transforming the result inline

```java
// Transform the value if it is returned
Optional<Integer> value = Options.get(source, "balance", Integer::parseInt);

// Transform the value or fallback to a default value
int value = Options.get(source, "balance", 0, Integer::parseInt);
```

## Usage (providing permissions)

Just register a listener for the `PermissionCheckEvent`.

```java
PermissionCheckEvent.EVENT.register((source, permission) -> {
    if (isSuperAdmin(source)) {
        return TriState.TRUE;
    }
    return TriState.DEFAULT;
});
```

```java
OfflinePermissionCheckEvent.EVENT.register((uuid, permission) -> {
    if (isSuperAdmin(uuid)) {
        return CompletableFuture.completedFuture(TriState.TRUE);
    }
    return CompletableFuture.completedFuture(TriState.DEFAULT);
});
```

## Usage (providing options)

Just register a listener for the `OptionRequestEvent`.

```java
OptionRequestEvent.EVENT.register((source, key) -> {
    if (key.equals("balance")) {
        return Optional.of(getPlayerBalance(source).toString());
    }
    return Optional.empty();
});
```
