package me.lucko.fabric.api.permissions.v0;

import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A simple permissions API.
 */
public interface Permissions {

    /**
     * Gets the {@link TriState state} of a {@code permission} for the given player.
     *
     * @param player the player
     * @param permission the permission
     * @return the state of the permission
     */
    static TriState getPermissionValue(ServerPlayerEntity player, String permission) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(permission, "permission");
        return PermissionCheckEvent.EVENT.invoker().onPermissionCheck(player, permission);
    }

    /**
     * Performs a permission check.
     *
     * @param player the player to perform the check for
     * @param permission the permission to check
     * @param defaultValue the default value to use for the permission, if nothing has been set.
     * @return the result of the permission check
     */
    static boolean check(ServerPlayerEntity player, String permission, boolean defaultValue) {
        return getPermissionValue(player, permission).orElse(defaultValue);
    }

    /**
     * Performs a permission check.
     *
     * @param player the player to perform the check for
     * @param permission the permission to check
     * @return the result of the permission check
     */
    static boolean check(ServerPlayerEntity player, String permission) {
        return check(player, permission, false);
    }

    /**
     * Performs a permission check.
     *
     * @param source the source to perform the check for
     * @param permission the permission to check
     * @param defaultRequiredLevel the OP level required for non-server-player sources
     * @return the result of the permission check
     */
    static TriState getPermissionValue(CommandSource source, String permission, int defaultRequiredLevel) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(permission, "permission");

        if (!(source instanceof ServerCommandSource)) {
            return source.hasPermissionLevel(defaultRequiredLevel) ? TriState.TRUE : TriState.DEFAULT;
        }

        ServerCommandSource serverSource = (ServerCommandSource) source;
        Entity entity = serverSource.getEntity();

        if (!(entity instanceof ServerPlayerEntity)) {
            return source.hasPermissionLevel(defaultRequiredLevel) ? TriState.TRUE : TriState.DEFAULT;
        }

        return getPermissionValue((ServerPlayerEntity) entity, permission);
    }

    /**
     * Performs a permission check.
     *
     * @param source the source to perform the check for
     * @param permission the permission to check
     * @return the result of the permission check
     */
    static TriState getPermissionValue(CommandSource source, String permission) {
        return getPermissionValue(source, permission, 4);
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
        return getPermissionValue(source, permission, 4).orElse(defaultValue);
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
        return require(permission, false);
    }

}
