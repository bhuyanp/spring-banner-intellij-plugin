package io.github.bhuyanp.intellij.springbanner.util;

import com.intellij.DynamicBundle;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Supplier;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/26/26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FunkyBannerBundle {
    @NonNls
    private static final String BUNDLE = "messages.FunkyBanner";
    private static final DynamicBundle INSTANCE =
            new DynamicBundle(FunkyBannerBundle.class, BUNDLE);


    public static @NotNull @Nls String message(
            @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
            Object @NotNull ... params
    ) {
        return INSTANCE.getMessage(key, params);
    }

    public static Supplier<@Nls String> lazyMessage(
            @NotNull @PropertyKey(resourceBundle = BUNDLE) String key,
            Object @NotNull ... params
    ) {
        return INSTANCE.getLazyMessage(key, params);
    }
}
