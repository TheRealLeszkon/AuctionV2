package com.michael.AuctionV2.domain.dtos;

import com.michael.AuctionV2.domain.entities.PlayerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchasedPlayer {
    private String name;
    private PlayerType playerType;
    private BigDecimal boughtFor;
    private Integer points;

}
