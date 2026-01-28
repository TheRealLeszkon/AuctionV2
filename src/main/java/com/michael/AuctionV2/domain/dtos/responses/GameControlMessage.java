package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.entities.enums.GameStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GameControlMessage {
    private String message;
    private String command;
    private GameStatus gameStatus;
}
