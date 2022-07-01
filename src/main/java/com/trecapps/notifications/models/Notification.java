package com.trecapps.notifications.models;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Notification {
    String app;
    String message;
    OffsetDateTime made;
    List<String> sessionOmit;
    boolean read;
}
