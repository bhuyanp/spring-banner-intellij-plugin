package io.github.bhuyanp.intellij.springbanner;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import io.github.bhuyanp.intellij.springbanner.util.FunkyBannerBundle;

import static io.github.bhuyanp.intellij.springbanner.util.FunkyBannerBundle.*;

/**
 *
 *
 * @author <a href="mailto:prasanta.k.bhuyan@gmail.com">Prasanta Bhuyan</a>
 * @Date 1/26/26
 */
public class BannerNotifier {



    public static void notify(Project project, String content) {


        NotificationGroupManager.getInstance()
                .getNotificationGroup(BannerNotifier.class.getName())
                .createNotification(message("sbb.plugin.name"), content, NotificationType.INFORMATION)
                .notify(project);
    }

}
