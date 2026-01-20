package io.github.bhuyanp.intellij.springbanner.generator;

import io.github.bhuyanp.intellij.springbanner.model.SpringCaptionConfig;
import io.github.bhuyanp.intellij.springbanner.theme.ThemeConfig;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;

import static io.github.bhuyanp.intellij.springbanner.ansi.Ansi.colorize;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/12/26
 */
public class SpringCaptionGenerator {
    public static final SpringCaptionGenerator INSTANCE = new SpringCaptionGenerator();

    public static final String KEY_JDK_VERSION = "$jdkVersion";
    public static final String KEY_SPRING_VERSION = "$springBootVersion";

    private static final String CAPTION_TEMPLATE_SPRING_BOOT = "Spring Boot      : "+KEY_SPRING_VERSION;
    private static final String CAPTION_TEMPLATE_JDK =         "JDK              : "+KEY_JDK_VERSION;

    private static final String DEFAULT_SPACING = " ";


    public String getCaption(SpringCaptionConfig springCaptionConfig) {
        String caption = "";
        String springVersion = springCaptionConfig.getSpringVersion();
        if(springCaptionConfig.isShowSpringVersion() && !StringUtils.isEmpty(springVersion)){
            caption += CAPTION_TEMPLATE_SPRING_BOOT.replace(KEY_SPRING_VERSION, springVersion)+System.lineSeparator();
        }
        String jdkVersion = springCaptionConfig.getJdkVersion();
        if(springCaptionConfig.isShowJDKVersion() && !StringUtils.isEmpty(jdkVersion)){
            caption += CAPTION_TEMPLATE_JDK.replace(KEY_JDK_VERSION, jdkVersion)+System.lineSeparator();
        }

        String captionText = springCaptionConfig.getCaptionText().trim();
        if(!StringUtils.isEmpty(captionText)){
            caption += captionText
                    .replace(KEY_SPRING_VERSION, springVersion)
                    .replace(KEY_JDK_VERSION, jdkVersion);
        }

        if(caption.isEmpty()) return caption;

        // Remove spaces around caption lines
        caption = caption.lines()
                .filter(line-> !line.isEmpty())
                .map(String::trim).collect(Collectors.joining(System.lineSeparator()));

        // Find the biggest line in caption
        int biggestLineLength = caption.lines().map(String::length).max(Integer::compareTo).get();

        // Add spaces to smaller lines to match the biggestLineLength
        caption = caption.lines()
                .map(line -> line + DEFAULT_SPACING.repeat(biggestLineLength - line.length()))
                .collect(Collectors.joining(System.lineSeparator()));

        ThemeConfig captionTheme = springCaptionConfig.getCaptionTheme();
        caption = caption.lines().map(line -> "| " + line).collect(Collectors.joining(System.lineSeparator()));
        caption = colorize(caption, captionTheme);
        return caption;
    }

}
