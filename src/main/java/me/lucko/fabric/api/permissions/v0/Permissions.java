package me.lucko.fabric.api.permissions.v0;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A simple permissions API.
 */
public interface Permissions {

    /**
     * Gets the {@link TriState state} of a {@code permission} for the given source.
     *
     * @param source the source
     * @param permission the permission
     * @return the state of the permission
     */
    static TriState getPermissionValue(CommandSource source, String permission) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(permission, "permission");
        return PermissionCheckEvent.EVENT.invoker().onPermissionCheck(source, permission);
    }

    /**
     * Performs a permission check.
     *
     * @param source the source to perform the check for
     * @param permission the permission to check
     * @param defaultValue the default value to use for the permission, if nothing has been set.
     * @return the result of the permission check
     */
    static boolean check(CommandSource source, String permission, boolean defaultValue) {
        return getPermissionValue(source, permission).orElse(defaultValue);
    }

    /**
     * Performs a permission check.
     *
     * @param source the source to perform the check for
     * @param permission the permission to check
     * @return the result of the permission check
     */
    static boolean check(CommandSource source, String permission) {
        return check(source, permission, false);
    }

    /**
     * Creates a predicate which returns the result of performing a permission check.
     *
     * @param permission the permission to check
     * @param defaultValue the default value to use for the permission, if nothing has been set.
     * @return the a predicate representing the permission check
     */
    static Predicate<ServerCommandSource> require(String permission, boolean defaultValue) {
        return player -> check(player, permission, defaultValue);
    }

    /**
     * Creates a predicate which returns the result of performing a permission check.
     *
     * @param permission the permission to check
     * @return the a predicate representing the permission check
     */
    static Predicate<ServerCommandSource> require(String permission) {
        return player -> check(player, permission);
    }

    /**
     * Gets the {@link TriState state} of a {@code permission} for the given entity.
     *
     * @param entity the entity
     * @param permission the permission
     * @return the state of the permission
     */
    static TriState getPermissionValue(Entity entity, String permission) {
        Objects.requireNonNull(entity, "entity");
        Objects.requireNonNull(permission, "permission");
        return PermissionCheckEvent.EVENT.invoker().onPermissionCheck(entity.getCommandSource(), permission);
    }

    /**
     * Performs a permission check.
     *
     * @param entity the entity to perform the check for
     * @param permission the permission to check
     * @param defaultValue the default value to use for the permission, if nothing has been set.
     * @return the result of the permission check
     */
    static boolean check(Entity entity, String permission, boolean defaultValue) {
        return getPermissionValue(entity, permission).orElse(defaultValue);
    }

    /**
     * Performs a permission check.
     *
     * @param entity the source to perform the check for
     * @param permission the permission to check
     * @return the result of the permission check
     */
    static boolean check(Entity entity, String permission) {
        return check(entity, permission, false);
    }

}
