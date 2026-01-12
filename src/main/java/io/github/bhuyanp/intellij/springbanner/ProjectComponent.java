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
    private final JBCheckBox useProjectSpecificSettingCheckBox = new JBCheckBox();

    public ProjectComponent() {
        super();
        useProjectSpecificSettingCheckBox.setFocusable(false);
        CheckBoxActionListener checkBoxActionListener = new CheckBoxActionListener();
        useProjectSpecificSettingCheckBox.addActionListener(checkBoxActionListener);
        Project activeProject = ProjectUtil.getActiveProject();
        String projectNameLabelString = "";
        if(activeProject!=null){
            projectNameLabelString = "Active Project [" + activeProject.getName() + "]";
        }
        JBLabel projectNameLabel = new JBLabel(projectNameLabelString, UIUtil.ComponentStyle.LARGE, UIUtil.FontColor.BRIGHTER);
        JPanel projectSpecificSettings = FormBuilder.createFormBuilder()
                .addComponent(projectNameLabel)
                .addVerticalGap(10)
                .addLabeledComponent(new JBLabel("Use project specific settings:"), useProjectSpecificSettingCheckBox, 1, false)
                .addVerticalGap(10)
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
        mainSettingsForm.setVisible(useProjectSpecificSetting);
        customSettingsForm.setVisible(useProjectSpecificSetting && showCustomSettings());
        useProjectSpecificSettingCheckBox.setSelected(useProjectSpecificSetting);
    }

    class CheckBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            mainSettingsForm.setVisible(useProjectSpecificSettingCheckBox.isSelected());
            customSettingsForm.setVisible(useProjectSpecificSettingCheckBox.isSelected() && showCustomSettings());
        }
    }

}
