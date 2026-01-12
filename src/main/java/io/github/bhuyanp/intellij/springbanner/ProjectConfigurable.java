// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package io.github.bhuyanp.intellij.springbanner;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Provides controller functionality for application settings.
 */
@Slf4j
final class ProjectConfigurable extends GlobalConfigurable {

    private ProjectComponent projectComponent;

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return ResourceBundle.getBundle("messages.SpringBanner").getString("sbb.plugin.setting.project");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return projectComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        projectComponent = new ProjectComponent();
        return projectComponent.getPanel();
    }


    private @NotNull String getCurrentActiveProjectName() {
        Project activeProject = ProjectUtil.getActiveProject();
        if (activeProject != null) {
            return activeProject.getName();
        } else {
            return "";
        }
    }

    private AppSettings.ProjectSpecificSetting getCurrentSettings() {
        String projectName = getCurrentActiveProjectName();
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        AppSettings.ProjectSpecificSetting projectSpecificSetting = state.projectSpecificSettings.get(projectName);
        return projectSpecificSetting == null ? new AppSettings.ProjectSpecificSetting() : projectSpecificSetting;
    }

    @Override
    public boolean isModified() {
        AppSettings.ProjectSpecificSetting projectSpecificSetting = getCurrentSettings();
        return !projectComponent.getBannerText().equalsIgnoreCase(projectSpecificSetting.bannerText) ||
                projectComponent.getTheme() != projectSpecificSetting.selectedTheme ||
                !projectComponent.getBannerFont().equalsIgnoreCase(projectSpecificSetting.bannerFont) ||
                projectComponent.getBannerFontBold() != projectSpecificSetting.bannerFontBold ||
                !equals(projectComponent.getBannerFontColor(), projectSpecificSetting.bannerFontColor) ||
                !equals(projectComponent.getBannerBackground(), projectSpecificSetting.bannerBackground) ||
                !projectComponent.getAdditionalEffect().equals(projectSpecificSetting.additionalEffect) ||
                projectComponent.getUseProjectSpecificSetting() != projectSpecificSetting.useProjectSpecificSetting;
    }


    @Override
    public void apply() {
        String currentActiveProjectName = getCurrentActiveProjectName();
        if(StringUtil.isEmpty(currentActiveProjectName)){
            log.warn("No active project found. Cannot apply project specific settings.");
            return;
        }
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        AppSettings.ProjectSpecificSetting setting = getCurrentSettings();
        setting.bannerText = projectComponent.getBannerText();
        setting.selectedTheme = projectComponent.getTheme();
        setting.bannerFont = projectComponent.getBannerFont();
        setting.bannerFontBold = projectComponent.getBannerFontBold();
        Color bannerFontColor = projectComponent.getBannerFontColor();
        Color bannerBackgroundCOLOR = projectComponent.getBannerBackground();
        setting.bannerFontColor = List.of(bannerFontColor.getRed(), bannerFontColor.getGreen(), bannerFontColor.getBlue());
        setting.bannerBackground = List.of(bannerBackgroundCOLOR.getRed(), bannerBackgroundCOLOR.getGreen(), bannerBackgroundCOLOR.getBlue());
        setting.additionalEffect = projectComponent.getAdditionalEffect();
        setting.useProjectSpecificSetting = projectComponent.getUseProjectSpecificSetting();
        state.projectSpecificSettings.put(currentActiveProjectName, setting);
    }

    @Override
    public void reset() {
        AppSettings.ProjectSpecificSetting projectSpecificSetting = getCurrentSettings();
        projectComponent.setBannerText(projectSpecificSetting.bannerText);
        projectComponent.setTheme(projectSpecificSetting.selectedTheme);
        projectComponent.setBannerFont(projectSpecificSetting.bannerFont);
        projectComponent.setBannerFontBold(projectSpecificSetting.bannerFontBold);
        @NonNls List<Integer> bannerFontColor = projectSpecificSetting.bannerFontColor;
        @NonNls List<Integer> bannerBackground = projectSpecificSetting.bannerBackground;
        projectComponent.setBannerFontColor(new Color(bannerFontColor.get(0), bannerFontColor.get(1), bannerFontColor.get(2)));
        projectComponent.setBannerBackground(new Color(bannerBackground.get(0), bannerBackground.get(1), bannerBackground.get(2)));
        projectComponent.setAdditionalEffect(projectSpecificSetting.additionalEffect);
        projectComponent.setUseProjectSpecificSetting(projectSpecificSetting.useProjectSpecificSetting);
    }

    @Override
    public void disposeUIResources() {
        projectComponent = null;
    }

}
