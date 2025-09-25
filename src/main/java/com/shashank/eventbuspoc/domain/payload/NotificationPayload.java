package com.shashank.eventbuspoc.domain.payload;
public record NotificationPayload(String subject, String message, String to, String channel) {}
