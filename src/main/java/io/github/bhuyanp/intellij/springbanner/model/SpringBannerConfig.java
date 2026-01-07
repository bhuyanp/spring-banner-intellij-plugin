package io.github.bhuyanp.intellij.springbanner.model;



import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.theme.ThemeConfig;
import lombok.*;

/**
 * Extension for the plugin with various customization option
 */
@Getter
@Builder
@ToString
public class SpringBannerConfig {
    private String text;
    private String bannerFont;
    private ThemeConfig bannerTheme;
}
