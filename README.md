# fabric-permissions-api

A simple permissions API for Fabric.

### Usage (modders)

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

### Usage (permission provider mods)

Just register a callback for the `PermissionCheckEvent`.

```java
PermissionCheckEvent.EVENT.register((source, permission) -> {
    if (isSuperAdmin(source)) {
        return TriState.TRUE;
    }
    return TriState.DEFAULT;
});
```
