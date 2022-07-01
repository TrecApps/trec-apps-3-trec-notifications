package com.trecapps.notifications.models;

import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class NotificationList {

    public NotificationList()
    {
        daysToKeep = 30;
        notificationsToStore = 100;
    }

    LinkedList<Notification> notifications;

    int daysToKeep;
    int notificationsToStore;
}
