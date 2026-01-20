package io.github.bhuyanp.intellij.springbanner.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/5/26
 */
public class PluginConstants {

    public static final String SPACE = " ";
    public static final String EMPTY = "";

    public static final String SPRING_BOOT_GROUP_ID = "org.springframework.boot";
    public static final String SPRING_BOOT_ARTIFACT_ID = "spring-boot-starter";
    public static final String RANDOM_FONT = "-- Random --";
    public static final List<String> DEFAULT_FONTS = List.of(
            "3d",
            "3d",
            "4max",
            "ansiregular",
            "ansishadow",
            "ansishadow",
            "banner3_d",
            "banner4",
            "bigmoneyne",
            "block",
            "bolger",
            "calvins",
            "colossal",
            "computer",
            "cyberlarge",
            "cyberlarge",
            "doom",
            "elite",
            "elite",
            "epic",
            "fender",
            "georgia11",
            "lean",
            "lineblocks",
            "lineblocks",
            "nancyj",
            "nancyjunderlined",
            "poison",
            "puffy",
            "small",
            "smslant",
            "slant",
            "soft",
            "standard",
            "starwars",
            "stop",
            "univers",
            "usaflag",
            "whimsy");
    public static final List<String> FONTS_WITH_PADDING_CORRECTION =List.of("bigmoneyne", "doom", "lean","stop");

    public static final List<String> FONT_OPTIONS;
    static{
        FONT_OPTIONS = new ArrayList<>();
        FONT_OPTIONS.add(RANDOM_FONT);
        // de-duplicating default fonts before adding them to the options

        //de-duplicating default fonts before adding them to the options
        FONT_OPTIONS.addAll(DEFAULT_FONTS.stream().distinct().sorted().toList());
    }

}
