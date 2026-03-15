package io.github.bhuyanp.intellij.springbanner;

import com.intellij.java.library.JavaLibraryUtil;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.OrderEnumerator;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.text.StringUtil;
import io.github.bhuyanp.intellij.springbanner.generator.SpringBannerGenerator;
import io.github.bhuyanp.intellij.springbanner.generator.SpringCaptionGenerator;
import io.github.bhuyanp.intellij.springbanner.model.SpringBannerConfig;
import io.github.bhuyanp.intellij.springbanner.model.SpringCaptionConfig;
import io.github.bhuyanp.intellij.springbanner.theme.THEME_OPTION;
import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.theme.ThemeConfig;
import io.github.bhuyanp.intellij.springbanner.util.FunkyBannerBundle;
import io.github.bhuyanp.intellij.springbanner.writer.BannerWriter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.ivy.util.Message;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.bhuyanp.intellij.springbanner.ansi.Attribute.NONE;
import static io.github.bhuyanp.intellij.springbanner.ansi.Attribute.TEXT_COLOR;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.*;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.BLANK;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/25/26
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BannerBuilder {
    public static final BannerBuilder INSTANCE = new BannerBuilder();

    private static final String POM_XML = "pom.xml";
    private static final String BUILD_GRADLE = "build.gradle";
    private static final String BUILD_GRADLE_KTS = "build.gradle.kts";

    public void build(@NotNull Project project, boolean isAutomake, boolean showNotification) {
        if (isAutomake) return;
        String rootProjectPath = Objects.requireNonNull(project.getBasePath(), "project base path missing");
        ProjectSettings.State projectSettings = Objects.requireNonNull(ProjectSettings.getState(project.getName()));
        AppSettings.State settings = projectSettings.useProjectSpecificSetting ? projectSettings : Objects.requireNonNull(AppSettings.getInstance().getState());

        try {
            Files.walk(Path.of(rootProjectPath))
                    .filter(path -> path.getFileName().toString().equals(POM_XML) ||
                            path.getFileName().toString().equals(BUILD_GRADLE) ||
                            path.getFileName().toString().equals(BUILD_GRADLE_KTS))
                    .forEach(buildFilePath -> {
                        CompletableFuture.runAsync(() -> {
                            try {
                                String projectPath = buildFilePath.getParent().toString();
                                String projectFolderName = buildFilePath.getParent().getFileName().toString();
                                boolean isRootProject = projectPath.equalsIgnoreCase(rootProjectPath);
                                BUILD_TOOL buildTool = determineBuildTool(projectPath);
                                if (buildTool == BUILD_TOOL.UNDETECTED) return;

                                String generatedBanner = BLANK;
                                if (settings.showBanner) {
                                    generatedBanner = generateBanner(project, projectFolderName, isRootProject, settings);
                                }
                                String generatedCaption = BLANK;
                                if (settings.showCaption) {
                                    generatedCaption = generateCaption(project, buildFilePath, settings, buildTool);
                                }

                                String finalText;
                                if (StringUtils.isEmpty(generatedBanner) && StringUtils.isEmpty(generatedCaption)) {
                                    finalText = BLANK;
                                } else if (!StringUtils.isEmpty(generatedBanner) && !StringUtils.isEmpty(generatedCaption)) {
                                    finalText = generatedBanner + System.lineSeparator().repeat(2) + generatedCaption;
                                } else if (!StringUtils.isEmpty(generatedBanner)) {
                                    finalText = generatedBanner;
                                } else {
                                    finalText = generatedCaption;
                                }
                                finalText = !StringUtils.isEmpty(finalText) ? System.lineSeparator() + finalText + System.lineSeparator() : finalText;
                                writeBannerFile(finalText, projectPath);
                                if (showNotification)
                                    BannerNotifier.notify(project, FunkyBannerBundle.message("sbb.msg.banner-generated"));
                            } catch (Exception e) {
                                log.error("Error while generating banner for {}", buildFilePath.getParent().toString(), e);
                            }
                        });
                    });
        } catch (IOException e) {
            log.error("Error while scanning project directory for build scripts", e);
        }
    }


    private String generateBanner(Project project, String projectFolderName, boolean isRootProject, AppSettings.State settings) {
        String bannerText = settings.bannerText;
        String projectName = isRootProject ? project.getName() : projectFolderName;
        bannerText = StringUtil.isEmpty(bannerText) ? projectName : bannerText;

        THEME_OPTION themePreset = settings.selectedTheme;
        String bannerFont = settings.bannerFont;
        ThemeConfig bannerThemeConfig;
        if (themePreset == THEME_OPTION.CUSTOM) {
            Theme.ADDITIONAL_EFFECT additionalEffect = settings.additionalEffect;
            bannerThemeConfig = settings.addBGColor ?
                    new Theme(settings.bannerFontColor, settings.bannerBackground, settings.bannerFontBold, additionalEffect).getBannerTheme() :
                    new Theme(settings.bannerFontColor, settings.bannerFontBold, additionalEffect).getBannerTheme();
        } else {
            boolean isDarkTheme = EditorColorsManager.getInstance().isDarkEditor();
            bannerThemeConfig = Theme.getBannerTheme(themePreset, isDarkTheme);
        }
        SpringBannerConfig springBannerConfig = SpringBannerConfig.builder()
                .text(bannerText)
                .bannerTheme(bannerThemeConfig)
                .bannerFont(bannerFont)
                .build();
        return SpringBannerGenerator.INSTANCE.getBanner(springBannerConfig);
    }

    private String generateCaption(Project project, Path buildFilePath, AppSettings.State settings, BUILD_TOOL buildTool) {
        THEME_OPTION themePreset = settings.selectedTheme;
        SpringCaptionConfig springCaptionConfig = new SpringCaptionConfig(settings);

        springCaptionConfig.setAppVersion(getAppVersion(buildFilePath, buildTool));
        springCaptionConfig.setSpringVersion(getSpringBootVersion(project));
        springCaptionConfig.setJdkVersion(getSDKVersion(project));


        if (themePreset == THEME_OPTION.CUSTOM) {
            List<Integer> captionColor = settings.captionColor;
            springCaptionConfig.setCaptionTheme(
                    new ThemeConfig(TEXT_COLOR(captionColor.get(0), captionColor.get(1), captionColor.get(2)),
                            NONE(),
                            NONE())
            );
        } else {
            boolean isDarkTheme = EditorColorsManager.getInstance().isDarkEditor();
            springCaptionConfig.setCaptionTheme(Theme.getCaptionTheme(themePreset, isDarkTheme));
        }
        return SpringCaptionGenerator.INSTANCE.getCaption(springCaptionConfig);
    }

    private static void writeBannerFile(String generatedBanner, String projectBasePath) {
        BannerWriter.of(BannerWriter.WRITER_TYPE.SOURCE).write(generatedBanner, projectBasePath);
    }


    private BUILD_TOOL determineBuildTool(String projectBasePath) {
        if (Files.exists(Path.of(projectBasePath, BUILD_GRADLE)))
            return BUILD_TOOL.GRADLE_GROOVY;
        else if (Files.exists(Path.of(projectBasePath, BUILD_GRADLE_KTS)))
            return BUILD_TOOL.GRADLE_KOTLIN;
        else if (Files.exists(Path.of(projectBasePath, POM_XML)))
            return BUILD_TOOL.MAVEN;
        else
            return BUILD_TOOL.UNDETECTED;
    }


    enum BUILD_TOOL {
        GRADLE_GROOVY,
        GRADLE_KOTLIN,
        MAVEN,
        UNDETECTED
    }

    private String getSpringBootVersion(@NotNull Project project) {
        val springBootVersion = new AtomicReference<String>();
        OrderEnumerator orderEnumerator = OrderEnumerator.orderEntries(project).recursively();
        orderEnumerator.forEachLibrary(library -> {
            val coordinate = JavaLibraryUtil.getMavenCoordinates(library);
            if (coordinate != null &&
                    SPRING_BOOT_GROUP_ID.equalsIgnoreCase(coordinate.getGroupId()) &&
                    SPRING_BOOT_ARTIFACT_ID.equalsIgnoreCase(coordinate.getArtifactId())) {
                springBootVersion.set(coordinate.getVersion());
                return false;
            }
            return true;
        });
        return springBootVersion.get();
    }


    private String getSDKVersion(@NotNull Project project) {
        Sdk projectSdk = ProjectRootManager.getInstance(project).getProjectSdk();
        assert projectSdk != null;
        return projectSdk.getVersionString();
    }

    private String getAppVersion(Path buildFilePath, BUILD_TOOL buildTool) {
        return switch (buildTool) {
            case MAVEN -> getMavenAppVersion(buildFilePath);
            case GRADLE_KOTLIN, GRADLE_GROOVY -> getGradleAppVersion(buildFilePath);
            case UNDETECTED -> BLANK;
        };
    }

    private static String getMavenAppVersion(Path mavenScript) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            Model model = reader.read(new FileReader(mavenScript.toFile()));
            return model.getVersion();
        } catch (Exception e) {
            return BLANK;
        }
    }

    private String getGradleAppVersion(Path gradleScriptPath) {
        try {
            String scriptContent = Files.readString(gradleScriptPath);
            String versionLine = scriptContent.lines()
                    .filter(line -> line.contains("version") && line.contains("="))
                    .findFirst()
                    .orElse(BLANK);
            if (versionLine.equals(BLANK)) return BLANK;
            return versionLine
                    .replace("version", BLANK)
                    .replace("\"", BLANK)
                    .replace(" ", BLANK)
                    .replace("=", BLANK)
                    .replace("'", BLANK);
        } catch (Exception e) {
            return BLANK;
        }
    }


}
