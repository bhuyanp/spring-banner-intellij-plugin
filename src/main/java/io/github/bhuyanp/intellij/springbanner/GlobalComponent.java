// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package io.github.bhuyanp.intellij.springbanner;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.ColorPanel;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import io.github.bhuyanp.intellij.springbanner.theme.CAPTION_BULLET_STYLE;
import io.github.bhuyanp.intellij.springbanner.theme.THEME_OPTION;
import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import io.github.bhuyanp.intellij.springbanner.util.ColorNameUtil;
import lombok.val;
import org.jdesktop.swingx.HorizontalLayout;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static io.github.bhuyanp.intellij.springbanner.generator.SpringCaptionGenerator.KEY_JDK_VERSION;
import static io.github.bhuyanp.intellij.springbanner.generator.SpringCaptionGenerator.KEY_SPRING_VERSION;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class GlobalComponent {

    static final int STANDARD_VERTICAL_GAP = 2;
    private static final int TEXT_AREA_PADDING = 10;
    private static final int tooltipOffset = 5;
    private static final String INDENT = SPACE.repeat(12);
    private static final int MAIN_FORM_LABEL_MAX_LENGTH = 26;
    private static final int SECONDARY_FORM_LABEL_MAX_LENGTH = MAIN_FORM_LABEL_MAX_LENGTH - 4;

    final JPanel myMainPanel;
    final JPanel mainSettingsForm;
    final JPanel customBannerSettingsForm;
    final JPanel customCaptionSettingsForm;

    final JBCheckBox showBannerCheckBox = new JBCheckBox("Generate Banner");
    private final JBTextField bannerTextField = new JBTextField(20);
    private final ComboBox<THEME_OPTION> themeComboBox = new ComboBox<>(THEME_OPTION.values());
    private final JBLabel themeTooltip = new JBLabel("", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);
    private final ComboBox<String> bannerFontComboBox = new ComboBox<>(FONT_OPTIONS.toArray(new String[0]));

    private final JBCheckBox bannerFontBoldCheckBox = new JBCheckBox("Bold");
    private final ColorPanel bannerFontColorPicker = new ColorPanel();
    private final JBLabel bannerFontColorTooltip = new JBLabel("", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);
    private final JBCheckBox addBGColorCheckBox = new JBCheckBox("");
    private final JBLabel bannerBGColorLabel = getLabelWithIndent("Background color:", SECONDARY_FORM_LABEL_MAX_LENGTH);
    private final ColorPanel bannerBGColorPicker = new ColorPanel();
    private final JBLabel bannerBGColorTooltip = new JBLabel("", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);
    private final ComboBox<Theme.ADDITIONAL_EFFECT> additionalEffectComboBox = new ComboBox<>(Theme.ADDITIONAL_EFFECT.values());

    final JBCheckBox showCaptionCheckBox = new JBCheckBox("Generate Caption");
    private final JBCheckBox showAppVersionCheckBox = new JBCheckBox();
    private final JBCheckBox showSpringVersionCheckBox = new JBCheckBox();
    private final JBCheckBox showJDKVersionCheckBox = new JBCheckBox();
    private final ComboBox<CAPTION_BULLET_STYLE> captionBulletComboBox = new ComboBox<>(CAPTION_BULLET_STYLE.values());

    private final JBTextArea captionTextArea = new JBTextArea(4, 20);


    private final JBLabel captionColorLabel = getLabelWithIndent("Caption color:", SECONDARY_FORM_LABEL_MAX_LENGTH);
    private final ColorPanel captionColorPicker = new ColorPanel();
    private final JBLabel captionColorTooltip = new JBLabel("", UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);

    public GlobalComponent() {
        showBannerCheckBox.addActionListener(new ShowBannerCheckBoxActionListener());
        themeComboBox.addActionListener(new ThemeComboBoxActionListener());
        themeComboBox.setLightWeightPopupEnabled(true);
        themeTooltip.setBorder(JBUI.Borders.emptyLeft(tooltipOffset));
        bannerFontComboBox.setLightWeightPopupEnabled(true);
        addBGColorCheckBox.addActionListener(new AddBGColorCheckBoxActionListener());
        makeComponentsNonFocusable(showBannerCheckBox, themeComboBox, bannerFontComboBox, bannerFontBoldCheckBox, additionalEffectComboBox, addBGColorCheckBox);

        mainSettingsForm = FormBuilder.createFormBuilder()
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Banner text:", MAIN_FORM_LABEL_MAX_LENGTH), bannerTextField)
                .addComponentToRightColumn(getToolTipWithOffset("Project name is used if left empty"))
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Theme:", MAIN_FORM_LABEL_MAX_LENGTH), themeComboBox)
                .addComponentToRightColumn(themeTooltip)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Banner font:", MAIN_FORM_LABEL_MAX_LENGTH), bannerFontComboBox)
                .getPanel();


        val bannerFontColorPickerPanel = initBannerColorSelector(bannerFontColorPicker, bannerFontColorTooltip, new BannerFontColorPickerListener());
        val bannerBGColorPickerPanel = initBannerColorSelector(bannerBGColorPicker, bannerBGColorTooltip, new BannerBGColorPickerListener());

        customBannerSettingsForm = FormBuilder.createFormBuilder()
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Font weight:", SECONDARY_FORM_LABEL_MAX_LENGTH), bannerFontBoldCheckBox)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Text color:", SECONDARY_FORM_LABEL_MAX_LENGTH), bannerFontColorPickerPanel)
                .addComponentToRightColumn(bannerFontColorTooltip)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Use background:", SECONDARY_FORM_LABEL_MAX_LENGTH), addBGColorCheckBox)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(bannerBGColorLabel, bannerBGColorPickerPanel)
                .addComponentToRightColumn(bannerBGColorTooltip)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Additional effect:", SECONDARY_FORM_LABEL_MAX_LENGTH), additionalEffectComboBox)
                .getPanel();

        showCaptionCheckBox.addActionListener(new ShowCaptionCheckBoxActionListener());
        makeComponentsNonFocusable(showCaptionCheckBox, showSpringVersionCheckBox, showJDKVersionCheckBox);

        captionTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(JBColor.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(TEXT_AREA_PADDING, TEXT_AREA_PADDING, TEXT_AREA_PADDING, TEXT_AREA_PADDING)));

        val captionTextTooltip = getToolTipWithOffset("Freeform caption text. JDK and Spring Boot versions available as " + KEY_JDK_VERSION + " and " + KEY_SPRING_VERSION);
        val captionFontColorPickerPanel = initBannerColorSelector(captionColorPicker, captionColorTooltip, new CaptionColorPickerListener());

        customCaptionSettingsForm = FormBuilder.createFormBuilder()
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Show app version:", SECONDARY_FORM_LABEL_MAX_LENGTH - 2), showAppVersionCheckBox)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Show spring version:", SECONDARY_FORM_LABEL_MAX_LENGTH - 2), showSpringVersionCheckBox)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Show jdk version:", SECONDARY_FORM_LABEL_MAX_LENGTH - 2), showJDKVersionCheckBox)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Bullet style:", SECONDARY_FORM_LABEL_MAX_LENGTH - 2), captionBulletComboBox)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(getLabelWithIndent("Caption text:", SECONDARY_FORM_LABEL_MAX_LENGTH - 2), captionTextArea)
                .addComponentToRightColumn(captionTextTooltip)
                .addVerticalGap(STANDARD_VERTICAL_GAP)
                .addLabeledComponent(captionColorLabel, captionFontColorPickerPanel)
                .addComponentToRightColumn(captionColorTooltip)
                .getPanel();

        val verticalFlowLayout = new VerticalFlowLayout();
        myMainPanel = new JPanel(verticalFlowLayout);
        myMainPanel.add(new JPanel());// added gap
        myMainPanel.add(showBannerCheckBox);
        myMainPanel.add(mainSettingsForm);
        myMainPanel.add(customBannerSettingsForm);
        myMainPanel.add(new JPanel());// added gap
        myMainPanel.add(showCaptionCheckBox);
        myMainPanel.add(customCaptionSettingsForm);
    }

    private @NotNull JBLabel getLabelWithIndent(String labelText, int labelMaxLength) {
        String paddedLabel = String.format("%-" + labelMaxLength + "s", labelText);
        return new JBLabel(INDENT + paddedLabel);
    }

    private void makeComponentsNonFocusable(JComponent... jComponents) {
        Arrays.stream(jComponents).forEach(jComponent -> jComponent.setFocusable(false));
    }

    private @NotNull JBLabel getToolTipWithOffset(String text) {
        val showCaptionTooltip = new JBLabel(text, UIUtil.ComponentStyle.SMALL, UIUtil.FontColor.BRIGHTER);
        showCaptionTooltip.setBorder(JBUI.Borders.emptyLeft(tooltipOffset));
        return showCaptionTooltip;
    }


    private JPanel initBannerColorSelector(ColorPanel colorPanel, JBLabel tooltip, ActionListener actionListener) {
        colorPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        colorPanel.setFocusable(false);
        colorPanel.setOpaque(true);
        colorPanel.addActionListener(actionListener);
        tooltip.setBorder(JBUI.Borders.emptyLeft(tooltipOffset));

        JPanel colorPickerPanel = new JPanel(new HorizontalLayout());
        colorPickerPanel.add(colorPanel);
        return colorPickerPanel;
    }


    private void showBannerBGColorPicker(boolean show) {
        bannerBGColorLabel.setVisible(show);
        bannerBGColorPicker.setVisible(show);
        bannerBGColorTooltip.setVisible(show);
    }

    private void showCaptionColorPicker(boolean show) {
        captionColorPicker.setVisible(show);
        captionColorLabel.setVisible(show);
        captionColorTooltip.setVisible(show);
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return bannerTextField;
    }


    public boolean getShowBanner() {
        return showBannerCheckBox.isSelected();
    }

    public void setShowBanner(boolean showBanner) {
        showBannerCheckBox.setSelected(showBanner);
        mainSettingsForm.setVisible(showBanner);
        customBannerSettingsForm.setVisible(showBanner && showCustomSettings());
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
        THEME_OPTION selectedItem = (THEME_OPTION) themeComboBox.getSelectedItem();
        return null == selectedItem ? THEME_OPTION.SURPRISE_ME : selectedItem;
    }

    public void setTheme(@NotNull THEME_OPTION themeOption) {
        themeTooltip.setText(themeOption.getDescription());
        themeComboBox.setSelectedItem(themeOption);
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

    public boolean getShowCaption() {
        return showCaptionCheckBox.isSelected();
    }

    public void setShowCaption(boolean showCaption) {
        showCaptionCheckBox.setSelected(showCaption);
        customCaptionSettingsForm.setVisible(showCaption);
        showCaptionColorPicker(showCaption && showCustomSettings());
    }

    public boolean getShowAppVersion() {
        return showAppVersionCheckBox.isSelected();
    }

    public void setShowAppVersion(boolean show) {
        showAppVersionCheckBox.setSelected(show);
    }

    public boolean getShowSpringVersion() {
        return showSpringVersionCheckBox.isSelected();
    }

    public void setShowSpringVersion(boolean show) {
        showSpringVersionCheckBox.setSelected(show);
    }

    public boolean getShowJDKVersion() {
        return showJDKVersionCheckBox.isSelected();
    }

    public void setShowJDKVersion(boolean show) {
        showJDKVersionCheckBox.setSelected(show);
    }

    @NotNull
    public CAPTION_BULLET_STYLE getCaptionBulletStyle() {
        CAPTION_BULLET_STYLE selectedItem = (CAPTION_BULLET_STYLE) captionBulletComboBox.getSelectedItem();
        return null == selectedItem ? CAPTION_BULLET_STYLE.PIPE : selectedItem;
    }

    public void setCaptionBulletStyle(@NotNull CAPTION_BULLET_STYLE captionBulletStyle) {
        captionBulletComboBox.setSelectedItem(captionBulletStyle);
    }

    @NotNull
    public String getCaptionText() {
        return Optional.ofNullable(captionTextArea.getText()).orElse("");
    }

    public void setCaptionText(@NotNull String captionText) {
        captionTextArea.setText(captionText);
    }

    public Color getCaptionColor() {
        return captionColorPicker.getSelectedColor();
    }

    public void setCaptionColor(Color color) {
        captionColorTooltip.setText(ColorNameUtil.getColorNameFromColor(color));
        captionColorPicker.setSelectedColor(color);
    }

    boolean showCustomSettings() {
        return THEME_OPTION.CUSTOM == THEME_OPTION.value(Objects.requireNonNull(themeComboBox.getSelectedItem()).toString());
    }

    class ShowBannerCheckBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            boolean showBanner = showBannerCheckBox.isSelected();
            mainSettingsForm.setVisible(showBanner);
            customBannerSettingsForm.setVisible(showBanner && showCustomSettings());
        }
    }

    class ThemeComboBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            THEME_OPTION themeOption = THEME_OPTION.value(Objects.requireNonNull(themeComboBox.getSelectedItem()).toString());
            themeTooltip.setText(themeOption.getDescription());
            customBannerSettingsForm.setVisible(showCustomSettings());
            showCaptionColorPicker(showCaptionCheckBox.isSelected() && showCustomSettings());
        }
    }

    class AddBGColorCheckBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            showBannerBGColorPicker(addBGColorCheckBox.isSelected());
        }
    }

    class ShowCaptionCheckBoxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            customCaptionSettingsForm.setVisible(showCaptionCheckBox.isSelected());
            showCaptionColorPicker(showCaptionCheckBox.isSelected() && showCustomSettings());
        }
    }

    class BannerFontColorPickerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            assert bannerFontColorPicker.getSelectedColor() != null;
            bannerFontColorTooltip.setText(ColorNameUtil.getColorNameFromColor(bannerFontColorPicker.getSelectedColor()));
        }
    }

    class BannerBGColorPickerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            assert bannerBGColorPicker.getSelectedColor() != null;
            bannerBGColorTooltip.setText(ColorNameUtil.getColorNameFromColor(bannerBGColorPicker.getSelectedColor()));
        }
    }

    class CaptionColorPickerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            assert captionColorPicker.getSelectedColor() != null;
            captionColorTooltip.setText(ColorNameUtil.getColorNameFromColor(captionColorPicker.getSelectedColor()));
        }
    }
}
