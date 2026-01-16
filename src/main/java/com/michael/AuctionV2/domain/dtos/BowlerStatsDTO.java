package com.michael.AuctionV2.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BowlerStatsDTO {
    private Integer matches;
    private Integer wickets;
    private BigDecimal economy;
    private String bestFigure;
}
