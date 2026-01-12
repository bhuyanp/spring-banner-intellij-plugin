// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package io.github.bhuyanp.intellij.springbanner;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Provides controller functionality for application settings.
 */
class GlobalConfigurable implements Configurable {

    private GlobalComponent globalSettingsComponent;

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return ResourceBundle.getBundle("messages.SpringBanner").getString("sbb.plugin.name");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return globalSettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        globalSettingsComponent = new GlobalComponent();
        return globalSettingsComponent.getPanel();
    }


    private AppSettings.Setting getCurrentSettings() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        AppSettings.Setting globalSetting = state.globalSetting;
        return globalSetting == null ? new AppSettings.Setting() : globalSetting;
    }
    @Override
    public boolean isModified() {
        AppSettings.Setting globalSetting = getCurrentSettings();
        return !globalSettingsComponent.getBannerText().equalsIgnoreCase(globalSetting.bannerText) ||
                globalSettingsComponent.getTheme() != globalSetting.selectedTheme ||
                !globalSettingsComponent.getBannerFont().equalsIgnoreCase(globalSetting.bannerFont) ||
                globalSettingsComponent.getBannerFontBold() != globalSetting.bannerFontBold ||
                !equals(globalSettingsComponent.getBannerFontColor(), globalSetting.bannerFontColor) ||
                globalSettingsComponent.getAddBGColor()!=globalSetting.addBGColor ||
                !equals(globalSettingsComponent.getBannerBackground(), globalSetting.bannerBackground) ||
                !globalSettingsComponent.getAdditionalEffect().equals(globalSetting.additionalEffect);
    }

    boolean equals(Color color, List<Integer> rgb) {
        if (color == null || rgb == null || rgb.size() != 3) return false;
        return color.getRed() == rgb.get(0)
                && color.getGreen() == rgb.get(1)
                && color.getBlue() == rgb.get(2);

    }

    @Override
    public void apply() {
        AppSettings.State state = Objects.requireNonNull(AppSettings.getInstance().getState());
        AppSettings.Setting globalSetting = getCurrentSettings();
        globalSetting.bannerText = globalSettingsComponent.getBannerText();
        globalSetting.selectedTheme = globalSettingsComponent.getTheme();
        globalSetting.bannerFont = globalSettingsComponent.getBannerFont();
        globalSetting.bannerFontBold = globalSettingsComponent.getBannerFontBold();
        Color bannerFontColor = globalSettingsComponent.getBannerFontColor();
        Color bannerBackgroundCOLOR = globalSettingsComponent.getBannerBackground();
        globalSetting.bannerFontColor = List.of(bannerFontColor.getRed(), bannerFontColor.getGreen(), bannerFontColor.getBlue());
        globalSetting.addBGColor = globalSettingsComponent.getAddBGColor();
        globalSetting.bannerBackground = List.of(bannerBackgroundCOLOR.getRed(), bannerBackgroundCOLOR.getGreen(), bannerBackgroundCOLOR.getBlue());
        globalSetting.additionalEffect = globalSettingsComponent.getAdditionalEffect();
        state.globalSetting = globalSetting;
    }

    @Override
    public void reset() {
        AppSettings.Setting globalSetting = getCurrentSettings();
        globalSettingsComponent.setBannerText(globalSetting.bannerText);
        globalSettingsComponent.setTheme(globalSetting.selectedTheme);
        globalSettingsComponent.setBannerFont(globalSetting.bannerFont);
        globalSettingsComponent.setBannerFontBold(globalSetting.bannerFontBold);
        @NonNls List<Integer> bannerFontColor = globalSetting.bannerFontColor;
        @NonNls List<Integer> bannerBackground = globalSetting.bannerBackground;
        globalSettingsComponent.setBannerFontColor(new Color(bannerFontColor.get(0), bannerFontColor.get(1), bannerFontColor.get(2)));
        globalSettingsComponent.setAddBGColor(globalSetting.addBGColor);
        globalSettingsComponent.setBannerBackground(new Color(bannerBackground.get(0), bannerBackground.get(1), bannerBackground.get(2)));
        globalSettingsComponent.setAdditionalEffect(globalSetting.additionalEffect);
    }

    @Override
    public void disposeUIResources() {
        globalSettingsComponent = null;
    }

}
