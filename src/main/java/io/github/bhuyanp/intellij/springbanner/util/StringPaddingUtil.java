package io.github.bhuyanp.intellij.springbanner.util;

import java.util.Optional;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/11/26
 */
public class StringPaddingUtil {

    public static String addPadding(String subject, int desiredLength) {
        Optional.ofNullable(subject).orElseThrow();
        int subjectLength = subject.length();
        if (desiredLength < 1 || subjectLength >= desiredLength) return subject;
        int paddingLength = desiredLength - subjectLength;
        return subject + String.format("%-" + paddingLength + "s", " ");
    }

}
