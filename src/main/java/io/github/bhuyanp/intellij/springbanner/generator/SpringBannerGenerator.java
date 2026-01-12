package io.github.bhuyanp.intellij.springbanner.generator;


import io.github.bhuyanp.intellij.springbanner.figlet.FigletBannerRenderer;
import io.github.bhuyanp.intellij.springbanner.model.SpringBannerConfig;
import io.github.bhuyanp.intellij.springbanner.theme.TextPadding;
import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.theme.ThemeConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import static io.github.bhuyanp.intellij.springbanner.ansi.Ansi.colorize;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.DEFAULT_FONTS;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.RANDOM_FONT;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringBannerGenerator {
    public static final SpringBannerGenerator INSTANCE = new SpringBannerGenerator();

    public String getBanner(SpringBannerConfig springBannerConfig) {
        String text = springBannerConfig.getText();
        ThemeConfig bannerTheme = springBannerConfig.getBannerTheme();
        log.info("Banner Theme Config: {}",bannerTheme);
        String bannerFont = getBannerFont(springBannerConfig);
        if (text.isBlank()) return text;

        text = capitalizeProjectName(text);
        String banner = FigletBannerRenderer.SINGLETON.render(bannerFont, text);
        TextPadding textPadding = null;
        //For no background banners no left/right padding needed
        if (bannerTheme.hasBackColor()) {
            textPadding = Theme.getBannerPadding(bannerFont);
            banner = textPadding.apply(banner);
        }
        banner = banner.lines()
                .map(line -> colorize(line, bannerTheme))
                .collect(Collectors.joining(System.lineSeparator()));

        banner = new TextPadding(1, 0, 1, 0).apply(banner);

        return banner;
    }



    /**
     * Get the banner font based on the configuration.
     * If the configuration is set to RANDOM_FONT, a random font from the default fonts list is selected.
     *
     * @param springBannerConfig Banner configuration
     * @return The banner string based on the configuration
     */
    private String getBannerFont(SpringBannerConfig springBannerConfig) {
        String bannerFont = springBannerConfig.getBannerFont();
        if(bannerFont.equalsIgnoreCase(RANDOM_FONT)){
            int randomFontIndex = new Random().nextInt(DEFAULT_FONTS.size());
            return DEFAULT_FONTS.get(randomFontIndex);
        }
        return bannerFont;
    }

    /**
     *
     * @param projectName Name of the project
     * @return The capitalized project name and dashes replaces with spaces
     */
    private String capitalizeProjectName(String projectName) {
        return Arrays.stream(projectName.replace('-', ' ').split(" ")).map(name ->
                        name.substring(0, 1).toUpperCase() + name.substring(1))
                .collect(Collectors.joining(" "));
    }


}
