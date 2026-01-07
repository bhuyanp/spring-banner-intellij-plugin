package io.github.bhuyanp.intellij.springbanner.model;


import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.theme.ThemeConfig;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Extension for the plugin with various customization option
 */
@Getter
@Builder
@ToString
public class SpringCaptionConfig {
    private String font;
    private int [] padding;
    private boolean bold;
    private String fontColor;
    private String backgroundColor;
    private ThemeConfig captionTheme;

}
