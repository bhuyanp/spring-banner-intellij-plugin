package io.github.bhuyanp.intellij.springbanner;

import com.intellij.compiler.server.BuildManagerListener;
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
import io.github.bhuyanp.intellij.springbanner.writer.BannerWriter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.ivy.util.Message;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.bhuyanp.intellij.springbanner.ansi.Attribute.NONE;
import static io.github.bhuyanp.intellij.springbanner.ansi.Attribute.TEXT_COLOR;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.*;


/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/1/26
 */
@Slf4j
public class SpringBannerBuildListener implements BuildManagerListener {

    private static final String POM_XML = "pom.xml";
    private static final String BUILD_GRADLE = "build.gradle";
    private static final String BUILD_GRADLE_KTS = "build.gradle.kts";

    @Override
    public void buildStarted(@NotNull Project project, @NotNull UUID sessionId, boolean isAutomake) {
        if(isAutomake) return;
        BuildManagerListener.super.beforeBuildProcessStarted(project, sessionId);
        try {
            String projectBasePath = Objects.requireNonNull(project.getBasePath(), "project base path");
            ProjectSettings.State projectSettings = Objects.requireNonNull(ProjectSettings.getState(project.getName()));
            AppSettings.State settings = projectSettings.useProjectSpecificSetting ? projectSettings : Objects.requireNonNull(AppSettings.getInstance().getState());

            BUILD_TOOL buildTool = determineBuildTool(project);
            if(buildTool==BUILD_TOOL.UNDETECTED) return;

            String generatedBanner = BLANK;
            if (settings.showBanner) {
                generatedBanner = generateBanner(project, settings);
            }
            String generatedCaption = BLANK;
            if (settings.showCaption) {
                generatedCaption = generateCaption(project, settings, buildTool);
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
            writeBannerFile(buildTool, finalText, projectBasePath);
        } catch (Exception e) {
        }
    }


    private String generateBanner(Project project, AppSettings.State settings) {
        String bannerText = settings.bannerText;
        bannerText = StringUtil.isEmpty(bannerText) ? project.getName() : bannerText;
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

    private String generateCaption(Project project, AppSettings.State settings, BUILD_TOOL buildTool) {
        THEME_OPTION themePreset = settings.selectedTheme;
        SpringCaptionConfig springCaptionConfig = new SpringCaptionConfig(settings);

        springCaptionConfig.setAppVersion(getAppVersion(project, buildTool));
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

    private static void writeBannerFile(BUILD_TOOL buildTool, String generatedBanner, String projectBasePath) {
        switch (buildTool) {
            case MAVEN -> BannerWriter.of(BannerWriter.WRITER_TYPE.MAVEN).write(generatedBanner, projectBasePath);
            case GRADLE_GROOVY, GRADLE_KOTLIN -> BannerWriter.of(BannerWriter.WRITER_TYPE.GRADLE).write(generatedBanner, projectBasePath);
            case UNDETECTED ->
                    Message.info("No Gradle or Maven build scripts detected at project root. Spring Boot banner not generated.");
        }
    }


    private BUILD_TOOL determineBuildTool(Project project) {
        String projectBasePath = Objects.requireNonNull(project.getBasePath(), "project base path");
        Path gradleScript = Path.of(projectBasePath, BUILD_GRADLE);
        Path gradleScriptKotlinDSL = Path.of(projectBasePath, BUILD_GRADLE_KTS);
        Path mavenScript = Path.of(projectBasePath, POM_XML);
        if (Files.exists(gradleScript))
            return BUILD_TOOL.GRADLE_GROOVY;
        else if(Files.exists(gradleScriptKotlinDSL))
            return BUILD_TOOL.GRADLE_KOTLIN;
        else if (Files.exists(mavenScript))
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

    private String getAppVersion(Project project, BUILD_TOOL buildTool) {
        String projectBasePath = Objects.requireNonNull(project.getBasePath(), "project base path missing");
        Path mavenScript = Path.of(projectBasePath, POM_XML);
        Path gradleScript = Path.of(projectBasePath, BUILD_GRADLE);
        Path gradleKotlinScript = Path.of(projectBasePath, BUILD_GRADLE_KTS);

        return switch (buildTool){
            case MAVEN ->getMavenAppVersion(mavenScript);
            case GRADLE_KOTLIN -> getGradleAppVersion(gradleKotlinScript);
            case GRADLE_GROOVY -> getGradleAppVersion(gradleScript);
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
            String versionLine =  scriptContent.lines()
                    .filter(line->line.contains("version") && line.contains("="))
                    .findFirst()
                    .orElse(BLANK);
            if(versionLine.equals(BLANK)) return BLANK;
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
