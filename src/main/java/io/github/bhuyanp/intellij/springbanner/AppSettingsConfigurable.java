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
final class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because
    // this implementation is registered as an applicationConfigurable

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return ResourceBundle.getBundle("messages.SpringBanner").getString("sbb.plugin.name");
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        return !mySettingsComponent.getBannerText().equalsIgnoreCase(state.bannerText) ||
                mySettingsComponent.getTheme() != state.selectedTheme ||
                !mySettingsComponent.getBannerFont().equalsIgnoreCase(state.bannerFont) ||
                mySettingsComponent.getBannerFontBold() != state.bannerFontBold ||
                !equals(mySettingsComponent.getBannerFontColor(), state.bannerFontColor) ||
                !equals(mySettingsComponent.getBannerBackground(), state.bannerBackground) ||
                !mySettingsComponent.getAdditionalEffect().equals(state.additionalEffect);
    }

    private boolean equals(Color color, List<Integer> rgb) {
        if (color == null || rgb == null || rgb.size() != 3) return false;
        return color.getRed() == rgb.get(0)
                && color.getGreen() == rgb.get(1)
                && color.getBlue() == rgb.get(2);

    }

    @Override
    public void apply() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        state.bannerText = mySettingsComponent.getBannerText();
        state.selectedTheme = mySettingsComponent.getTheme();
        state.bannerFont = mySettingsComponent.getBannerFont();
        state.bannerFontBold = mySettingsComponent.getBannerFontBold();
        Color bannerFontColor = mySettingsComponent.getBannerFontColor();
        Color bannerBackgroundCOLOR = mySettingsComponent.getBannerBackground();
        state.bannerFontColor = List.of(bannerFontColor.getRed(), bannerFontColor.getGreen(), bannerFontColor.getBlue());
        state.bannerBackground = List.of(bannerBackgroundCOLOR.getRed(), bannerBackgroundCOLOR.getGreen(), bannerBackgroundCOLOR.getBlue());
        state.additionalEffect = mySettingsComponent.getAdditionalEffect();
    }

    @Override
    public void reset() {
        AppSettings.State state =
                Objects.requireNonNull(AppSettings.getInstance().getState());
        mySettingsComponent.setBannerText(state.bannerText);
        mySettingsComponent.setTheme(state.selectedTheme);
        mySettingsComponent.setBannerFont(state.bannerFont);
        mySettingsComponent.setBannerFontBold(state.bannerFontBold);
        @NonNls List<Integer> bannerFontColor = state.bannerFontColor;
        @NonNls List<Integer> bannerBackground = state.bannerBackground;
        mySettingsComponent.setBannerFontColor(new Color(bannerFontColor.get(0), bannerFontColor.get(1), bannerFontColor.get(2)));
        mySettingsComponent.setBannerBackground(new Color(bannerBackground.get(0), bannerBackground.get(1), bannerBackground.get(2)));
        mySettingsComponent.setAdditionalEffect(state.additionalEffect);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
