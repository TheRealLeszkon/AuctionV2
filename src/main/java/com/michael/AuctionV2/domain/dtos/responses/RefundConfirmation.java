package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.entities.PlayerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundConfirmation {
    private String playerName;
    private PlayerStatus playerStatus;
    private String message;
}
