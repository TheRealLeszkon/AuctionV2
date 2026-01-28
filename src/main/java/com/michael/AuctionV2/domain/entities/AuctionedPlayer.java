package com.michael.AuctionV2.domain.entities;

import com.michael.AuctionV2.domain.entities.enums.PlayerStatus;
import com.michael.AuctionV2.domain.entities.keys.AuctionedPlayerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "auctioned_players")
public class AuctionedPlayer {
    @EmbeddedId
    private AuctionedPlayerId auctionedPlayerId;
    private Integer teamId;
    @Enumerated(EnumType.STRING)
    private PlayerStatus playerStatus;
    private BigDecimal soldPrice;
}
