package io.github.bhuyanp.intellij.springbanner;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import io.github.bhuyanp.intellij.springbanner.util.FunkyBannerBundle;
import org.jetbrains.annotations.NotNull;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/25/26
 */
public class GenerateBannerAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        assert project != null;
        BannerBuilder.INSTANCE.build(project, false, true);
    }

    // Override getActionUpdateThread() when you target 2022.3 or later!

}
