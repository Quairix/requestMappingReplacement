package com.github.quairix.mappingreplacement;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class NotifyReplacement {
    public static void notifyInfo(@Nullable Project project, String title, String content, NotificationType type) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup("Replacement Notification Group")
                .createNotification(title, content, type)
                .notify(project);
    }
}
