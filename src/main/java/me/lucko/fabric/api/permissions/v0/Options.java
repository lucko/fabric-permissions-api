/*
 * This file is part of fabric-permissions-api, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.fabric.api.permissions.v0;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * A simple options (metadata) API.
 */
public interface Options {

    /**
     * Gets the value of an option for the given source.
     *
     * @param source the source
     * @param key the option key
     * @return the option value
     */
    static @NotNull Optional<String> get(@NotNull CommandSource source, @NotNull String key) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(key, "key");
        return OptionRequestEvent.EVENT.invoker().onOptionRequest(source, key);
    }

    /**
     * Gets the value of an option for the given source, falling back to the {@code defaultValue}
     * if nothing is returned from the provider.
     * 
     * @param source the source
     * @param key the option key
     * @param defaultValue the default value to use if nothing is returned
     * @return the option value
     */
    @Contract("_, _, !null -> !null")
    static String get(@NotNull CommandSource source, @NotNull String key, String defaultValue) {
        return get(source, key).orElse(defaultValue);
    }

    /**
     * Gets the value of an option for the given source, and runs it through the given {@code valueTransformer}.
     *
     * <p>If nothing is returned from the provider, an {@link Optional#empty() empty optional} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in an {@link Optional#empty() empty optional} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(source, "my-int-value", Integer::parseInt).orElse(0);
     * </pre></blockquote>
     *
     * @param source the source
     * @param key the option key
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    static <T> @NotNull Optional<T> get(@NotNull CommandSource source, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
        return get(source, key).flatMap(value -> {
            try {
                return Optional.ofNullable(valueTransformer.apply(value));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
    }

    /**
     * Gets the value of an option for the given source, runs it through the given {@code valueTransformer},
     * and falls back to the {@code defaultValue} if nothing is returned.
     *
     * <p>If nothing is returned from the provider, the {@code defaultValue} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in the {@code defaultValue} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(source, "my-int-value", 0, Integer::parseInt);
     * </pre></blockquote>
     *
     * @param source the source
     * @param key the option key
     * @param defaultValue the default value
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    @Contract("_, _, !null, _ -> !null")
    static <T> T get(@NotNull CommandSource source, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
        return Options.<T>get(source, key, valueTransformer).orElse(defaultValue);
    }

    /**
     * Gets the value of an option for the given entity.
     *
     * @param entity the entity
     * @param key the option key
     * @return the option value
     */
    static @NotNull Optional<String> get(@NotNull Entity entity, @NotNull String key) {
        Objects.requireNonNull(entity, "entity");
        return get(entity.getCommandSource(), key);
    }

    /**
     * Gets the value of an option for the given entity, falling back to the {@code defaultValue}
     * if nothing is returned from the provider.
     *
     * @param entity the entity
     * @param key the option key
     * @param defaultValue the default value to use if nothing is returned
     * @return the option value
     */
    @Contract("_, _, !null -> !null")
    static String get(@NotNull Entity entity, @NotNull String key, String defaultValue) {
        Objects.requireNonNull(entity, "entity");
        return get(entity.getCommandSource(), key, defaultValue);
    }

    /**
     * Gets the value of an option for the given entity, and runs it through the given {@code valueTransformer}.
     *
     * <p>If nothing is returned from the provider, an {@link Optional#empty() empty optional} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in an {@link Optional#empty() empty optional} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(entity, "my-int-value", Integer::parseInt).orElse(0);
     * </pre></blockquote>
     *
     * @param entity the entity
     * @param key the option key
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    static <T> @NotNull Optional<T> get(@NotNull Entity entity, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
        Objects.requireNonNull(entity, "entity");
        return get(entity.getCommandSource(), key, valueTransformer);
    }

    /**
     * Gets the value of an option for the given entity, runs it through the given {@code valueTransformer},
     * and falls back to the {@code defaultValue} if nothing is returned.
     *
     * <p>If nothing is returned from the provider, the {@code defaultValue} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in the {@code defaultValue} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(entity, "my-int-value", 0, Integer::parseInt);
     * </pre></blockquote>
     *
     * @param entity the entity
     * @param key the option key
     * @param defaultValue the default value
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    @Contract("_, _, !null, _ -> !null")
    static <T> T get(@NotNull Entity entity, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
        Objects.requireNonNull(entity, "entity");
        return get(entity.getCommandSource(), key, defaultValue, valueTransformer);
    }

    /**
     * Gets the value of an option for the given (potentially) offline player.
     *
     * @param uuid the uuid of the player
     * @param key the option key
     * @return the option value
     */
    static @NotNull CompletableFuture<Optional<String>> get(@NotNull UUID uuid, @NotNull String key) {
        Objects.requireNonNull(uuid, "uuid");
        Objects.requireNonNull(key, "key");
        return OfflineOptionRequestEvent.EVENT.invoker().onOptionRequest(uuid, key);
    }

    /**
     * Gets the value of an option for the given player, falling back to the {@code defaultValue}
     * if nothing is returned from the provider.
     *
     * @param uuid the uuid of the player
     * @param key the option key
     * @param defaultValue the default value to use if nothing is returned
     * @return the option value
     */
    @Contract("_, _, !null -> !null")
    static CompletableFuture<String> get(@NotNull UUID uuid, @NotNull String key, String defaultValue) {
        return get(uuid, key).thenApply(opt -> opt.orElse(defaultValue));
    }

    /**
     * Gets the value of an option for the given player, and runs it through the given {@code valueTransformer}.
     *
     * <p>If nothing is returned from the provider, an {@link Optional#empty() empty optional} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in an {@link Optional#empty() empty optional} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(uuid, "my-int-value", Integer::parseInt).orElse(0);
     * </pre></blockquote>
     *
     * @param uuid the uuid of the player
     * @param key the option key
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    static <T> @NotNull CompletableFuture<Optional<T>> get(@NotNull UUID uuid, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
        return get(uuid, key).thenApply(opt -> opt.flatMap(value -> {
            try {
                return Optional.ofNullable(valueTransformer.apply(value));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }));
    }

    /**
     * Gets the value of an option for the given player, runs it through the given {@code valueTransformer},
     * and falls back to the {@code defaultValue} if nothing is returned.
     *
     * <p>If nothing is returned from the provider, the {@code defaultValue} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in the {@code defaultValue} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(uuid, "my-int-value", 0, Integer::parseInt);
     * </pre></blockquote>
     *
     * @param uuid the uuid of the player
     * @param key the option key
     * @param defaultValue the default value
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    @Contract("_, _, !null, _ -> !null")
    static <T> CompletableFuture<T> get(@NotNull UUID uuid, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
        return Options.<T>get(uuid, key, valueTransformer).thenApply(opt -> opt.orElse(defaultValue));
    }

    /**
     * Gets the value of an option for the given (potentially) offline player.
     *
     * @param profile the player profile
     * @param key the option key
     * @return the option value
     */
    static @NotNull CompletableFuture<Optional<String>> get(@NotNull GameProfile profile, @NotNull String key) {
        Objects.requireNonNull(profile, "profile");
        return get(profile.getId(), key);
    }

    /**
     * Gets the value of an option for the given player, falling back to the {@code defaultValue}
     * if nothing is returned from the provider.
     *
     * @param profile the player profile
     * @param key the option key
     * @param defaultValue the default value to use if nothing is returned
     * @return the option value
     */
    @Contract("_, _, !null -> !null")
    static CompletableFuture<String> get(@NotNull GameProfile profile, @NotNull String key, String defaultValue) {
        Objects.requireNonNull(profile, "profile");
        return get(profile.getId(), key, defaultValue);
    }

    /**
     * Gets the value of an option for the given player, and runs it through the given {@code valueTransformer}.
     *
     * <p>If nothing is returned from the provider, an {@link Optional#empty() empty optional} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in an {@link Optional#empty() empty optional} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(uuid, "my-int-value", Integer::parseInt).orElse(0);
     * </pre></blockquote>
     *
     * @param profile the player profile
     * @param key the option key
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    static <T> @NotNull CompletableFuture<Optional<T>> get(@NotNull GameProfile profile, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
        Objects.requireNonNull(profile, "profile");
        return get(profile.getId(), key, valueTransformer);
    }

    /**
     * Gets the value of an option for the given player, runs it through the given {@code valueTransformer},
     * and falls back to the {@code defaultValue} if nothing is returned.
     *
     * <p>If nothing is returned from the provider, the {@code defaultValue} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in the {@code defaultValue} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     get(uuid, "my-int-value", 0, Integer::parseInt);
     * </pre></blockquote>
     *
     * @param profile the player profile
     * @param key the option key
     * @param defaultValue the default value
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the transformed option value
     */
    @Contract("_, _, !null, _ -> !null")
    static <T> CompletableFuture<T> get(@NotNull GameProfile profile, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
        Objects.requireNonNull(profile, "profile");
        return get(profile.getId(), key, defaultValue, valueTransformer);
    }
    
}
