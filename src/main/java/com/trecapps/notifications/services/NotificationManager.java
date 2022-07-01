package com.trecapps.notifications.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trecapps.notifications.models.Notification;
import com.trecapps.notifications.models.NotificationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;

@Service
public class NotificationManager {
    @Autowired
    NotificationStorageService notificationStorageService;

    private NotificationList cleanNotificationList(NotificationList list)
    {
        LinkedList<Notification> currentNotifications = list.getNotifications();
        LinkedList<Notification> newNotifications = new LinkedList<>();

        OffsetDateTime now = OffsetDateTime.now();

        for(Notification notification: currentNotifications)
        {
            long daysPast = ChronoUnit.DAYS.between(notification.getMade(), now);

            if(daysPast < list.getDaysToKeep())
                newNotifications.add(notification);
        }

        while(newNotifications.size() > list.getNotificationsToStore())
            newNotifications.remove(0);

        return new NotificationList(newNotifications, list.getDaysToKeep(), list.getNotificationsToStore());
    }

    public String notify(String userId, String message, String app, String... sessions)
    {
        try {
            NotificationList list = notificationStorageService.retrieveNotifications(userId);

            Notification newNotification = new Notification();
            newNotification.setMade(OffsetDateTime.now());
            newNotification.setApp(app);
            newNotification.setMessage(message);
            newNotification.setRead(false);
            newNotification.setSessionOmit(List.of(sessions));
            LinkedList<Notification> notifications = list.getNotifications();
            notifications.add(newNotification);

            notificationStorageService.saveNotifications(cleanNotificationList(list), userId);
            return "";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public NotificationList getNotifications(String userId, String sessionId, String app)
    {
        try {
            NotificationList list = notificationStorageService.retrieveNotifications(userId);
            LinkedList<Notification> notifications = list.getNotifications();
            LinkedList<Notification> filteredList = new LinkedList<>();
            for(Notification notification: notifications)
            {
                if(app.equals(notification.getApp()) && !notification.getSessionOmit().contains(sessionId))
                    filteredList.add(notification);
            }

            list.setNotifications(filteredList);
            return list;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void readNotifications(String userId, String app)
    {
        try
        {
            NotificationList list = notificationStorageService.retrieveNotifications(userId);
            for(Notification notification: list.getNotifications())
            {
                if(app.equals(notification.getApp()))
                    notification.setRead(true);
            }
            notificationStorageService.saveNotifications(cleanNotificationList(list), userId);
        }
        catch(JsonProcessingException e)
        {
            e.printStackTrace();
        }
    }
}
