// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package io.github.bhuyanp.intellij.springbanner;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
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

    private ProjectSettings.State getCurrentSettings() {
        String projectName = getCurrentActiveProjectName();
        ProjectSettings.State state = ProjectSettings.getInstance(projectName).getState();
        return state == null ? new ProjectSettings.State() : state;
    }

    @Override
    public boolean isModified() {
        ProjectSettings.State projectSpecificSetting = getCurrentSettings();
        return isModified(projectComponent, projectSpecificSetting) ||
                projectComponent.getUseProjectSpecificSetting() != projectSpecificSetting.useProjectSpecificSetting;
    }


    @Override
    public void apply() {
        String currentActiveProjectName = getCurrentActiveProjectName();
        if (StringUtil.isEmpty(currentActiveProjectName)) {
            log.warn("No active project found. Cannot apply project specific settings.");
            return;
        }
        ProjectSettings.State settings = getCurrentSettings();
        apply(projectComponent, settings);
        settings.useProjectSpecificSetting = projectComponent.getUseProjectSpecificSetting();
    }

    @Override
    public void reset() {
        ProjectSettings.State projectSpecificSetting = getCurrentSettings();
        reset(projectComponent, projectSpecificSetting);
        projectComponent.setUseProjectSpecificSetting(projectSpecificSetting.useProjectSpecificSetting);
    }

    @Override
    public void disposeUIResources() {
        projectComponent = null;
    }

}
