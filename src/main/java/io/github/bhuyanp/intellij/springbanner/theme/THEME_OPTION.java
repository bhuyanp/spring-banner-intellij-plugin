package io.github.bhuyanp.intellij.springbanner.theme;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum THEME_OPTION {
    DARK("Dark", "Suitable for dark themes"),
    LIGHT("Light", "Suitable for light themes"),
    AUTO("Auto", "Automatically switches between dark and light theme to match IDE's theme"),
    SURPRISE_ME("Surprise Me", "Randomises banner colors and additional effects"),
    CUSTOM("Custom", "Customize all aspects of the banner");

    private final String label;
    private final String description;

    public static THEME_OPTION value(String themeOption){
        return switch (themeOption){
            case "Dark"->THEME_OPTION.DARK;
            case "Light"->THEME_OPTION.LIGHT;
            case "Auto"->THEME_OPTION.AUTO;
            case "Surprise Me"->THEME_OPTION.SURPRISE_ME;
            case "Custom"->THEME_OPTION.CUSTOM;
            default -> throw new IllegalStateException("Unexpected theme option: " + themeOption);
        };
    }

    @Override
    public String toString(){
        return label;
    }
}
