package io.github.bhuyanp.intellij.springbanner.generator;


import io.github.bhuyanp.intellij.springbanner.figlet.FigletBannerRenderer;
import io.github.bhuyanp.intellij.springbanner.model.SpringBannerConfig;
import io.github.bhuyanp.intellij.springbanner.theme.TextPadding;
import io.github.bhuyanp.intellij.springbanner.theme.ThemeConfig;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import static io.github.bhuyanp.intellij.springbanner.ansi.Ansi.colorize;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringBannerGenerator {
    public static final SpringBannerGenerator INSTANCE = new SpringBannerGenerator();

    public String getBanner(SpringBannerConfig springBannerConfig) {
        String text = springBannerConfig.getText();
        ThemeConfig bannerTheme = springBannerConfig.getBannerTheme();
        String bannerFont = getBannerFont(springBannerConfig);
        if (text.isBlank()) return text;

        text = capitalizeProjectName(text);
        String banner = FigletBannerRenderer.SINGLETON.render(bannerFont, text);
        TextPadding textPadding;
        //For no background banners no left/right padding needed
        if (bannerTheme.hasBackColor()||bannerTheme.hasFrame()) {
            textPadding = TextPadding.getBannerPadding(bannerFont);
            if(FONTS_WITH_PADDING_CORRECTION.contains(bannerFont) && hasLowerCaseTallCharacter(text)){
                textPadding = TextPadding.getBannerPadding(bannerFont+"-withtall");
            }
            banner = textPadding.apply(banner);
        }
        banner = banner.lines()
                .map(line -> colorize(line, bannerTheme))
                .collect(Collectors.joining(System.lineSeparator()));

        banner = new TextPadding(1, 0, 1, 0).apply(banner);

        return banner;
    }


    boolean hasLowerCaseTallCharacter(String text){
        boolean hasL = false;
        for (char c : text.toCharArray()) {
            hasL = switch (c){
                case 'g', 'j', 'p', 'q', 'y' -> true;
                default -> false;
            };
            if(hasL)break;
        }
        return hasL;
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
