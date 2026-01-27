package com.michael.AuctionV2.domain.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequest {
    private Integer playerId;
    private String teamAssociation;
    private BigDecimal  finalBid;
}
