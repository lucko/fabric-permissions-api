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


import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public interface Meta {
    /**
     * Gets the meta value of a {@code key} for the given source.
     * @param source the source
     * @param key the meta key
     * @return the value of the meta key
     */
    static @NotNull Optional<String> getMetaValue(@NotNull CommandSource source, @NotNull String key) {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(key, "key");
        return MetaFetchEvent.EVENT.invoker().onMetaFetch(source, key);
    }

    /**
     * Gets the meta value of a {@code key} for the given entity.
     * @param entity the entity
     * @param key the meta key
     * @return the value of the meta key
     */
    static @NotNull Optional<String> getMetaValue(@NotNull Entity entity, @NotNull String key) {
        Objects.requireNonNull(entity, "entity");
        return getMetaValue(entity.getCommandSource(), key);
    }

    /**
     * Gets the meta value of a {@code key} for the given source, falling back to the {@code defaultValue} if the resultant value is {@link Optional#empty}.
     * @param source the source
     * @param key the meta key
     * @param defaultValue the default value to use if nothing has been set
     * @return the value of the meta key
     */
    static @NotNull String getMetaValue(@NotNull CommandSource source, @NotNull String key, @NotNull String defaultValue) {
        return getMetaValue(source, key).orElse(defaultValue);
    }

    /**
     * Gets the meta value of a {@code key} for the given entity, falling back to the {@code defaultValue} if the resultant value is {@link Optional#empty}.
     * @param entity the entity
     * @param key the meta key
     * @param defaultValue the default value to use if nothing has been set
     * @return the value of the meta key
     */
    static @NotNull String getMetaValue(@NotNull Entity entity, @NotNull String key, @NotNull String defaultValue) {
        return getMetaValue(entity.getCommandSource(), key, defaultValue);
    }

    /**
     * Gets the meta value of a {@code key} for the given source, and runs it through the given {@code valueTransformer}.
     *
     * <p>If no such meta value exists, an {@link Optional#empty() empty optional} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in an {@link Optional#empty() empty optional} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     getMetaValue(source, "my-int-val", Integer::parseInt).orElse(0);
     * </pre></blockquote>
     *
     * @param source the source
     * @param key the key
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the meta value
     */
    static <T> @NotNull Optional<T> getMetaValue(@NotNull CommandSource source, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
        return getMetaValue(source, key).flatMap(value -> {
            try {
                return Optional.of(valueTransformer.apply(key));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
    }

    /**
     * Gets the meta value of a {@code key} for the given entity, and runs it through the given {@code valueTransformer}.
     *
     * <p>If no such meta value exists, an {@link Optional#empty() empty optional} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in an {@link Optional#empty() empty optional} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     getMetaValue(entity, "my-int-val", Integer::parseInt).orElse(0);
     * </pre></blockquote>
     *
     * @param entity the entity
     * @param key the key
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the meta value
     */
    static <T> @NotNull Optional<T> getMetaValue(@NotNull Entity entity, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
        return getMetaValue(entity.getCommandSource(), key, valueTransformer);
    }
}
