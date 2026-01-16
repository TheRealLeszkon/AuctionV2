package com.michael.AuctionV2.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetPlayerDTO {
    private Integer setId;
    private Integer playerId;
    private Integer points;
    private BigDecimal price;
    private Integer order;
}
