# fabric-permissions-api

A simple permissions API for Fabric.

### Usage (end users)

All the methods you need are in the `Permissions` class.

#### Checking permissions for a `ServerPlayerEntity`

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

### Usage (implementors)

Just register a callback for the `PermissionCheckEvent`.

```java
PermissionCheckEvent.EVENT.register((player, permission) -> {
    if (player.getName().equals("Luck")) {
        return TriState.TRUE;
    }
    return TriState.DEFAULT;
});
```
