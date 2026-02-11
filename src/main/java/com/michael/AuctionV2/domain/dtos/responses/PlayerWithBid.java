package com.michael.AuctionV2.domain.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerWithBid {
    private String name;
    private BigDecimal boughtFor;
}
