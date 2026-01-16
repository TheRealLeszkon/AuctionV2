package com.michael.AuctionV2.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatsmanStatsDTO {
    private Integer runs;
    private Integer matches;
    private BigDecimal battingAvg;
    private BigDecimal strikeRate;
}
