// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.

package io.github.bhuyanp.intellij.springbanner;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.ProjectManager;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;


/*
 * Supports storing the application settings in a persistent way.
 * The {@link com.intellij.openapi.components.State State} and {@link Storage}
 * annotations define the name of the data and the filename where these persistent
 * application settings are stored.
 */

@Service(Service.Level.PROJECT)
@State(
        name = "io.github.bhuyanp.intellij.springbootbanner.ProjectSettings",
        storages = @Storage("funky-banner-plugin.xml")
)
final class ProjectSettings
        implements PersistentStateComponent<ProjectSettings.State> {

    static class State extends AppSettings.State{
        public boolean useProjectSpecificSetting = false;

    }

    private State myState = new State();



    static ProjectSettings getInstance(String projectName) {
        return Arrays.stream(Objects.requireNonNull(ProjectManager.getInstanceIfCreated()).getOpenProjects())
                .filter(project -> projectName.equalsIgnoreCase(project.getName()))
                .findAny()
                .orElseThrow()
                .getService(ProjectSettings.class);
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
