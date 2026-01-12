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
            "cyberlarge",
            "elite",
            "elite",
            "georgia11",
            "lean",
            "epic",
            "lineblocks",
            "nancyj",
            "poison",
            "starwars",
            "puffy",
            "soft",
            "standard",
            "usaflag",
            "whimsy");
    public static final List<String> FONT_OPTIONS;
    static{
        FONT_OPTIONS = new ArrayList<>();
        FONT_OPTIONS.add(RANDOM_FONT);
        // de-duplicating default fonts before adding them to the options

        //de-duplicating default fonts before adding them to the options
        FONT_OPTIONS.addAll(DEFAULT_FONTS.stream().distinct().sorted().toList());
    }

}
