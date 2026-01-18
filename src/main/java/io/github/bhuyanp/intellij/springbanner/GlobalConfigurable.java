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

    private AppSettings.State getCurrentSettings() {
        return Objects.requireNonNull(AppSettings.getInstance().getState());
    }

    @Override
    public boolean isModified() {
        AppSettings.State globalSetting = getCurrentSettings();
        return isModified(globalSettingsComponent,globalSetting);
    }

    protected boolean isModified(GlobalComponent globalSettingsComponent, AppSettings.State globalSetting) {
        return !globalSettingsComponent.getBannerText().equalsIgnoreCase(globalSetting.bannerText) ||
                globalSettingsComponent.getTheme() != globalSetting.selectedTheme ||
                !globalSettingsComponent.getBannerFont().equalsIgnoreCase(globalSetting.bannerFont) ||
                globalSettingsComponent.getShowCaption() != globalSetting.showCaption ||
                globalSettingsComponent.getBannerFontBold() != globalSetting.bannerFontBold ||
                !equals(globalSettingsComponent.getBannerFontColor(), globalSetting.bannerFontColor) ||
                globalSettingsComponent.getAddBGColor() != globalSetting.addBGColor ||
                !equals(globalSettingsComponent.getBannerBackground(), globalSetting.bannerBackground) ||
                globalSettingsComponent.getAdditionalEffect() != globalSetting.additionalEffect;
    }

    boolean equals(Color color, List<Integer> rgb) {
        if (color == null || rgb == null || rgb.size() != 3) return false;
        return color.getRed() == rgb.get(0)
                && color.getGreen() == rgb.get(1)
                && color.getBlue() == rgb.get(2);

    }

    @Override
    public void apply() {
        AppSettings.State globalSetting = getCurrentSettings();
        apply(globalSettingsComponent, globalSetting);
    }

    protected void apply(GlobalComponent globalSettingsComponent, AppSettings.State settings){
        settings.bannerText = globalSettingsComponent.getBannerText();
        settings.selectedTheme = globalSettingsComponent.getTheme();
        settings.bannerFont = globalSettingsComponent.getBannerFont();
        settings.showCaption = globalSettingsComponent.getShowCaption();
        settings.bannerFontBold = globalSettingsComponent.getBannerFontBold();
        Color bannerFontColor = globalSettingsComponent.getBannerFontColor();
        Color bannerBackgroundCOLOR = globalSettingsComponent.getBannerBackground();
        settings.bannerFontColor = List.of(bannerFontColor.getRed(), bannerFontColor.getGreen(), bannerFontColor.getBlue());
        settings.addBGColor = globalSettingsComponent.getAddBGColor();
        settings.bannerBackground = List.of(bannerBackgroundCOLOR.getRed(), bannerBackgroundCOLOR.getGreen(), bannerBackgroundCOLOR.getBlue());
        settings.additionalEffect = globalSettingsComponent.getAdditionalEffect();
    }

    @Override
    public void reset() {
        AppSettings.State globalSetting = getCurrentSettings();
        reset(globalSettingsComponent, globalSetting);
    }

    protected void reset(GlobalComponent globalSettingsComponent, AppSettings.State globalSetting) {
        globalSettingsComponent.setBannerText(globalSetting.bannerText);
        globalSettingsComponent.setTheme(globalSetting.selectedTheme);
        globalSettingsComponent.setBannerFont(globalSetting.bannerFont);
        globalSettingsComponent.setShowCaption(globalSetting.showCaption);
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
