package com.michael.AuctionV2.domain.dtos.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidRequest {
//    Integer playerId;
    BigDecimal currentBid;
}
