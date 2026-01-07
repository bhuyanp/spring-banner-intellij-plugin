package io.github.bhuyanp.intellij.springbanner.writer;

import com.intellij.openapi.util.text.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/3/26
 */
@Slf4j
public abstract class BannerWriter {
    private static final String FILENAME = "banner.txt";

    public void write(String bannerText, String projectBasePath) {
        try {
            if (StringUtil.isEmpty(bannerText)) return;
            Path path = getPath(projectBasePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
            Files.write(path, (bannerText).getBytes(), StandardOpenOption.CREATE);
        } catch (Exception e) {
            log.error("Error writing the banner file.", e);
        }
    }

    abstract Path getPath(String projectBasePath);

    private static class MavenWriter extends BannerWriter {
        @Override
        Path getPath(String projectBasePath) {
            return Path.of(projectBasePath, "target", "classes", FILENAME);
        }
    }

    private static class GradleWriter extends BannerWriter {
        @Override
        Path getPath(String projectBasePath) {
            return Path.of(projectBasePath, "build", "resources", "main", FILENAME);
        }
    }

    private static class SourceWriter extends BannerWriter {
        @Override
        Path getPath(String projectBasePath) {
            return Path.of(projectBasePath, "src", "main", "resources", FILENAME);
        }
    }

    public static final BannerWriter of(WRITER_TYPE writerType) {
        return switch (writerType) {
            case MAVEN -> new MavenWriter();
            case GRADLE -> new GradleWriter();
            case SOURCE -> new SourceWriter();
        };
    }

    public enum WRITER_TYPE {
        MAVEN, GRADLE, SOURCE
    }
}
