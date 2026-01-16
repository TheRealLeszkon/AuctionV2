package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.entities.BowlerStats;
import com.michael.AuctionV2.domain.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompletePlayerDTO {
    private PlayerDTO playerDTO;
    private SetPlayerDTO setPlayerDTO;
}
