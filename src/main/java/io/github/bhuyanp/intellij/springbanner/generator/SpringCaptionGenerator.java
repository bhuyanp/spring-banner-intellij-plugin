package io.github.bhuyanp.intellij.springbanner.generator;

import io.github.bhuyanp.intellij.springbanner.model.SpringCaptionConfig;
import io.github.bhuyanp.intellij.springbanner.theme.TextPadding;
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

    private static final String CAPTION_TEMPLATE_SPRING_BOOT = "Spring Boot      : %s";
    private static final String CAPTION_TEMPLATE_JDK =         "JDK              : %s";

    private static final String DEFAULT_SPACING = " ";

    public String getCaption(SpringCaptionConfig springCaptionConfig) {
        String springBootCaption = "";
        if(!StringUtils.isEmpty(springCaptionConfig.getSpringVersion())){
            springBootCaption = CAPTION_TEMPLATE_SPRING_BOOT.formatted(springCaptionConfig.getSpringVersion());
        }
        String jdkCaption = "";
        if(!StringUtils.isEmpty(springCaptionConfig.getJdkVersion())){
            jdkCaption = CAPTION_TEMPLATE_JDK.formatted(springCaptionConfig.getJdkVersion());
        }
        if(StringUtils.isEmpty(springBootCaption) && StringUtils.isEmpty(jdkCaption)) return "";

        String caption = "";
        if(StringUtils.isEmpty(springBootCaption)) {
            caption = jdkCaption;
        } else if(StringUtils.isEmpty(jdkCaption)) {
            caption = springBootCaption;
        } else {
            caption = springBootCaption+System.lineSeparator()+jdkCaption;
        }

        // Remove spaces around caption lines
        caption = caption.lines().map(String::trim).collect(Collectors.joining(System.lineSeparator()));

        // Find the biggest line in caption
        int biggestLineLength = caption.lines().map(String::length).max(Integer::compareTo).get();


        // Add spaces to smaller lines to match the biggestLineLength
        caption = caption.lines()
                .map(line -> line + DEFAULT_SPACING.repeat(biggestLineLength - line.length()))
                .collect(Collectors.joining(System.lineSeparator()));

        ThemeConfig captionTheme = springCaptionConfig.getCaptionTheme();
        //For no background, padding not needed for captions
        boolean addPadding = captionTheme.hasBackColor();
        if (addPadding) {
            caption = new TextPadding(1, 2, 1, 2).apply(caption);
        } else {
            caption = caption.lines().map(line -> "| " + line).collect(Collectors.joining(System.lineSeparator()));
        }
        caption = colorize(caption, captionTheme);
        caption = new TextPadding(0, 0, 1, 0).apply(caption);
        return caption;
    }

}
