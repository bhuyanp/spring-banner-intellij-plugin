package io.github.bhuyanp.intellij.springbanner.model;


import io.github.bhuyanp.intellij.springbanner.AppSettings;
import io.github.bhuyanp.intellij.springbanner.theme.CAPTION_BULLET_STYLE;
import io.github.bhuyanp.intellij.springbanner.theme.ThemeConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Extension for the plugin with various customization option
 */
@Getter
@Setter
@ToString
public class SpringCaptionConfig {
    private final boolean showAppVersion;
    private final boolean showSpringVersion;
    private final boolean showJDKVersion;
    private final CAPTION_BULLET_STYLE captionBulletStyle;
    private final String captionText;
    private String appVersion;
    private String springVersion;
    private String jdkVersion;
    private ThemeConfig captionTheme;

    public SpringCaptionConfig(AppSettings.State settings){
        this.showAppVersion = settings.showAppVersion;
        this.showSpringVersion = settings.showSpringVersionInCaption;
        this.showJDKVersion = settings.showJDKVersionInCaption;
        this.captionBulletStyle = settings.captionBulletStyle;
        this.captionText = settings.captionText;
    }
}
