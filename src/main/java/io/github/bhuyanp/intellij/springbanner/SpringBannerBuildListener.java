package io.github.bhuyanp.intellij.springbanner;

import com.intellij.compiler.server.BuildManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import io.github.bhuyanp.intellij.springbanner.generator.SpringBannerGenerator;
import io.github.bhuyanp.intellij.springbanner.model.SpringBannerConfig;
import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.theme.THEME_OPTION;
import io.github.bhuyanp.intellij.springbanner.writer.BannerWriter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;


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
            String bannerText = state.bannerText;
            bannerText = StringUtil.isEmpty(bannerText) ? project.getName() : bannerText;
            THEME_OPTION themePreset = state.selectedTheme;
            String bannerFont = state.bannerFont;
            BUILD_TOOL buildTool = determineBuildTool(project);
            String generateBanner = generateBanner(bannerText, themePreset, bannerFont);

            writeBannerFile(buildTool, generateBanner, projectBasePath);

        } catch (Exception e) {
            log.error("Error while generating banner before build: ", e);
        }

    }

    private static void writeBannerFile(BUILD_TOOL buildTool, String generateBanner, String projectBasePath) {
        switch (buildTool) {
            case MAVEN -> BannerWriter.of(BannerWriter.WRITER_TYPE.MAVEN).write(generateBanner, projectBasePath);
            case GRADLE -> BannerWriter.of(BannerWriter.WRITER_TYPE.SOURCE).write(generateBanner, projectBasePath);
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

    private String generateBanner(String bannerText, THEME_OPTION themePreset, String bannerFont) {
        Theme theme = themePreset.getTheme();
        if (themePreset == THEME_OPTION.CUSTOM) {
            AppSettings.State state =
                    Objects.requireNonNull(AppSettings.getInstance().getState());
            theme = new Theme(state.bannerFontColor, state.bannerBackground, state.bannerFontBold, state.additionalEffect);
        }
        SpringBannerConfig springBannerConfig = SpringBannerConfig.builder()
                .text(bannerText)
                .bannerTheme(theme.getBannerTheme())
                .bannerFont(bannerFont)
                .build();
        return SpringBannerGenerator.INSTANCE.getBanner(springBannerConfig);

    }

    enum BUILD_TOOL {
        GRADLE,
        MAVEN
    }


    @Override
    public void buildFinished(@NotNull Project project, @NotNull UUID sessionId, boolean isAutomake) {

    }


    }
