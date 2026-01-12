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
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.SPRING_BOOT_ARTIFACT_ID;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.SPRING_BOOT_GROUP_ID;


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
            AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());

            AppSettings.Setting setting = state.globalSetting;
            AppSettings.ProjectSpecificSetting projectSpecificSetting = state.projectSpecificSettings.get(project.getName());
            setting = projectSpecificSetting == null || !projectSpecificSetting.useProjectSpecificSetting ? setting : projectSpecificSetting;

            BUILD_TOOL buildTool = determineBuildTool(project);
            String generatedBanner = generateBanner(project, setting);
            String generatedCaption = generateCaption(project, setting);
            String finalResult = generatedBanner+System.lineSeparator()+generatedCaption;
            writeBannerFile(buildTool, finalResult, projectBasePath);
        } catch (Exception e) {
            log.error("Error while generating banner before build: ", e);
        }

    }



    private static void writeBannerFile(BUILD_TOOL buildTool, String generateBanner, String projectBasePath) {
        switch (buildTool) {
            case MAVEN -> BannerWriter.of(BannerWriter.WRITER_TYPE.MAVEN).write(generateBanner, projectBasePath);
            case GRADLE -> BannerWriter.of(BannerWriter.WRITER_TYPE.GRADLE).write(generateBanner, projectBasePath);
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
            throw new RuntimeException("Unable to determine build tool for project: " + project.getName());
    }

    private String generateBanner(Project project, AppSettings.Setting settings) {
        String bannerText = settings.bannerText;
        bannerText = StringUtil.isEmpty(bannerText) ? project.getName() : bannerText;
        THEME_OPTION themePreset = settings.selectedTheme;
        String bannerFont = settings.bannerFont;
        ThemeConfig bannerThemeConfig;
        if (themePreset == THEME_OPTION.CUSTOM) {
            bannerThemeConfig = settings.addBGColor ?
                    new Theme(settings.bannerFontColor, settings.bannerBackground, settings.bannerFontBold, settings.additionalEffect).getBannerTheme() :
                    new Theme(settings.bannerFontColor, settings.bannerFontBold, settings.additionalEffect).getBannerTheme();
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


    private String generateCaption(Project project, AppSettings.Setting settings) {
        THEME_OPTION themePreset = settings.selectedTheme;
        boolean isDarkTheme = EditorColorsManager.getInstance().isDarkEditor();
        SpringCaptionConfig springCaptionConfig = SpringCaptionConfig.builder()
                .springVersion(getSpringBootVersion(project))
                .jdkVersion(getSDKVersion(project))
                .captionTheme(Theme.getCaptionTheme(themePreset, isDarkTheme))
                .build();
        return SpringCaptionGenerator.INSTANCE.getCaption(springCaptionConfig);
    }

    enum BUILD_TOOL {
        GRADLE,
        MAVEN
    }

    private String getSpringBootVersion(@NotNull Project project) {
        final AtomicReference<String> springBootVersion = new AtomicReference<>();
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
