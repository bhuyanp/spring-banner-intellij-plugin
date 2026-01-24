// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package io.github.bhuyanp.intellij.springbanner;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
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
        return isModified(globalSettingsComponent, globalSetting);
    }

    protected boolean isModified(GlobalComponent globalSettingsComponent, AppSettings.State globalSetting) {
        return globalSettingsComponent.getShowBanner() != globalSetting.showBanner ||
                !globalSettingsComponent.getBannerText().equalsIgnoreCase(globalSetting.bannerText) ||
                globalSettingsComponent.getTheme() != globalSetting.selectedTheme ||
                !globalSettingsComponent.getBannerFont().equalsIgnoreCase(globalSetting.bannerFont) ||
                globalSettingsComponent.getBannerFontBold() != globalSetting.bannerFontBold ||
                !matchColor(globalSettingsComponent.getBannerFontColor(), globalSetting.bannerFontColor) ||
                globalSettingsComponent.getAddBGColor() != globalSetting.addBGColor ||
                !matchColor(globalSettingsComponent.getBannerBackground(), globalSetting.bannerBackground) ||
                globalSettingsComponent.getAdditionalEffect() != globalSetting.additionalEffect ||
                globalSettingsComponent.getShowCaption() != globalSetting.showCaption ||
                globalSettingsComponent.getShowAppVersion() != globalSetting.showAppVersion ||
                globalSettingsComponent.getShowSpringVersion() != globalSetting.showSpringVersionInCaption ||
                globalSettingsComponent.getShowJDKVersion() != globalSetting.showJDKVersionInCaption ||
                globalSettingsComponent.getCaptionBulletStyle() != globalSetting.captionBulletStyle ||
                !globalSettingsComponent.getCaptionText().equalsIgnoreCase(globalSetting.captionText) ||
                !matchColor(globalSettingsComponent.getCaptionColor(), globalSetting.captionColor);
    }

    boolean matchColor(Color color, List<Integer> rgb) {
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

    protected void apply(GlobalComponent globalSettingsComponent, AppSettings.State settings) {
        settings.showBanner = globalSettingsComponent.getShowBanner();
        settings.bannerText = globalSettingsComponent.getBannerText();
        settings.selectedTheme = globalSettingsComponent.getTheme();
        settings.bannerFont = globalSettingsComponent.getBannerFont();
        settings.bannerFontBold = globalSettingsComponent.getBannerFontBold();
        settings.bannerFontColor = getColorRGB(globalSettingsComponent.getBannerFontColor());
        settings.addBGColor = globalSettingsComponent.getAddBGColor();
        settings.bannerBackground = getColorRGB(globalSettingsComponent.getBannerBackground());
        settings.additionalEffect = globalSettingsComponent.getAdditionalEffect();

        settings.showCaption = globalSettingsComponent.getShowCaption();
        settings.showAppVersion = globalSettingsComponent.getShowAppVersion();
        settings.showSpringVersionInCaption = globalSettingsComponent.getShowSpringVersion();
        settings.showJDKVersionInCaption = globalSettingsComponent.getShowJDKVersion();
        settings.captionBulletStyle = globalSettingsComponent.getCaptionBulletStyle();
        settings.captionText = globalSettingsComponent.getCaptionText();
        settings.captionColor = getColorRGB(globalSettingsComponent.getCaptionColor());
    }

    private List<Integer> getColorRGB(Color color) {
        return List.of(color.getRed(), color.getGreen(), color.getBlue());
    }

    private Color getColor(List<Integer> colorRGB) {
        return new Color(colorRGB.get(0), colorRGB.get(1), colorRGB.get(2));
    }

    @Override
    public void reset() {
        AppSettings.State globalSetting = getCurrentSettings();
        reset(globalSettingsComponent, globalSetting);
    }

    protected void reset(GlobalComponent globalSettingsComponent, AppSettings.State globalSetting) {
        globalSettingsComponent.setShowBanner(globalSetting.showBanner);
        globalSettingsComponent.setBannerText(globalSetting.bannerText);
        globalSettingsComponent.setTheme(globalSetting.selectedTheme);
        globalSettingsComponent.setBannerFont(globalSetting.bannerFont);
        globalSettingsComponent.setBannerFontBold(globalSetting.bannerFontBold);
        globalSettingsComponent.setBannerFontColor(getColor(globalSetting.bannerFontColor));
        globalSettingsComponent.setAddBGColor(globalSetting.addBGColor);
        globalSettingsComponent.setBannerBackground(getColor(globalSetting.bannerBackground));
        globalSettingsComponent.setAdditionalEffect(globalSetting.additionalEffect);

        globalSettingsComponent.setShowCaption(globalSetting.showCaption);
        globalSettingsComponent.setShowAppVersion(globalSetting.showAppVersion);
        globalSettingsComponent.setShowSpringVersion(globalSetting.showSpringVersionInCaption);
        globalSettingsComponent.setShowJDKVersion(globalSetting.showJDKVersionInCaption);
        globalSettingsComponent.setCaptionBulletStyle(globalSetting.captionBulletStyle);
        globalSettingsComponent.setCaptionText(globalSetting.captionText);
        globalSettingsComponent.setCaptionColor(getColor(globalSetting.captionColor));
    }

    @Override
    public void disposeUIResources() {
        globalSettingsComponent = null;
    }

}
