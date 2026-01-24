package io.github.bhuyanp.intellij.springbanner.theme;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.BLANK;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/23/26
 */
@RequiredArgsConstructor
@Getter
public enum CAPTION_BULLET_STYLE {
    GT(">", true),
    PIPE("|", false),
    GTA("->", false),
    POUND("#", false),
    DOLLAR("$", false),
    RANDOM("Random", false),
    NONE("None", false);

    private final String label;
    private final boolean isDefault;


    @Override
    public String toString() {
        return label + (isDefault ? " (Default)" : BLANK);
    }
}
