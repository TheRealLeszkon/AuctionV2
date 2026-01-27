package com.michael.AuctionV2.domain.dtos.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebSocketEvent<T> {
    private WSEvent eventType;
    private Instant timestamp;
    private T payload;
}
