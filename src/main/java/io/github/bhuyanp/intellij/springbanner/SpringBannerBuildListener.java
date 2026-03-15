package io.github.bhuyanp.intellij.springbanner;

import com.intellij.compiler.server.BuildManagerListener;
import com.intellij.openapi.project.Project;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static io.github.bhuyanp.intellij.springbanner.ansi.Attribute.TEXT_COLOR;


/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/1/26
 */
@Slf4j
public class SpringBannerBuildListener implements BuildManagerListener {
    @Override
    public void buildStarted(@NotNull Project project, @NotNull UUID sessionId, boolean isAutomake) {
        BannerBuilder.INSTANCE.build(project, isAutomake, false);
    }
}
