// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package io.github.bhuyanp.intellij.springbanner;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import io.github.bhuyanp.intellij.springbanner.theme.THEME_OPTION;
import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.util.ColorNameUtil;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.FONT_OPTIONS;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.RANDOM_FONT;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class GlobalComponent {

    final JPanel myMainPanel;
    final JPanel mainSettingsForm;
    final JPanel customSettingsForm;
    private final JBTextField bannerTextField = new JBTextField(20);
    private final ComboBox<String> themeComboBox = new ComboBox<>(Arrays.stream(THEME_OPTION.values()).map(Object::toString).toList().toArray(new String[0]));
    private final ComboBox<String> bannerFontComboBox = new ComboBox<>(FONT_OPTIONS.toArray(new String[0]));

    private final JBCheckBox bannerFontBoldCheckBox = new JBCheckBox("Bold");
    private final ColorPanel bannerFontColorPicker = new ColorPanel();
    private final JBLabel bannerFontColorTooltip = new JBLabel("", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);

    private final JBCheckBox addBGColorCheckBox = new JBCheckBox("");
    private final JBLabel bannerBGColorLabel = new JBLabel("Banner background:");
    private final ColorPanel bannerBGColorPicker = new ColorPanel();
    private final JBLabel bannerBGColorTooltip = new JBLabel("", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);

    private final ComboBox<Theme.ADDITIONAL_EFFECT> additionalEffectComboBox = new ComboBox<>(Theme.ADDITIONAL_EFFECT.values());


    private final JBLabel themeTooltip = new JBLabel("", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);

    private final int tooltipOffset = 5;
    public GlobalComponent() {
        val themeComboBoxActionListener = new ThemeComboBoxActionListener();
        themeComboBox.addActionListener(themeComboBoxActionListener);
        themeComboBox.setFocusable(false);
        themeComboBox.setLightWeightPopupEnabled(true);
        bannerFontComboBox.setFocusable(false);
        bannerFontComboBox.setLightWeightPopupEnabled(true);
        bannerFontBoldCheckBox.setFocusable(false);
        additionalEffectComboBox.setFocusable(false);
        val addBGColorCheckBoxActionListener = new GlobalComponent.AddBGColorCheckBoxActionListener();
        addBGColorCheckBox.addActionListener(addBGColorCheckBoxActionListener);
        addBGColorCheckBox.setFocusable(false);

        themeTooltip.setBorder(JBUI.Borders.emptyLeft(tooltipOffset));

        val bannerTextTooltip = new JBLabel("Project name is used if left empty", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);
        bannerTextTooltip.setBorder(JBUI.Borders.emptyLeft(tooltipOffset));

        mainSettingsForm = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Banner text:"), bannerTextField, 1, false)
                .addComponentToRightColumn(bannerTextTooltip)
                .addVerticalGap(4)
                .addLabeledComponent(new JBLabel("Theme:"), themeComboBox, 1, false)
                .addComponentToRightColumn(themeTooltip)
                .addVerticalGap(4)
                .addLabeledComponent(new JBLabel("Banner font:"), bannerFontComboBox, 1, false)
                .getPanel();



        bannerFontColorTooltip.setBorder(JBUI.Borders.emptyLeft(tooltipOffset));
        bannerBGColorTooltip.setBorder(JBUI.Borders.emptyLeft(tooltipOffset));

        customSettingsForm = FormBuilder.createFormBuilder()
                .addVerticalGap(10)
                .addSeparator()
                .addVerticalGap(10)
                .addComponent(new JBLabel("Custom Theme Settings:"))
                .addVerticalGap(5)
                .addLabeledComponent(new JBLabel("Font weight:"), bannerFontBoldCheckBox)
                .addVerticalGap(4)
                .addLabeledComponent(new JBLabel("Font color:"), bannerFontColorPicker)
                .addComponentToRightColumn(bannerFontColorTooltip)
                .addVerticalGap(4)
                .addLabeledComponent(new JBLabel("Add banner background:"), addBGColorCheckBox)
                .addVerticalGap(4)
                .addLabeledComponent(bannerBGColorLabel, bannerBGColorPicker)
                .addComponentToRightColumn(bannerBGColorTooltip)
                .addVerticalGap(4)
                .addLabeledComponent(new JBLabel("Additional effect:"), additionalEffectComboBox)
                .addComponentFillVertically(new JPanel(), 1)
                .getPanel();

        initBannerColorSelector(bannerFontColorPicker);
        initBannerColorSelector(bannerBGColorPicker);
        myMainPanel = new JPanel(new VerticalFlowLayout());
        myMainPanel.add(mainSettingsForm);
        myMainPanel.add(customSettingsForm);
    }


    private void initBannerColorSelector(ColorPanel colorPanel) {
        colorPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorPanel.setFocusable(false);
        colorPanel.setOpaque(true);
    }


    private void showBannerBGColorPicker(boolean show){
        bannerBGColorLabel.setVisible(show);
        bannerBGColorPicker.setVisible(show);
        bannerBGColorTooltip.setVisible(show);
    }


    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return bannerTextField;
    }

    @NotNull
    public String getBannerText() {
        String bannerText = bannerTextField.getText();
        return Optional.ofNullable(bannerText).orElse("");
    }

    public void setBannerText(@NotNull String newText) {
        bannerTextField.setText(newText);
    }

    @NotNull
    public THEME_OPTION getTheme() {
        String selected = (String) themeComboBox.getSelectedItem();
        return StringUtils.isEmpty(selected)?THEME_OPTION.SURPRISE_ME: THEME_OPTION.value(selected);
    }

    public void setTheme(@NotNull THEME_OPTION themeOption) {
        themeTooltip.setText(themeOption.getDescription());
        themeComboBox.setSelectedItem(themeOption.getLabel());
    }

    @NotNull
    public String getBannerFont() {
        return Objects.requireNonNullElse(bannerFontComboBox.getSelectedItem(), RANDOM_FONT).toString();
    }

    public void setBannerFont(@NotNull String bannerFont) {
        bannerFontComboBox.setSelectedItem(bannerFont);
    }

    public boolean getBannerFontBold() {
        return bannerFontBoldCheckBox.isSelected();
    }

    public void setBannerFontBold(boolean bold) {
        bannerFontBoldCheckBox.setSelected(bold);
    }

    public Color getBannerFontColor() {
        return bannerFontColorPicker.getSelectedColor();
    }

    public void setBannerFontColor(Color color) {
        bannerFontColorTooltip.setText(ColorNameUtil.getColorNameFromColor(color));
        bannerFontColorPicker.setSelectedColor(color);
    }

    public boolean getAddBGColor() {
        return addBGColorCheckBox.isSelected();
    }

    public void setAddBGColor(boolean addBGColor) {
        showBannerBGColorPicker(addBGColor);
        addBGColorCheckBox.setSelected(addBGColor);
    }

    public Color getBannerBackground() {
        return bannerBGColorPicker.getSelectedColor();
    }

    public void setBannerBackground(Color color) {
        bannerBGColorTooltip.setText(ColorNameUtil.getColorNameFromColor(color));
        bannerBGColorPicker.setSelectedColor(color);
    }

    public void setAdditionalEffect(Theme.ADDITIONAL_EFFECT additionalEffect) {
        additionalEffectComboBox.setSelectedItem(additionalEffect);
    }

    public Theme.ADDITIONAL_EFFECT getAdditionalEffect() {
        return (Theme.ADDITIONAL_EFFECT) additionalEffectComboBox.getSelectedItem();
    }

    boolean showCustomSettings(){
        return THEME_OPTION.CUSTOM==THEME_OPTION.value(Objects.requireNonNull(themeComboBox.getSelectedItem()).toString());
    }

    class ThemeComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            THEME_OPTION themeOption = THEME_OPTION.value(Objects.requireNonNull(themeComboBox.getSelectedItem()).toString());
            themeTooltip.setText(themeOption.getDescription());
            customSettingsForm.setVisible(THEME_OPTION.CUSTOM==themeOption);
        }
    }

    class AddBGColorCheckBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showBannerBGColorPicker(addBGColorCheckBox.isSelected());
        }
    }

}
