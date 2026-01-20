// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package io.github.bhuyanp.intellij.springbanner;

import com.intellij.ide.impl.ProjectUtil;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class ProjectComponent extends GlobalComponent {
    private final JBCheckBox useProjectSpecificSettingCheckBox = new JBCheckBox("Enable Project Specific Settings");

    public ProjectComponent() {
        super();
        useProjectSpecificSettingCheckBox.setFocusable(false);
        useProjectSpecificSettingCheckBox.addActionListener(new CheckBoxActionListener());
        Project activeProject = ProjectUtil.getActiveProject();
        String projectNameLabelString = "";
        if(activeProject!=null){
            projectNameLabelString = "Active Project [" + activeProject.getName() + "]";
        }
        JBLabel projectNameLabel = new JBLabel(projectNameLabelString, UIUtil.ComponentStyle.LARGE, UIUtil.FontColor.BRIGHTER);
        JPanel projectSpecificSettings = FormBuilder.createFormBuilder()
                .addComponent(projectNameLabel)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addComponent(useProjectSpecificSettingCheckBox)
                .getPanel();
        myMainPanel.add(projectSpecificSettings, 0);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return useProjectSpecificSettingCheckBox;
    }

    public boolean getUseProjectSpecificSetting() {
        return useProjectSpecificSettingCheckBox.isSelected();
    }

    public void setUseProjectSpecificSetting(boolean useProjectSpecificSetting) {
        useProjectSpecificSettingCheckBox.setSelected(useProjectSpecificSetting);
        showSettings(useProjectSpecificSetting);
    }

    private void showSettings(boolean useProjectSpecificSetting) {
        showBannerCheckBox.setVisible(useProjectSpecificSetting);
        mainSettingsForm.setVisible(useProjectSpecificSetting && getShowBanner());
        customBannerSettingsForm.setVisible(useProjectSpecificSetting && getShowBanner() && showCustomSettings());

        showCaptionCheckBox.setVisible(useProjectSpecificSetting);
        customCaptionSettingsForm.setVisible(useProjectSpecificSetting && getShowCaption());
    }

    class CheckBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean useProjectSpecificSetting = useProjectSpecificSettingCheckBox.isSelected();
            showSettings(useProjectSpecificSetting);
        }
    }

}
