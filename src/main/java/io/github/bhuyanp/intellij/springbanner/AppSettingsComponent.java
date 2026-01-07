// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package io.github.bhuyanp.intellij.springbanner;

import com.intellij.ui.ColorPicker;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.theme.THEME_OPTION;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collections;

import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.FONT_OPTIONS;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.RANDOM_FONT;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField bannerTextField = new JBTextField(20);
    private final ComboBox<String> themeComboBox = new ComboBox<>(new String[]{"Dark", "Light", "Surprise Me", "Custom"});
    private final ComboBox<String> bannerFontComboBox = new ComboBox<>(FONT_OPTIONS.toArray(new String[0]));

    private final JBLabel customTheme = new JBLabel("Custom Theme Settings:");
    private final JBCheckBox bannerFontBold = new JBCheckBox("Bold");
    private final JButton bannerFontColorSelector = new JButton("        ");
    private final JButton bannerBackgroundColorSelector = new JButton("        ");
    private final ComboBox<Theme.ADDITIONAL_EFFECT> additionalEffectComboBox = new ComboBox<>(Theme.ADDITIONAL_EFFECT.values());

    private final JBLabel bannerFontStyleLabel = new JBLabel("Banner font style:");
    private final JBLabel bannerFontColorLabel = new JBLabel("Banner font color:");
    private final JBLabel bannerBackgroundLabel = new JBLabel("Banner background:");
    private final JBLabel additionalEffectLabel = new JBLabel("Additional effect:");

    public AppSettingsComponent() {
        themeComboBox.addActionListener(new ThemeComboBoxActionListener());
        themeComboBox.setFocusable(false);
        bannerFontComboBox.setFocusable(false);
        bannerFontBold.setFocusable(false);
        additionalEffectComboBox.setFocusable(false);

        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Banner text:"), bannerTextField, 1, false)
                .addTooltip("Project name is used if left empty")
                .addVerticalGap(4)
                .addLabeledComponent(new JBLabel("Theme:"), themeComboBox, 1, false)
                .addVerticalGap(4)
                .addLabeledComponent(new JBLabel("Banner font:            "), bannerFontComboBox, 1, false)
                .addVerticalGap(10)
                .addSeparator()
                .addVerticalGap(10)
                .addComponent(customTheme)
                .addVerticalGap(5)
                .addLabeledComponent(bannerFontStyleLabel, bannerFontBold)
                .addVerticalGap(4)
                .addLabeledComponent(bannerFontColorLabel, bannerFontColorSelector)
                .addVerticalGap(4)
                .addLabeledComponent(bannerBackgroundLabel, bannerBackgroundColorSelector)
                .addVerticalGap(4)
                .addLabeledComponent(additionalEffectLabel, additionalEffectComboBox)
                .addComponentFillVertically(new JPanel(), 1)
                .getPanel();

        initBannerColorSelector(bannerFontColorSelector, "Banner Text");
        initBannerColorSelector(bannerBackgroundColorSelector, "Banner Background");

    }

    class ThemeComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (themeComboBox.getSelectedItem().equals("Custom")) {
                bannerCustomizationVisibility(true);
            } else {
                bannerCustomizationVisibility(false);
            }
        }

        private void bannerCustomizationVisibility(boolean visible) {
            customTheme.setVisible(visible);
            bannerFontBold.setVisible(visible);
            bannerFontColorSelector.setVisible(visible);
            bannerBackgroundColorSelector.setVisible(visible);
            bannerFontColorLabel.setVisible(visible);
            bannerBackgroundLabel.setVisible(visible);
            bannerFontStyleLabel.setVisible(visible);
            additionalEffectLabel.setVisible(visible);
            additionalEffectComboBox.setVisible(visible);
        }
    }


    private void initBannerColorSelector(JButton bannerCustomColorSelector, String title) {
        bannerCustomColorSelector.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        bannerCustomColorSelector.setFocusable(false);
        bannerCustomColorSelector.setBorder(BorderFactory.createSoftBevelBorder(1));
    }

    private void addActionListener(JButton bannerCustomColorSelector, String title, Color initialColor) {
        bannerCustomColorSelector.addActionListener(e -> {
            Color selectedColor = ColorPicker.showDialog(
                    myMainPanel,
                    "Pick "+ title +" Color",
                    initialColor,
                    true,
                    Collections.emptyList(),
                    true
            );
            if (selectedColor != null) {
                bannerCustomColorSelector.setBackground(selectedColor);
                bannerCustomColorSelector.setForeground(selectedColor);
                String rgbTextModified = getRGBText(selectedColor);
                bannerCustomColorSelector.setToolTipText(rgbTextModified);
            }
        });
    }

    private String getRGBText(Color color){
        if(color==null) return "";
        return "R:" +color.getRed() + " G:" + color.getGreen() + " B" + color.getBlue();

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
        if (bannerText != null) {
            return bannerText;
        }
        return ""; // default
    }

    public void setBannerText(@NotNull String newText) {
        bannerTextField.setText(newText);
    }

    @NotNull
    public THEME_OPTION getTheme() {
        String selected = (String) themeComboBox.getSelectedItem();
        if (selected != null) {
            THEME_OPTION theme = Arrays.stream(THEME_OPTION.values())
                    .filter(th -> th.getLabel().equalsIgnoreCase(selected))
                    .findFirst().get();
            return theme;

        }
        return THEME_OPTION.SURPRISE_ME; // default
    }

    public void setTheme(@NotNull THEME_OPTION theme) {
        themeComboBox.setSelectedItem(theme.getLabel());
    }

    @NotNull
    public String getBannerFont() {
        String selected = (String) bannerFontComboBox.getSelectedItem();
        if (selected != null) {
            return selected;
        }
        return RANDOM_FONT; // default
    }

    public void setBannerFont(@NotNull String bannerFont) {
        bannerFontComboBox.setSelectedItem(bannerFont);
    }

    public boolean getBannerFontBold() {
        return bannerFontBold.isSelected();
    }

    public void setBannerFontBold(boolean bold) {
        bannerFontBold.setSelected(bold);
    }

    public Color getBannerFontColor() {
        return bannerFontColorSelector.getBackground();
    }

    public void setBannerFontColor(Color color) {
        bannerFontColorSelector.setBackground(color);
        bannerFontColorSelector.setForeground(color);
        bannerFontColorSelector.setToolTipText(getRGBText(color));
        addActionListener(bannerFontColorSelector, "Banner Text", color);
    }

    public Color getBannerBackground() {
        return bannerBackgroundColorSelector.getBackground();
    }

    public void setBannerBackground(Color color) {
        bannerBackgroundColorSelector.setBackground(color);
        bannerBackgroundColorSelector.setForeground(color);
        bannerBackgroundColorSelector.setToolTipText(getRGBText(color));
        addActionListener(bannerBackgroundColorSelector, "Banner Background", color);
    }

    public void setAdditionalEffect(Theme.ADDITIONAL_EFFECT additionalEffect) {
        additionalEffectComboBox.setSelectedItem(additionalEffect);
    }

    public Theme.ADDITIONAL_EFFECT getAdditionalEffect() {
        return (Theme.ADDITIONAL_EFFECT) additionalEffectComboBox.getSelectedItem();
    }
}
