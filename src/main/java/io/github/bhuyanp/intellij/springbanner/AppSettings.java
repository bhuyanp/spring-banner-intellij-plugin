// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package io.github.bhuyanp.intellij.springbanner;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import io.github.bhuyanp.intellij.springbanner.theme.THEME_OPTION;
import io.github.bhuyanp.intellij.springbanner.theme.Theme;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.EMPTY;
import static io.github.bhuyanp.intellij.springbanner.util.PluginConstants.RANDOM_FONT;


/*
 * Supports storing the application settings in a persistent way.
 * The {@link com.intellij.openapi.components.State State} and {@link Storage}
 * annotations define the name of the data and the filename where these persistent
 * application settings are stored.
 */

@Service(Service.Level.APP)
@State(
        name = "io.github.bhuyanp.intellij.springbootbanner.AppSettings",
        storages = @Storage("funky-banner-plugin.xml")
)
final class AppSettings
        implements PersistentStateComponent<AppSettings.State> {

    static class State {
        public boolean showBanner = true;
        @NonNls
        public String bannerText = EMPTY;
        @NonNls
        public THEME_OPTION selectedTheme = THEME_OPTION.SURPRISE_ME;
        @NonNls
        public String bannerFont = RANDOM_FONT;


        //Custom Theme Settings
        public boolean bannerFontBold = false;
        @NonNls
        public List<Integer> bannerFontColor = List.of(230, 230, 230); // Default Off White
        public boolean addBGColor = true;
        @NonNls
        public List<Integer> bannerBackground = List.of(45, 45, 45);  // Default dark gray
        @NonNls
        public Theme.ADDITIONAL_EFFECT additionalEffect = Theme.ADDITIONAL_EFFECT.NONE;


        public boolean showCaption = true;
        @NonNls
        public List<Integer> captionColor = List.of(0, 110, 110);
        public boolean showSpringVersionInCaption = true;
        public boolean showJDKVersionInCaption = true;
        public String captionText = EMPTY;
    }


    private State myState = new State();

    static AppSettings getInstance() {
        return ApplicationManager.getApplication()
                .getService(AppSettings.class);
    }

    @Override
    public State getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull State state) {
        myState = state;
    }

}
