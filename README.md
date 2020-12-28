# fabric-permissions-api

A simple permissions API for Fabric.

### Usage (checking permissions)

All the methods you need to check for permissions in a mod live in the `Permissions` class.

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

### Usage (providing permissions)

Just register a listener for the `PermissionCheckEvent`.

```java
PermissionCheckEvent.EVENT.register((source, permission) -> {
    if (isSuperAdmin(source)) {
        return TriState.TRUE;
    }
    return TriState.DEFAULT;
});
```
