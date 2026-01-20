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
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public void beforeBuildProcessStarted(@NotNull Project project, @NotNull UUID sessionId) {
        BuildManagerListener.super.beforeBuildProcessStarted(project, sessionId);
        try {
            String projectBasePath = Objects.requireNonNull(project.getBasePath(), "project base path");
            ProjectSettings.State projectSettings = Objects.requireNonNull(ProjectSettings.getState(project.getName()));
            AppSettings.State settings = projectSettings.useProjectSpecificSetting ? projectSettings : Objects.requireNonNull(AppSettings.getInstance().getState());

            String generatedBanner = EMPTY;
            if (settings.showBanner) {
                generatedBanner = generateBanner(project, settings);
            }
            String generatedCaption = EMPTY;
            if (settings.showCaption) {
                generatedCaption = generateCaption(project, settings);
            }

            String finalText;
            if(StringUtils.isEmpty(generatedBanner) && StringUtils.isEmpty(generatedCaption)){
                finalText = EMPTY;
            } else if(!StringUtils.isEmpty(generatedBanner) && !StringUtils.isEmpty(generatedCaption)) {
                finalText = generatedBanner + System.lineSeparator().repeat(2) + generatedCaption;
            } else if(!StringUtils.isEmpty(generatedBanner)){
                finalText = generatedBanner;
            } else {
                finalText = generatedCaption;
            }

            finalText = !StringUtils.isEmpty(finalText)?System.lineSeparator()+finalText+System.lineSeparator():finalText;
            BUILD_TOOL buildTool = determineBuildTool(project);
            writeBannerFile(buildTool, finalText, projectBasePath);
        } catch (Exception e) {
            log.error("Error while generating banner before build: ", e);
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

    private String generateCaption(Project project, AppSettings.State settings) {
        THEME_OPTION themePreset = settings.selectedTheme;
        SpringCaptionConfig springCaptionConfig;
        SpringCaptionConfig.SpringCaptionConfigBuilder springCaptionConfigBuilder = SpringCaptionConfig.builder()
                .springVersion(getSpringBootVersion(project))
                .jdkVersion(getSDKVersion(project))
                .showSpringVersion(settings.showSpringVersionInCaption)
                .showJDKVersion(settings.showJDKVersionInCaption)
                .captionText(settings.captionText);
        if (themePreset == THEME_OPTION.CUSTOM) {
            List<Integer> captionColor = settings.captionColor;
            springCaptionConfig = springCaptionConfigBuilder
                    .captionTheme(new ThemeConfig(TEXT_COLOR(captionColor.get(0), captionColor.get(1), captionColor.get(2)),
                            NONE(), NONE()))
                    .build();
        } else {
            boolean isDarkTheme = EditorColorsManager.getInstance().isDarkEditor();
            springCaptionConfig = springCaptionConfigBuilder
                    .captionTheme(Theme.getCaptionTheme(themePreset, isDarkTheme))
                    .build();
        }
        return SpringCaptionGenerator.INSTANCE.getCaption(springCaptionConfig);
    }

    private static void writeBannerFile(BUILD_TOOL buildTool, String generatedBanner, String projectBasePath) {
        switch (buildTool) {
            case MAVEN -> BannerWriter.of(BannerWriter.WRITER_TYPE.MAVEN).write(generatedBanner, projectBasePath);
            case GRADLE -> BannerWriter.of(BannerWriter.WRITER_TYPE.GRADLE).write(generatedBanner, projectBasePath);
            case UNDETECTED ->
                    Message.info("No Gradle or Maven build scripts detected at the project root. Spring Boot banner not generated.");
        }
    }


    private BUILD_TOOL determineBuildTool(Project project) {
        String projectBasePath = Objects.requireNonNull(project.getBasePath(), "project base path");
        Path gradleScript = Path.of(projectBasePath, "build.gradle");
        Path gradleScriptKotlinDSL = Path.of(projectBasePath, "build.gradle.kts");
        Path mavenScript = Path.of(projectBasePath, "pom.xml");
        if (Files.exists(gradleScript) || Files.exists(gradleScriptKotlinDSL))
            return BUILD_TOOL.GRADLE;
        else if (Files.exists(mavenScript))
            return BUILD_TOOL.MAVEN;
        else
            return BUILD_TOOL.UNDETECTED;
    }


    enum BUILD_TOOL {
        GRADLE,
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

}
