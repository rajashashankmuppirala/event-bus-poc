package com.shashank.eventbuspoc.domain.payload;
public record DataflowPayload(String flowName, String operation, String sourceSystem, String targetSystem) {}
