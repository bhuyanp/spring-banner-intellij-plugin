package io.github.bhuyanp.intellij.springbanner.model;


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
    private String springVersion;
    private boolean showSpringVersion;
    private String jdkVersion;
    private boolean showJDKVersion;
    private ThemeConfig captionTheme;
    private String captionText;
}
