package com.michael.AuctionV2.domain.dtos.responses;

import com.michael.AuctionV2.domain.entities.enums.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PurchasedPlayer {
    private Integer playerId;
    private String name;
    private PlayerType playerType;
    private BigDecimal boughtFor;
    private Integer points;
    private Boolean isForeign;
    private Boolean isLegend;
    private Boolean isUncapped;

}
