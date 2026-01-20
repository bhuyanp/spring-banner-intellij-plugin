package io.github.bhuyanp.intellij.springbanner.writer;

import com.intellij.openapi.util.text.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

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
            if (StringUtil.isEmpty(projectBasePath)) return;
            List<List<String>> targetDirs = getTargetDirectories(projectBasePath);
            targetDirs.forEach(targetDir -> {
                try {
                    String directory = String.join("/", targetDir);
                    Path bannerFile = Path.of(directory, FILENAME);
                    Path directoryPath = Path.of(directory);
                    if(!Files.exists(directoryPath)){
                        Files.createDirectories(directoryPath);
                    } else {
                        Files.deleteIfExists(bannerFile);
                    }
                    Files.write(bannerFile, (bannerText).getBytes(), StandardOpenOption.CREATE);
                } catch (IOException e) {
                    log.error("Error writing the banner file.", e);
                }
            });
        } catch (Exception e) {
            log.error("Error writing the banner file.", e);
        }
    }

    abstract List<List<String>> getTargetDirectories(String projectBasePath);

    private static class MavenWriter extends BannerWriter {
        @Override
        List<List<String>> getTargetDirectories(String projectBasePath) {
            return List.of(List.of(projectBasePath, "target", "classes"));
        }
    }

    private static class GradleWriter extends BannerWriter {
        @Override
        List<List<String>> getTargetDirectories(String projectBasePath) {
            return List.of(
                    List.of(projectBasePath, "out", "production", "resources"),
                    List.of(projectBasePath, "build", "resources", "main")
            );
        }
    }

    private static class SourceWriter extends BannerWriter {
        @Override
        List<List<String>> getTargetDirectories(String projectBasePath) {
            return List.of(List.of(projectBasePath, "src", "main", "resources"));
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
