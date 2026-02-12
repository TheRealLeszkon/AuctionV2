package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.entities.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Selection {
    private List<PlayerWithBid> selectedPlayers;
    private List<PlayerWithBid> substitutes;
}
