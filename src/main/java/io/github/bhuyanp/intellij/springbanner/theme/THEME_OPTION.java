package io.github.bhuyanp.intellij.springbanner.theme;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum THEME_OPTION {
    DARK("Dark", Theme.DARK),
    LIGHT("Light", Theme.LIGHT),
    SURPRISE_ME("Surprise Me", Theme.SURPRISE_ME),
    CUSTOM("Custom", null);

    private final String label;
    private final Theme theme;
}
